package com.taseen.tapoff;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

// Colours and pieces shared by both screens. Page colours follow the light/dark setting; anything drawn over a
// photo (preview controls, card chips) is always light-on-dark glass.
final class Ui {
    static final int PHOTO_BG = 0xFF0A0A0B;
    enum Pill { PRIMARY, SECONDARY, ON_PHOTO }

    static boolean dark(Context c) {
        return (c.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    static int bg(Context c) { return dark(c) ? 0xFF0A0A0B : 0xFFF4F4F5; }

    static int surface(Context c) { return dark(c) ? 0x17FFFFFF : 0xFFFFFFFF; }

    static int line(Context c) { return dark(c) ? 0x2EFFFFFF : 0x14000000; }

    static int ink(Context c) { return dark(c) ? 0xFFF6F6F7 : 0xFF0D0E10; }

    static int muted(Context c) { return dark(c) ? 0xB3FFFFFF : 0xFF5E626A; }

    static int faint(Context c) { return dark(c) ? 0x80FFFFFF : 0xFF6B6F76; }

    static int dp(Context c, float v) {
        return Math.round(v * c.getResources().getDisplayMetrics().density);
    }

    static Typeface font(int weight) {
        return Typeface.create(Typeface.create("google-sans", Typeface.NORMAL), weight, false);
    }

    static GradientDrawable shape(Context c, float radiusDp, int fill, int stroke) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        if (stroke != 0) g.setStroke(dp(c, 1), stroke);
        g.setCornerRadius(dp(c, radiusDp));
        return g;
    }

    // Glass over a photo.
    static GradientDrawable glass(Context c, float radiusDp, int fill) {
        return shape(c, radiusDp, fill, 0x2EFFFFFF);
    }

    static TextView text(Context c, String s, float sp, int weight, int color) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(sp);
        t.setTypeface(font(weight));
        t.setTextColor(color);
        return t;
    }

    // PRIMARY inverts the page (near-black on light, near-white on dark); ON_PHOTO is always white.
    static Button pill(Context c, String label, Pill style) {
        Button b = new Button(c);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(15);
        b.setTypeface(font(600));
        b.setGravity(Gravity.CENTER);
        b.setMinHeight(dp(c, 52));
        b.setStateListAnimator(null);
        int fill, text;
        GradientDrawable bg;
        if (style == Pill.ON_PHOTO) {
            fill = 0xFFFFFFFF;
            text = 0xFF0A0A0B;
            bg = shape(c, 26, fill, 0);
        } else if (style == Pill.PRIMARY) {
            fill = ink(c);
            text = bg(c);
            bg = shape(c, 26, fill, 0);
        } else {
            text = ink(c);
            bg = shape(c, 26, dark(c) ? 0x1FFFFFFF : 0x0F000000, 0);
        }
        b.setTextColor(text);
        b.setBackground(new RippleDrawable(ColorStateList.valueOf(0x33808080), bg, null));
        return b;
    }

    private static final android.animation.ArgbEvaluator ARGB = new android.animation.ArgbEvaluator();

    // A colour part-way between two others, alpha included.
    static int blend(int from, int to, float f) {
        return (int) ARGB.evaluate(f, from, to);
    }
}
