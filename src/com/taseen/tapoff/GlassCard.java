package com.taseen.tapoff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;

// A wallpaper set's card, in the same language as the double-tap tile: the photo full-bleed under a dark fade,
// a glass icon chip, white title and count, and a glass open button.
final class GlassCard extends View {
    private static final float RATIO = 0.62f; // height / width

    private final float dp = getResources().getDisplayMetrics().density;
    private final Paint image = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private final Paint fadeLeft = new Paint(), fadeTop = new Paint();
    private final Paint glassFill = new Paint(Paint.ANTI_ALIAS_FLAG), glassLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint title = new Paint(Paint.ANTI_ALIAS_FLAG), sub = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path clip = new Path();
    private final Matrix crop = new Matrix();
    private final Drawable chevron;
    private final int placeholder;
    private Drawable icon;
    private Bitmap photo;
    private float dim;
    private String name = "", count = "";

    GlassCard(Context c) {
        super(c);
        glassFill.setColor(0x33FFFFFF);
        glassLine.setStyle(Paint.Style.STROKE);
        glassLine.setStrokeWidth(dp);
        glassLine.setColor(0x3DFFFFFF);
        title.setColor(0xFFFFFFFF);
        title.setTextSize(19 * dp);
        title.setTypeface(Ui.font(600));
        sub.setColor(0xCCFFFFFF);
        sub.setTextSize(13 * dp);
        sub.setTypeface(Ui.font(400));
        chevron = c.getDrawable(R.drawable.ic_chevron).mutate();
        placeholder = Ui.dark(c) ? 0xFF1B1C20 : 0xFF2A2B30;
        setClickable(true);
    }

    void bind(String name, String count, int iconRes) {
        this.name = name;
        this.count = count;
        icon = getContext().getDrawable(iconRes).mutate();
        icon.setTint(0xFFFFFFFF);
        setContentDescription(name + ", " + count);
        invalidate();
    }

    void setPhoto(Bitmap b) {
        if (b == photo) return;
        photo = b;
        updateCrop();
        invalidate();
    }

    // Darkens the card as it sinks into the deck; opaque, so cards behind never show through.
    void setDim(float d) {
        if (d == dim) return;
        dim = d;
        invalidate();
    }

    @Override protected void onMeasure(int wSpec, int hSpec) {
        int w = MeasureSpec.getSize(wSpec);
        setMeasuredDimension(w, Math.round(w * RATIO));
    }

    @Override protected void onSizeChanged(int w, int h, int ow, int oh) {
        clip.reset();
        clip.addRoundRect(new RectF(0, 0, w, h), 28 * dp, 28 * dp, Path.Direction.CW);
        fadeLeft.setShader(new LinearGradient(0, 0, w * 0.75f, 0, 0x99000000, 0x00000000, Shader.TileMode.CLAMP));
        fadeTop.setShader(new LinearGradient(0, 0, 0, h * 0.55f, 0x80000000, 0x00000000, Shader.TileMode.CLAMP));
        updateCrop();
    }

    private void updateCrop() {
        if (photo == null || getWidth() == 0) return;
        float s = Math.max((float) getWidth() / photo.getWidth(), (float) getHeight() / photo.getHeight());
        crop.setScale(s, s);
        crop.postTranslate((getWidth() - photo.getWidth() * s) / 2, (getHeight() - photo.getHeight() * s) / 2);
    }

    @Override protected void onDraw(Canvas c) {
        int w = getWidth(), h = getHeight();
        float pad = 18 * dp, chip = 48 * dp;
        c.save();
        c.clipPath(clip);
        if (photo != null) c.drawBitmap(photo, crop, image);
        else c.drawColor(placeholder);
        c.drawRect(0, 0, w, h, fadeLeft);
        c.drawRect(0, 0, w, h, fadeTop);

        // Icon chip, then the name and count beside it, as on the double-tap tile.
        float cx = pad + chip / 2, cy = pad + chip / 2;
        c.drawCircle(cx, cy, chip / 2, glassFill);
        c.drawCircle(cx, cy, chip / 2, glassLine);
        if (icon != null) {
            int is = Math.round(24 * dp);
            icon.setBounds(Math.round(cx - is / 2f), Math.round(cy - is / 2f), Math.round(cx + is / 2f), Math.round(cy + is / 2f));
            icon.draw(c);
        }
        float tx = pad + chip + 14 * dp;
        c.drawText(name, tx, cy - 2 * dp, title);
        c.drawText(count, tx, cy + 16 * dp, sub);

        // Glass open button, bottom right.
        float br = 22 * dp, bx = w - pad - br, by = h - pad - br;
        c.drawCircle(bx, by, br, glassFill);
        c.drawCircle(bx, by, br, glassLine);
        int cs = Math.round(20 * dp);
        chevron.setBounds(Math.round(bx - cs / 2f), Math.round(by - cs / 2f), Math.round(bx + cs / 2f), Math.round(by + cs / 2f));
        chevron.draw(c);

        if (dim > 0) c.drawColor(Math.round(dim * 255) << 24);
        c.restore();
    }

    @Override public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        animate().alpha(pressed ? 0.85f : 1f).setDuration(120).start();
    }
}
