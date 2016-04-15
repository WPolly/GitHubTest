package com.xiaoshan.mytencentqq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoshan.mytencentqq.R;
import com.xiaoshan.mytencentqq.manager.XmppConnectionManager;
import com.xiaoshan.mytencentqq.utils.UIUtils;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xiaoshan on 2016/4/15.
 * 14:52
 */
public class ContactsExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private int mGroupCount;
    private ArrayList<RosterGroup> mRosterGroups;
    private Roster mRoster;

    public ContactsExpandableListAdapter(Context context) {
        this.mContext = context;
        mRoster = XmppConnectionManager.getInstance().getRoster();
        mGroupCount = mRoster.getGroupCount();
        Collection<RosterGroup> groups = mRoster.getGroups();
        mRosterGroups = new ArrayList<>(groups);
    }

    @Override
    public int getGroupCount() {
        return mGroupCount;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mRosterGroups.get(groupPosition).getEntryCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mRosterGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mRosterGroups.get(groupPosition).getEntries().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition * (groupPosition + 1);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_contact_group_list, parent, false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.mIvIndicator.setSelected(isExpanded);
        groupViewHolder.mTvCount.setText(UIUtils.getString(R.string.entry_count_in_group, mRosterGroups.get(groupPosition).getEntryCount()));
        groupViewHolder.mTvGroup.setText(mRosterGroups.get(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_contact_child_list, null);
            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        RosterEntry rosterEntry = mRosterGroups.get(groupPosition).getEntries().get(childPosition);
        String name = rosterEntry.getName();
        String user = rosterEntry.getUser();
        Presence presence = mRoster.getPresence(user);
        boolean available = presence.isAvailable();
        int drawableId = available ? R.mipmap.online : R.mipmap.offline;
        String status = available ? "在线" : "离线";
        childViewHolder.mTvName.setText(name);
        childViewHolder.mIvPresence.setImageResource(drawableId);
        childViewHolder.mTvPresence.setText(status);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ChildViewHolder {
        @InjectView(R.id.iv_avatar)
        ImageView mIvAvatar;
        @InjectView(R.id.tv_name)
        TextView mTvName;
        @InjectView(R.id.iv_online)
        ImageView mIvPresence;
        @InjectView(R.id.tv_presence)
        TextView mTvPresence;

        ChildViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class GroupViewHolder {
        @InjectView(R.id.iv_indicator)
        ImageView mIvIndicator;
        @InjectView(R.id.tv_group)
        TextView mTvGroup;
        @InjectView(R.id.tv_count)
        TextView mTvCount;

        GroupViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


}
