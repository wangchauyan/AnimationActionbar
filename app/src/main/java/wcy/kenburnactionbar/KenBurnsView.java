package wcy.kenburnactionbar;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;

public class KenBurnsView extends FrameLayout {

    private Handler mHandler = null;
    private int[] mResourceIds;
    private ImageView[] mImageViews;
    private int mActiveImageIndex = -1;

    private final Random random = new Random();
    private int mSwapMs = 10000;
    private int mFadeInOutMs = 400;

    private float maxScaleFactor = 1.5F;
    private float minScaleFactor = 1.2F;

    private Runnable mSwapImageRunnable = new Runnable() {
        @Override
        public void run() {
            if(mActiveImageIndex != -1) {
                swapImage();
            }
            mActiveImageIndex = (1 + mActiveImageIndex) % mImageViews.length;
            animate(mImageViews[mActiveImageIndex]);

            mHandler.postDelayed(mSwapImageRunnable, mSwapMs - mFadeInOutMs*2);
        }
    };

    public KenBurnsView(Context context) {
        this(context, null);
        if(mHandler == null)
            mHandler = new Handler();
    }

    public KenBurnsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        if(mHandler == null)
            mHandler = new Handler();
    }

    public KenBurnsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(mHandler == null)
            mHandler = new Handler();
    }

    public void setResourceIds(int... resourceIds) {
        mResourceIds = resourceIds;

        fillImageViews();
    }

    private void swapImage() {
        final ImageView nextImageView = mImageViews[(1 + mActiveImageIndex) % mImageViews.length];
        final ImageView prevImageView = mImageViews[mActiveImageIndex];
        nextImageView.setAlpha(0.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(mFadeInOutMs);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(prevImageView, "alpha", 1.0f, 0.0f),
                ObjectAnimator.ofFloat(nextImageView, "alpha", 0.0f, 1.0f)
        );
        animatorSet.start();
    }

    private void start(View view, long duration, float fromScale, float toScale, float fromTranslationX, float fromTranslationY, float toTranslationX, float toTranslationY) {
        view.setScaleX(fromScale);
        view.setScaleY(fromScale);
        view.setTranslationX(fromTranslationX);
        view.setTranslationY(fromTranslationY);
        ViewPropertyAnimator propertyAnimator = view.animate().translationX(toTranslationX).translationY(toTranslationY).scaleX(toScale).scaleY(toScale).setDuration(duration);
        propertyAnimator.start();
    }

    private float pickScale() {
        return this.minScaleFactor + this.random.nextFloat() * (this.maxScaleFactor - this.minScaleFactor);
    }

    private float pickTranslation(int value, float ratio) {
        return value * (ratio - 1.0f) * (this.random.nextFloat() - 0.5f);
    }

    public void animate(View view) {
        float fromScale = pickScale();
        float toScale = pickScale();
        float fromTranslationX = pickTranslation(view.getWidth(), fromScale);
        float fromTranslationY = pickTranslation(view.getHeight(), fromScale);
        float toTranslationX = pickTranslation(view.getWidth(), toScale);
        float toTranslationY = pickTranslation(view.getHeight(), toScale);
        start(view, this.mSwapMs, fromScale, toScale, fromTranslationX, fromTranslationY, toTranslationX, toTranslationY);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startKenBurnsAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(mSwapImageRunnable);
    }

    private void startKenBurnsAnimation() {
        mHandler.post(mSwapImageRunnable);
    }

    private void fillImageViews() {
        mImageViews = new ImageView[mResourceIds.length];
        for(int i = 0; i < mResourceIds.length; i++) {
            mImageViews[i] = new ImageView(getContext());
            mImageViews[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageViews[i].setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            this.addView(mImageViews[i]);
            if(i != 0) mImageViews[i].setAlpha(0.0F);
        }

        for (int i = 0; i < mImageViews.length; i++) {
            mImageViews[i].setImageResource(mResourceIds[i]);
        }
    }
}
