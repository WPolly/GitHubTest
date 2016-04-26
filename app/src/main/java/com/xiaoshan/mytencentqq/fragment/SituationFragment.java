package com.xiaoshan.mytencentqq.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.xiaoshan.mytencentqq.R;
import com.xiaoshan.mytencentqq.activity.LoginActivity;
import com.xiaoshan.mytencentqq.base.BaseFragment;
import com.xiaoshan.mytencentqq.manager.XmppConnectionManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by xiaoshan on 2016/4/10.
 * 21:39
 */
public class SituationFragment extends BaseFragment {
    @InjectView(R.id.iv_exit)
    ImageView mIvExit;

    @Override
    protected View initView() {
        View rootView = View.inflate(getActivity(), R.layout.fragment_situation, null);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.iv_exit)
    public void onClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示")
                .setMessage("确定要退出登录吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XmppConnectionManager.getInstance().logout();
                        dialog.dismiss();
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }
}
