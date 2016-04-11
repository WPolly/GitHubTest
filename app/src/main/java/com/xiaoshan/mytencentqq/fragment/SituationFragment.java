package com.xiaoshan.mytencentqq.fragment;

import android.view.View;
import android.widget.TextView;

import com.xiaoshan.mytencentqq.base.BaseFragment;

/**
 * Created by xiaoshan on 2016/4/10.
 * 21:39
 */
public class SituationFragment extends BaseFragment {
    @Override
    protected View initView() {
        TextView tv = new TextView(getActivity());
        tv.setText("situation");
        return tv;
    }

    @Override
    protected void initData() {

    }
}
