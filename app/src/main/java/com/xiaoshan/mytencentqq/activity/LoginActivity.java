package com.xiaoshan.mytencentqq.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoshan.mytencentqq.R;
import com.xiaoshan.mytencentqq.bean.MessageEventBean;
import com.xiaoshan.mytencentqq.config.Constants;
import com.xiaoshan.mytencentqq.factory.ThreadPoolFactory;
import com.xiaoshan.mytencentqq.manager.XmppConnectionManager;
import com.xiaoshan.mytencentqq.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {

    @InjectView(R.id.et_password)
    EditText mEtPassword;
    @InjectView(R.id.et_user_name)
    EditText mEtUserName;
    @InjectView(R.id.btn_login)
    Button mBtnLogin;
    @InjectView(R.id.tv_register)
    TextView mTvRegister;
    private XmppConnectionManager mXmppConnectionManager;
    private String mUsername;
    private String mPassword;
    private SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        mXmppConnectionManager = XmppConnectionManager.getInstance();

        mSp = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = mSp.getString(Constants.SP_KEY_USERNAME, "");
        mPassword = mSp.getString(Constants.SP_KEY_PASSWORD, "");
        mEtUserName.setText(mUsername);
        mEtUserName.setSelection(mUsername.length());
        mEtPassword.setText(mPassword);
        mEtPassword.setSelection(mPassword.length());
    }

    @OnClick({R.id.btn_login, R.id.tv_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                    login();
                break;
            case R.id.tv_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login() {
        mUsername = mEtUserName.getText().toString();
        mPassword = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(mUsername)) {
            mEtUserName.setError("用户名不能为空");
            return;
        }

        if (TextUtils.isEmpty(mPassword)) {
            mEtPassword.setError("密码不能为空");
            return;
        }
        mSp.edit().putString(Constants.SP_KEY_USERNAME, mUsername).apply();
        mSp.edit().putString(Constants.SP_KEY_PASSWORD, mPassword).apply();
        ThreadPoolFactory.getNormalThreadPool().getThreadPoolExecutor().execute(mLoginTask);
    }

    @Subscribe
    public void onMessageEvent(final MessageEventBean event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, event.message, Toast.LENGTH_SHORT).show();
                if (UIUtils.getString(R.string.login_succeed).equals(event.message)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private Runnable mLoginTask = new Runnable() {
        @Override
        public void run() {
            mXmppConnectionManager.login(mUsername, mPassword);
        }
    };

}
