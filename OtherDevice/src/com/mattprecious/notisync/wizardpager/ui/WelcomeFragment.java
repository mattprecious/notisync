
package com.mattprecious.notisync.wizardpager.ui;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.mattprecious.notisync.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.TextView;

public class WelcomeFragment extends Fragment {
    public static final String STATE_PLAY_ANIMATION = "playAnimation";

    ViewTreeObserver viewObserver;

    private TextView titleText;
    private TextView contentText;

    private boolean playAnimation = true;

    public static WelcomeFragment create() {
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_PLAY_ANIMATION)) {
            playAnimation = savedInstanceState.getBoolean(STATE_PLAY_ANIMATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wizard_welcome, container, false);

        titleText = (TextView) rootView.findViewById(android.R.id.title);
        contentText = (TextView) rootView.findViewById(R.id.content);

        viewObserver = rootView.getViewTreeObserver();
        viewObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                playAnimation();
                removeOnGlobalLayoutListener(this);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_PLAY_ANIMATION, playAnimation);
    }

    private synchronized void playAnimation() {
        if (!playAnimation) {
            return;
        }

        playAnimation = false;

        final int yShift = titleText.getHeight() * 3;
        ViewHelper.setAlpha(contentText, 0);
        animate(titleText)
                .alpha(0)
                .yBy(yShift)
                .setDuration(0)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        titleText.setVisibility(View.VISIBLE);
                        animate(titleText)
                                .alpha(1)
                                .yBy(-yShift)
                                .setDuration(1000)
                                .setListener(new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        animate(contentText)
                                                .alpha(1)
                                                .setDuration(1000)
                                                .setListener(new AnimatorListenerAdapter() {

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        contentText.setVisibility(View.VISIBLE);
                                                    }
                                                });
                                    }
                                });
                    }
                });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    private void removeOnGlobalLayoutListener(OnGlobalLayoutListener listener) {
        if (!viewObserver.isAlive()) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            viewObserver.removeGlobalOnLayoutListener(listener);
        } else {
            viewObserver.removeOnGlobalLayoutListener(listener);
        }
    }

}
