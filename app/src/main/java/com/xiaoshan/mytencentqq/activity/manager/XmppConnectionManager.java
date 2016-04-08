package com.xiaoshan.mytencentqq.activity.manager;

import com.xiaoshan.mytencentqq.activity.bean.MessageEventBean;
import com.xiaoshan.mytencentqq.activity.config.Constants;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * Created by xiaoshan on 2016/4/8.
 * 14:41
 */
public class XmppConnectionManager {
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
            EventBus.getDefault().post(new MessageEventBean("登录成功"));
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new MessageEventBean("登录失败"));
        }
    }
}
