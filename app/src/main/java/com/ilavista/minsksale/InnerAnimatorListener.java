package com.ilavista.minsksale;

import android.animation.Animator;
import android.view.View;

class InnerAnimatorListener implements Animator.AnimatorListener {

    private View v;

    private int layerType;

    public InnerAnimatorListener(View v) {
        this.v = v;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        layerType = v.getLayerType();
        v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        v.setLayerType(layerType, null);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }
}