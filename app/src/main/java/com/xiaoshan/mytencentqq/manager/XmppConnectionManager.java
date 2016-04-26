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
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;

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

    public Roster getRoster() {
        return mRoster;
    }

    public void logout() {
        mXMPPTCPConnection.disconnect();
        initConnection();
    }
}
