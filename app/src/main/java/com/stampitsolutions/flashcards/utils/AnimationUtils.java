package com.stampitsolutions.flashcards.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class AnimationUtils {

    public static void flipCard(View card, Runnable midAnimationRunnable) {
        card.setPivotX(card.getWidth() / 2);
        card.setPivotY(card.getHeight() / 2);

        // First half of the flip animation with scaling
        ObjectAnimator firstHalfFlip = ObjectAnimator.ofFloat(card, "rotationY", 0f, 90f);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(card, "scaleX", 1f, 0.8f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(card, "scaleY", 1f, 0.8f);
        firstHalfFlip.setInterpolator(new AccelerateInterpolator());
        firstHalfFlip.setDuration(250);

        // Second half of the flip animation with scaling
        ObjectAnimator secondHalfFlip = ObjectAnimator.ofFloat(card, "rotationY", -90f, 0f);
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(card, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(card, "scaleY", 0.8f, 1f);
        secondHalfFlip.setInterpolator(new DecelerateInterpolator());
        secondHalfFlip.setDuration(250);

        AnimatorSet firstHalfAnimation = new AnimatorSet();
        firstHalfAnimation.playTogether(firstHalfFlip, scaleDownX, scaleDownY);

        AnimatorSet secondHalfAnimation = new AnimatorSet();
        secondHalfAnimation.playTogether(secondHalfFlip, scaleUpX, scaleUpY);

        firstHalfAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (midAnimationRunnable != null) {
                    midAnimationRunnable.run();
                }
                secondHalfAnimation.start();
            }
        });

        firstHalfAnimation.start();
    }
}
