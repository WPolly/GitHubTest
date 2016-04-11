package com.xiaoshan.mytencentqq.manager;

import com.xiaoshan.mytencentqq.bean.MessageEventBean;
import com.xiaoshan.mytencentqq.config.Constants;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;

/**
 * Created by xiaoshan on 2016/4/8.
 * 14:41
 */
public class XmppConnectionManager {
    public static final String LOGIN_SUCCEED = "登录成功";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String USER_EXIST = "该用户名已存在";
    public static final String REGISTER_SUCCEED = "注册成功";
    public static final String REGISTER_FAILED = "注册失败";
    private static XmppConnectionManager ourInstance = null;
    private XMPPTCPConnection mXMPPTCPConnection;

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

    public void login(String username, String password) {
        try {
            if (!mXMPPTCPConnection.isConnected()) {
                mXMPPTCPConnection.connect();
            }
            mXMPPTCPConnection.login(username, password);
            EventBus.getDefault().post(new MessageEventBean(LOGIN_SUCCEED));
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new MessageEventBean(LOGIN_FAILED));
        }
    }

    public void register(String username, String password) {
        try {
            if (!mXMPPTCPConnection.isConnected()) {
                mXMPPTCPConnection.connect();
            }
            AccountManager accountManager = AccountManager.getInstance(mXMPPTCPConnection);
            accountManager.createAccount(username,password);
            EventBus.getDefault().post(new MessageEventBean(REGISTER_SUCCEED));
        } catch (SmackException | IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new MessageEventBean(REGISTER_FAILED));
        } catch (XMPPException e) {
            if (e instanceof XMPPException.XMPPErrorException) {
                XMPPError.Condition condition = ((XMPPException.XMPPErrorException) e).getXMPPError().getCondition();
                if (condition == XMPPError.Condition.conflict) {
                    EventBus.getDefault().post(new MessageEventBean(USER_EXIST));
                }
            }
            EventBus.getDefault().post(new MessageEventBean(REGISTER_FAILED));
        }
    }
}
