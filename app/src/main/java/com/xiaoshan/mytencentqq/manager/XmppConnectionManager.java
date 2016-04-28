package com.xiaoshan.mytencentqq.manager;

import com.xiaoshan.mytencentqq.R;
import com.xiaoshan.mytencentqq.bean.MessageEventBean;
import com.xiaoshan.mytencentqq.config.Constants;
import com.xiaoshan.mytencentqq.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by xiaoshan on 2016/4/8.
 * 14:41
 */
public class XmppConnectionManager {
    private static XmppConnectionManager ourInstance = null;
    private XMPPTCPConnection mXMPPTCPConnection;
    private Roster mRoster;

    public static XmppConnectionManager getInstance() {
        if (ourInstance == null) {
            synchronized (XmppConnectionManager.class) {
                if (ourInstance == null) {
                    ourInstance = new XmppConnectionManager();
                }
            }
        }
        return ourInstance;
    }

    private XmppConnectionManager() {
        initConnection();
    }

    private void initConnection() {
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        XMPPTCPConnectionConfiguration xmpptcpConnectionConfiguration = builder.setDebuggerEnabled(true)
                .setHost(Constants.HOST)
                .setServiceName("shange")//服务器名称!!
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .build();

        mXMPPTCPConnection = new XMPPTCPConnection(xmpptcpConnectionConfiguration);
        mXMPPTCPConnection.setUseStreamManagement(true);
    }

    public void register(String username, String password) {
        try {
            if (!mXMPPTCPConnection.isConnected()) {
                mXMPPTCPConnection.connect();
            }
            AccountManager accountManager = AccountManager.getInstance(mXMPPTCPConnection);
            accountManager.createAccount(username, password);
            EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.register_succeed)));
        } catch (SmackException | IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.register_failed)));
        } catch (XMPPException e) {
            if (e instanceof XMPPException.XMPPErrorException) {
                XMPPError.Condition condition = ((XMPPException.XMPPErrorException) e).getXMPPError().getCondition();
                if (condition == XMPPError.Condition.conflict) {
                    EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.user_exist)));
                }
            }
            EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.register_failed)));
        }
    }

    public void login(String username, String password) {
        try {
            if (!mXMPPTCPConnection.isConnected()) {
                mXMPPTCPConnection.connect();
            }
            mXMPPTCPConnection.login(username, password);
            EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.login_succeed)));
            mRoster = Roster.getInstanceFor(mXMPPTCPConnection);
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.login_failed)));
        }
    }

    public boolean isLogin() {
        return mXMPPTCPConnection.isAuthenticated();
    }

    public boolean addNewFriend(String name, String groupName) {
        if (searchUserInRoster(name)) {
            EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.user_already_in_roster)));
            return false;
        }

        if (!searchUserInServer(name)) {
            EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.user_not_exist)));
            return false;
        }
        try {
            RosterGroup group1 = mRoster.getGroup(groupName);
            if (group1 == null) {
                mRoster.createGroup(groupName);
            }
            mRoster.createEntry(name + "@" + mXMPPTCPConnection.getServiceName(), name, new String[]{groupName});
            EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.user_add_succeed)));
            return true;
        } catch (SmackException.NotLoggedInException |
                SmackException.NoResponseException |
                XMPPException.XMPPErrorException |
                SmackException.NotConnectedException e) {
            EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.user_add_failed)));
            e.printStackTrace();
            return false;
        }
    }

    public boolean addNewGroup(String groupName) {
        RosterGroup group1 = mRoster.getGroup(groupName);
        if (group1 != null) {
            EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(R.string.group_already_exist)));
            return false;
        }
        RosterGroup group = mRoster.createGroup(groupName);
        boolean result = group != null;
        int stringResId = result ? R.string.group_add_succeed : R.string.group_add_failed;
        EventBus.getDefault().post(new MessageEventBean(UIUtils.getString(stringResId)));
        return result;
    }

    private boolean searchUserInRoster(String name) {
        Set<RosterEntry> entries = mRoster.getEntries();
        for (RosterEntry next : entries) {
            String nextName = next.getName();
            if (nextName.equals(name)) {
                return true;
            }
        }

        return false;
    }

    private boolean searchUserInServer(String name) {
        UserSearchManager userSearchManager = new UserSearchManager(mXMPPTCPConnection);
        try {
            Form searchForm = userSearchManager.getSearchForm("search.shange");
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", name);
            ReportedData searchResults = userSearchManager.getSearchResults(answerForm, "search.shange");
            List<ReportedData.Row> rows = searchResults.getRows();
            for (ReportedData.Row row : rows) {
                List<String> usernames = row.getValues("Username");
                for (String username : usernames) {
                    if (username.equals(name)) {
                        return true;
                    }
                }
            }

        } catch (SmackException.NoResponseException |
                XMPPException.XMPPErrorException |
                SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Roster getRoster() {
        return mRoster;
    }

    public void logout() {
        mXMPPTCPConnection.disconnect();
        initConnection();
    }
}
