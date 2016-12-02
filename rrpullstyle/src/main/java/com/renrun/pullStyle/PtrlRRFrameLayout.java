package com.renrun.pullStyle;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * author cyq
 * Date 2016/12/1
 *
 * 使用说明
 * 方式1：直接使用 RRPullHeadView  在使用的地方设置head
 *       final RRPullHeadView header = new RRPullHeadView(this);
 *       ptrFrame.setHeaderView(header);
 *       ptrFrame.addPtrUIHandler(header);
 * 布局文件使用内置的in.srain.cube.views.ptr.PtrFrameLayout类型
 *
 *
 * 方式2：使用定义好的com.renrun.onlywebview.PtrlRRFrameLayout
 * 这样就不用设置头部，省略每次同样的代码
 *
 */

public class PtrlRRFrameLayout extends PtrFrameLayout {
    public PtrlRRFrameLayout(Context context) {
        super(context);
        init();
    }

    public PtrlRRFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PtrlRRFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        RRPullHeadView mHeaderView = new RRPullHeadView(getContext());
        setHeaderView(mHeaderView);
        addPtrUIHandler(mHeaderView);
    }
}
