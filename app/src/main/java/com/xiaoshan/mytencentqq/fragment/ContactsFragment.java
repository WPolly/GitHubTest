package com.xiaoshan.mytencentqq.fragment;

import android.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xiaoshan.mytencentqq.R;
import com.xiaoshan.mytencentqq.adapter.ContactsExpandableListAdapter;
import com.xiaoshan.mytencentqq.base.BaseFragment;
import com.xiaoshan.mytencentqq.manager.XmppConnectionManager;

import org.jivesoftware.smack.roster.RosterGroup;

import java.util.ArrayList;

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
    private ContactsExpandableListAdapter mAdapter;

    @Override
    protected View initView() {
        View rootView = View.inflate(getActivity(), R.layout.fragment_contacts, null);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    protected void initData() {
        mAdapter = new ContactsExpandableListAdapter(getActivity());
        mElvContacts.setAdapter(mAdapter);
    }

    @OnClick({R.id.tv_new_friend, R.id.tv_new_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_new_friend:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = View.inflate(getActivity(), R.layout.dialog_add_new_friend, null);
                final AlertDialog alertDialog = builder.setTitle("添加新好友")
                        .setView(dialogView)
                        .create();
                alertDialog
                        .show();
                final EditText etNewUserName = (EditText) dialogView.findViewById(R.id.et_new_user);
                final AutoCompleteTextView atvNewGroupName = (AutoCompleteTextView) dialogView.findViewById(R.id.atv_group_name);
                ArrayAdapter<String> arrayAdapter  = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,getGroupNames());
                atvNewGroupName.setAdapter(arrayAdapter);
                dialogView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XmppConnectionManager.getInstance().addNewFriend(etNewUserName.getText().toString(), atvNewGroupName.getText().toString());
                        mAdapter.initData();
                        mAdapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });
                break;
            case R.id.tv_new_group:
                break;
        }
    }

    private ArrayList<String> getGroupNames() {
        ArrayList<RosterGroup> rosterGroups = mAdapter.getRosterGroups();
        ArrayList<String> groupNames = new ArrayList<>();
        for (RosterGroup rosterGroup:rosterGroups) {
            String groupName = rosterGroup.getName();
            groupNames.add(groupName);
        }
        return groupNames;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
