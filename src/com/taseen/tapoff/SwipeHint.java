package com.taseen.tapoff;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

// A hand swiping right to left, leaving a fading line behind it. Plays PASSES times, then calls done.
final class SwipeHint extends View {
    static final int PASSES = 2;
    static final long PASS_MS = 1100;

    private final float dp = getResources().getDisplayMetrics().density;
    private final Drawable hand = getContext().getDrawable(R.drawable.ic_hand).mutate();
    private final Drawable shadow = getContext().getDrawable(R.drawable.ic_hand).mutate();
    private final Paint trail = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float t; // 0..1 within the current pass

    SwipeHint(Context c, Runnable done) {
        super(c);
        shadow.setTint(0x59000000);
        trail.setStyle(Paint.Style.STROKE);
        trail.setStrokeCap(Paint.Cap.ROUND);
        trail.setStrokeWidth(4 * dp);
        ValueAnimator a = ValueAnimator.ofFloat(0f, 1f).setDuration(PASS_MS);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setRepeatCount(PASSES - 1);
        a.addUpdateListener(v -> {
            t = (float) v.getAnimatedValue();
            invalidate();
        });
        a.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(android.animation.Animator anim) { done.run(); }
        });
        a.setStartDelay(250);
        a.start();
    }

    @Override protected void onDraw(Canvas c) {
        float span = 70 * dp, cx = getWidth() / 2f, tipY = getHeight() * 0.3f;
        float startX = cx + span, x = startX - 2 * span * t;
        // Fades in at the start of each pass and out at the end.
        float fade = t < 0.15f ? t / 0.15f : t > 0.8f ? (1 - t) / 0.2f : 1f;

        trail.setShader(new LinearGradient(startX, 0, x, 0, 0x00FFFFFF, 0xCCFFFFFF, Shader.TileMode.CLAMP));
        trail.setAlpha((int) (255 * fade));
        if (startX - x > 1) c.drawLine(startX, tipY, x, tipY, trail);

        // The drawable's fingertip sits at (11.5, 6) of 24.
        float size = 44 * dp, left = x - size * 11.5f / 24, top = tipY - size * 6f / 24;
        int alpha = (int) (255 * fade);
        shadow.setBounds((int) (left + dp), (int) (top + 2 * dp), (int) (left + size + dp), (int) (top + size + 2 * dp));
        shadow.setAlpha(alpha);
        shadow.draw(c);
        hand.setBounds((int) left, (int) top, (int) (left + size), (int) (top + size));
        hand.setAlpha(alpha);
        hand.draw(c);
    }
}
