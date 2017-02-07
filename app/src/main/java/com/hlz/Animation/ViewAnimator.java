package com.hlz.Animation;

/**
 * Created by Hanlizhi on 2016/10/18.
 */

public class ViewAnimator implements ComponentViewAnimation.StateListener {

    private InitialCenterCircleView initialCenterCircleView;
    private RightCircleView rightCircleView;
    private SideArcsView sideArcsView;
    private TopCircleBorderView topCircleBorderView;
    private MainCircleView mainCircleView;
    private FinishedOkView finishedOkView;
    private FinishedFailureView finishedFailureView;
    private PercentIndicatorView percentIndicatorView;
    private AnimationState finishedState;
    private boolean resetAnimator;

    public void setComponentViewAnimations(InitialCenterCircleView initialCenterCircleView,
                                           RightCircleView rightCircleView, SideArcsView sideArcsView,
                                           TopCircleBorderView topCircleBorderView, MainCircleView mainCircleView,
                                           FinishedOkView finishedOkCircleView, FinishedFailureView finishedFailureView,
                                           PercentIndicatorView percentIndicatorView) {
        this.initialCenterCircleView = initialCenterCircleView;
        this.rightCircleView = rightCircleView;
        this.sideArcsView = sideArcsView;
        this.topCircleBorderView = topCircleBorderView;
        this.mainCircleView = mainCircleView;
        this.finishedOkView = finishedOkCircleView;
        this.finishedFailureView = finishedFailureView;
        this.percentIndicatorView = percentIndicatorView;
        initListeners();
    }

    private void initListeners() {
        initialCenterCircleView.setStateListener(this);
        rightCircleView.setStateListener(this);
        sideArcsView.setStateListener(this);
        topCircleBorderView.setStateListener(this);
        mainCircleView.setStateListener(this);
        finishedOkView.setStateListener(this);
        finishedFailureView.setStateListener(this);
    }

    public void startAnimator() {
        finishedState = null;
        initialCenterCircleView.showView();
        initialCenterCircleView.startTranslateTopAnimation();
        initialCenterCircleView.startScaleAnimation();
        rightCircleView.showView();
        rightCircleView.startSecondaryCircleAnimation();
    }

    public void resetAnimator() {
        initialCenterCircleView.hideView();
        rightCircleView.hideView();
        sideArcsView.hideView();
        topCircleBorderView.hideView();
        mainCircleView.hideView();
        finishedOkView.hideView();
        finishedFailureView.hideView();
        resetAnimator = true;
        startAnimator();
    }

    public void finishOk() {
        finishedState = AnimationState.FINISHED_OK;
    }

    public void finishFailure() {
        finishedState = AnimationState.FINISHED_FAILURE;
    }

    @Override
    public void onStateChanged(AnimationState state) {
        if (resetAnimator) {
            resetAnimator = false;
        } else {
            switch (state) {
                case MAIN_CIRCLE_TRANSLATED_TOP:
                    onMainCircleTranslatedTop();
                    break;
                case MAIN_CIRCLE_SCALED_DISAPPEAR:
                    onMainCircleScaledDisappear();
                    break;
                case MAIN_CIRCLE_FILLED_TOP:
                    onMainCircleFilledTop();
                    break;
                case SIDE_ARCS_RESIZED_TOP:
                    onSideArcsResizedTop();
                    break;
                case MAIN_CIRCLE_DRAWN_TOP:
                    onMainCircleDrawnTop();
                    break;
                case FINISHED_OK:
                    onFinished(state);
                    break;
                case FINISHED_FAILURE:
                    onFinished(state);
                    break;
                case MAIN_CIRCLE_TRANSLATED_CENTER:
                    onMainCircleTranslatedCenter();
                    break;
                default:
                    break;
            }
        }
    }

    private void onMainCircleTranslatedTop() {
        initialCenterCircleView.startTranslateBottomAnimation();
        initialCenterCircleView.startScaleDisappear();
    }

    private void onMainCircleScaledDisappear() {
        initialCenterCircleView.hideView();
        sideArcsView.showView();
        sideArcsView.startRotateAnimation();
        sideArcsView.startResizeDownAnimation();
    }

    private void onSideArcsResizedTop() {
        topCircleBorderView.showView();
        topCircleBorderView.startDrawCircleAnimation();
        sideArcsView.hideView();
    }

    private void onMainCircleDrawnTop() {
        mainCircleView.showView();
        mainCircleView.startFillCircleAnimation();
    }

    private void onMainCircleFilledTop() {
        if (isAnimationFinished()) {
            onStateChanged(finishedState);
            percentIndicatorView.startAlphaAnimation();
        } else {
            topCircleBorderView.hideView();
            mainCircleView.hideView();
            initialCenterCircleView.showView();
            initialCenterCircleView.startTranslateBottomAnimation();
            initialCenterCircleView.startScaleDisappear();
        }
    }

    private boolean isAnimationFinished() {
        return finishedState != null;
    }

    private void onFinished(AnimationState state) {
        topCircleBorderView.hideView();
        mainCircleView.hideView();
        finishedState = state;
        initialCenterCircleView.showView();
        initialCenterCircleView.startTranslateCenterAnimation();
    }

    private void onMainCircleTranslatedCenter() {
        if (finishedState == AnimationState.FINISHED_OK) {
            finishedOkView.showView();
            finishedOkView.startScaleAnimation();
        } else {
            finishedFailureView.showView();
            finishedFailureView.startScaleAnimation();
        }
        initialCenterCircleView.hideView();
    }
}
