package com.taseen.tapoff;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.LinearInterpolator;

// The hand from the swipe hint taps twice, and a small heart pops out and floats up: double-tap to save a favourite.
// Plays once, then calls done.
final class DoubleTapHint extends View {
    static final long MS = 1800;
    private static final float TAP1 = 0.16f, TAP2 = 0.34f, HEART = 0.42f;

    private final float dp = getResources().getDisplayMetrics().density;
    private final Drawable hand = getContext().getDrawable(R.drawable.ic_hand).mutate();
    private final Drawable shadow = getContext().getDrawable(R.drawable.ic_hand).mutate();
    private final Drawable heart = getContext().getDrawable(R.drawable.ic_heart_red).mutate();
    private final Paint ring = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float t; // 0..1 through the animation

    DoubleTapHint(Context c, Runnable done) {
        super(c);
        shadow.setTint(0x59000000);
        ring.setStyle(Paint.Style.STROKE);
        ring.setStrokeWidth(2.5f * dp);
        ring.setColor(0xFFFFFFFF);
        ValueAnimator a = ValueAnimator.ofFloat(0f, 1f).setDuration(MS);
        a.setInterpolator(new LinearInterpolator());
        a.addUpdateListener(v -> {
            t = (float) v.getAnimatedValue();
            invalidate();
        });
        a.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(android.animation.Animator anim) { done.run(); }
        });
        a.setStartDelay(150);
        a.start();
    }

    // 0 → 1 → 0 over a short press starting at `at`.
    private float press(float at) {
        float p = (t - at) / 0.08f;
        return p <= 0 || p >= 1 ? 0 : (float) Math.sin(Math.PI * p);
    }

    @Override protected void onDraw(Canvas c) {
        float tipX = getWidth() / 2f, tipY = getHeight() * 0.62f;
        float fade = t < 0.08f ? t / 0.08f : t > 0.86f ? (1 - t) / 0.14f : 1f;

        // A ripple from the fingertip for each tap.
        for (float at : new float[] {TAP1, TAP2}) {
            float p = (t - at) / 0.22f;
            if (p <= 0 || p >= 1) continue;
            ring.setAlpha((int) (200 * (1 - p)));
            c.drawCircle(tipX, tipY, (8 + 20 * p) * dp, ring);
        }

        // The heart pops above the fingertip, then floats up and fades.
        if (t > HEART) {
            float p = (t - HEART) / (1 - HEART);
            float scale = p < 0.15f ? 1.25f * p / 0.15f : p < 0.25f ? 1.25f - 0.25f * (p - 0.15f) / 0.1f : 1f;
            float size = 26 * dp * scale, y = tipY - 34 * dp - 70 * dp * p;
            heart.setAlpha((int) (255 * (p < 0.6f ? 1 : (1 - p) / 0.4f)));
            heart.setBounds((int) (tipX - size / 2), (int) (y - size / 2), (int) (tipX + size / 2), (int) (y + size / 2));
            heart.draw(c);
        }

        // The hand, pressing in a little on each tap. Its fingertip sits at (11.5, 6) of 24, like in SwipeHint.
        float size = 44 * dp * (1 - 0.1f * Math.max(press(TAP1), press(TAP2)));
        float left = tipX - size * 11.5f / 24, top = tipY - size * 6f / 24;
        int alpha = (int) (255 * fade);
        shadow.setBounds((int) (left + dp), (int) (top + 2 * dp), (int) (left + size + dp), (int) (top + size + 2 * dp));
        shadow.setAlpha(alpha);
        shadow.draw(c);
        hand.setBounds((int) left, (int) top, (int) (left + size), (int) (top + size));
        hand.setAlpha(alpha);
        hand.draw(c);
    }
}
