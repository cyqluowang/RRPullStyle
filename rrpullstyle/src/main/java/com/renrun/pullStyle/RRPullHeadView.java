package com.renrun.pullStyle;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socks.library.KLog;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * author zhetengxiang
 * Date 2016/7/27
 * step0:下拉/准备下拉状态
 * step1:下拉->可刷新状态
 * step2:刷新状态
 * step3:刷新结束状态
 * step4:可刷新->下拉状态
 */

public class RRPullHeadView extends LinearLayout implements PtrUIHandler {

    private ImageView ivFirst;
    private ImageView ivSecond;
    private ImageView ivThird;
    private TextView tvMsg;
    private Matrix mMatrix = new Matrix();

    private AnimationDrawable mSecondAnimation;
    private AnimationDrawable mThirdAnimation;

    public RRPullHeadView(Context context) {
        this(context, null);
    }

    public RRPullHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RRPullHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View headerView = LayoutInflater.from(context).inflate(R.layout.header_renrun, this);
        ivFirst =(ImageView) headerView.findViewById(R.id.ivFirst);
        ivSecond =(ImageView) headerView.findViewById(R.id.ivSecond);
        ivThird =(ImageView) headerView.findViewById(R.id.ivThird);
        tvMsg =(TextView) headerView.findViewById(R.id.tvMsg);
        init();
    }

    private void init() {
        mSecondAnimation = (AnimationDrawable) ivSecond.getDrawable();
        mThirdAnimation = (AnimationDrawable) ivThird.getDrawable();
    }


    /**
     * 下拉的过程中，没有释放手
     * @param frame
     */
    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        KLog.e("onUIRefreshPrepare");
        pullStep0(0.0f);
    }

    /**
     * 下拉后手释放
     * @param frame
     */
    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        KLog.e("onUIRefreshBegin");
        mSecondAnimation.start();
        pullStep2();
    }

    /**
     * 刷新完成
     * @param frame
     */
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        KLog.e("onUIRefreshComplete");
        cancelAnimationSecond();
        mThirdAnimation.start();
        pullStep3();
    }

    /**
     * 结束后重置状态
     * @param frame
     */
    @Override
    public void onUIReset(PtrFrameLayout frame) {
        KLog.e("onUIReset");
        resetView();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();


        /**
         * 刚开始下拉到下拉刷新设置的开始的距离这段路程
         */
        if(isUnderTouch && lastPos < mOffsetToRefresh && status == PtrFrameLayout.PTR_STATUS_PREPARE){
            float scale = lastPos / Float.valueOf(mOffsetToRefresh);
            pullStep0(scale);
        }

        /**
         * 手释放刷新的时候，可能下拉的距离比较远，这个时候没有开始走onUIRefreshBegin还。要到设定的距离才会走
         */
        if(currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh &&
                isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE){
            pullStep1(frame);
        }

    }

    /**
     * 放大图片
     * @param scale
     */
    private void pullStep0(float scale) {
        ivFirst.setVisibility(View.VISIBLE);
        ivSecond.setVisibility(View.GONE);
        ivThird.setVisibility(View.GONE);
        scaleImage(scale);
        tvMsg.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
    }

    private void pullStep1(PtrFrameLayout frame) {
        if (!frame.isPullToRefresh()) {
            ivFirst.setVisibility(View.GONE);
            ivSecond.setVisibility(View.VISIBLE);
            ivThird.setVisibility(View.GONE);
            tvMsg.setText(getResources().getString(R.string.cube_ptr_release_to_refresh));
        }
    }

    private void pullStep2() {
        ivFirst.setVisibility(View.GONE);
        ivSecond.setVisibility(View.VISIBLE);
        ivThird.setVisibility(View.GONE);
        tvMsg.setText(R.string.cube_ptr_refreshing);
    }

    private void pullStep3() {
        ivFirst.setVisibility(View.GONE);
        ivSecond.setVisibility(View.GONE);
        ivThird.setVisibility(View.VISIBLE);
        tvMsg.setText(getResources().getString(R.string.cube_ptr_refresh_complete));
    }



    private void scaleImage(float scale) {
        mMatrix.setScale(scale, scale, ivFirst.getWidth() / 2, ivFirst.getHeight() / 2);
        ivFirst.setImageMatrix(mMatrix);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resetView();
    }

    private void resetView() {
        cancelAnimations();
    }

    private void cancelAnimations() {
        cancelAnimationSecond();
        cancelAnimationThird();
    }

    private void cancelAnimationSecond() {
        if (mSecondAnimation != null && mSecondAnimation.isRunning()) {
            mSecondAnimation.stop();
        }
    }

    private void cancelAnimationThird() {
        if (mThirdAnimation != null && mThirdAnimation.isRunning()) {
            mThirdAnimation.stop();
        }
    }
}
