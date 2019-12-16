package com.shinkaryuk.passbook;

//вся анимация сделана по примеру из этой статьи https://habr.com/ru/company/nix/blog/369097/

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class FAB_Utils extends CoordinatorLayout.Behavior<FloatingActionButton> {

    Boolean isHideFAB = false;
    public FAB_Utils(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        if (dyConsumed > 0) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            int fab_bottomMargin = layoutParams.bottomMargin;
            child.animate().translationY(child.getHeight() + fab_bottomMargin).setInterpolator(new LinearInterpolator()).start();
            isHideFAB = true;
        } else if (dyConsumed < 0) {
            child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
            isHideFAB = false;
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes, int type) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && type == ViewCompat.TYPE_TOUCH
                && coordinatorLayout.getHeight() > child.getTop(); //не прячем FAB при скролировании, т.к. криво реагирует на Snackbar
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int type) {
        //nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL; //не прячем FAB при скролировании, т.к. криво реагирует на Snackbar
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency.getTranslationY() <= 0 || !isHideFAB) {
            float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
            //child.setTranslationY(translationY);

            child.animate().translationY(translationY).setInterpolator(new LinearInterpolator()).start();
        } else {
            if (isHideFAB) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
                int fab_bottomMargin = layoutParams.bottomMargin;
                float translationY = child.getHeight() + fab_bottomMargin;//Math.min(child.getHeight() + fab_bottomMargin, dependency.getTranslationY() - dependency.getHeight());
                //child.setTranslationY(translationY);

                child.animate().translationY(translationY).setInterpolator(new LinearInterpolator()).start();
            }
        }

        return true;
    }
}