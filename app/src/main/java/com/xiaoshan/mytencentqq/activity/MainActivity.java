package com.xiaoshan.mytencentqq.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xiaoshan.mytencentqq.R;
import com.xiaoshan.mytencentqq.fragment.ContactsFragment;
import com.xiaoshan.mytencentqq.fragment.ConversationFragment;
import com.xiaoshan.mytencentqq.fragment.SituationFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.main_toolbar)
    Toolbar mMainToolbar;
    @InjectView(R.id.fl_main_content)
    FrameLayout mFlMainContent;
    @InjectView(R.id.iv_conversation)
    ImageView mIvConversation;
    @InjectView(R.id.iv_contact)
    ImageView mIvContact;
    @InjectView(R.id.iv_situation)
    ImageView mIvSituation;
    private FragmentManager mSupportFragmentManager;
    private ConversationFragment mConversationFragment;
    private ContactsFragment mContactsFragment;
    private SituationFragment mSituationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mConversationFragment = new ConversationFragment();
        mContactsFragment = new ContactsFragment();
        mSituationFragment = new SituationFragment();
        mIvConversation.setSelected(true);
        mSupportFragmentManager = getSupportFragmentManager();
        mSupportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_main_content, mConversationFragment)
                .commit();
    }

    @OnClick({R.id.iv_conversation, R.id.iv_contact, R.id.iv_situation})
    public void onClick(View view) {
        mIvConversation.setSelected(view.getId() == R.id.iv_conversation);
        mIvContact.setSelected(view.getId() == R.id.iv_contact);
        mIvSituation.setSelected(view.getId() == R.id.iv_situation);
        FragmentTransaction fragmentTransaction = mSupportFragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.iv_conversation:
                fragmentTransaction.replace(R.id.fl_main_content, mConversationFragment);
                mMainToolbar.setTitle("消息");
                break;
            case R.id.iv_contact:
                fragmentTransaction.replace(R.id.fl_main_content, mContactsFragment);
                mMainToolbar.setTitle("联系人");
                break;
            case R.id.iv_situation:
                fragmentTransaction.replace(R.id.fl_main_content, mSituationFragment);
                mMainToolbar.setTitle("动态");
                break;
        }
        fragmentTransaction.commit();
    }
}
