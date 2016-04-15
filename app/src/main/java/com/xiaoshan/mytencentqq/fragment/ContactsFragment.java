package com.xiaoshan.mytencentqq.fragment;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xiaoshan.mytencentqq.R;
import com.xiaoshan.mytencentqq.adapter.ContactsExpandableListAdapter;
import com.xiaoshan.mytencentqq.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by xiaoshan on 2016/4/10.
 * 21:39
 */
public class ContactsFragment extends BaseFragment {
    @InjectView(R.id.tv_new_friend)
    TextView mTvNewFriend;
    @InjectView(R.id.tv_new_group)
    TextView mTvNewGroup;
    @InjectView(R.id.elv_contacts)
    ExpandableListView mElvContacts;

    @Override
    protected View initView() {
        View rootView = View.inflate(getActivity(), R.layout.fragment_contacts, null);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    protected void initData() {
        mElvContacts.setAdapter(new ContactsExpandableListAdapter(getActivity()));
    }

    @OnClick({R.id.tv_new_friend, R.id.tv_new_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_new_friend:
                break;
            case R.id.tv_new_group:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
