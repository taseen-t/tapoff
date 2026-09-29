package com.taseen.tapoff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.view.View;

// A photo card with a big frosted-glass word across its bottom edge and glass chips on top.
final class GlassCard extends View {
    private static final float RATIO = 0.62f; // height / width

    private final float dp = getResources().getDisplayMetrics().density;
    private final Paint image = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private final Paint glass = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint tint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint edge = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint chipFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint chipLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint chipText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path clip = new Path();
    private final Matrix crop = new Matrix();
    private final int placeholder;
    private Bitmap photo, blurred;
    private float dim;
    private String word = "", left = "", right = "";

    GlassCard(Context c) {
        super(c);
        Typeface display = Typeface.create(Typeface.create("google-sans", Typeface.NORMAL), 700, false);
        for (Paint p : new Paint[] {glass, tint, edge}) p.setTypeface(display);
        tint.setColor(0x40FFFFFF);
        edge.setStyle(Paint.Style.STROKE);
        edge.setStrokeWidth(1.2f * dp);
        chipFill.setColor(0x2EFFFFFF);
        chipLine.setStyle(Paint.Style.STROKE);
        chipLine.setStrokeWidth(dp);
        chipLine.setColor(0x66FFFFFF);
        chipText.setColor(0xFFFFFFFF);
        chipText.setTextSize(12 * dp);
        chipText.setTypeface(Typeface.create(Typeface.create("google-sans", Typeface.NORMAL), 500, false));
        placeholder = Ui.dark(c) ? 0xFF1B1C20 : 0xFFE2E2E5;
        setClickable(true);
    }

    // Darkens the card as it sinks into the deck; opaque, so cards behind never show through.
    void setDim(float d) {
        if (d == dim) return;
        dim = d;
        invalidate();
    }

    void bind(String word, String left, String right) {
        this.word = word;
        this.left = left;
        this.right = right;
        setContentDescription(word + ", " + left);
        invalidate();
    }

    void setPhoto(Bitmap b) {
        photo = b;
        blurred = b == null ? null : blur(b);
        updateShaders();
        invalidate();
    }

    // Cheap blur: halve repeatedly with filtering, then let the shader scale it back up.
    private static Bitmap blur(Bitmap b) {
        Bitmap s = b;
        while (s.getWidth() > 48) s = Bitmap.createScaledBitmap(s, s.getWidth() / 2, Math.max(1, s.getHeight() / 2), true);
        return s;
    }

    @Override protected void onMeasure(int wSpec, int hSpec) {
        int w = MeasureSpec.getSize(wSpec);
        setMeasuredDimension(w, Math.round(w * RATIO));
    }

    @Override protected void onSizeChanged(int w, int h, int ow, int oh) {
        clip.reset();
        clip.addRoundRect(new RectF(0, 0, w, h), 28 * dp, 28 * dp, Path.Direction.CW);
        edge.setShader(new LinearGradient(0, h * 0.45f, 0, h, 0xB3FFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP));
        updateShaders();
    }

    private void updateShaders() {
        if (photo == null || getWidth() == 0) {
            glass.setShader(null);
            return;
        }
        float s = Math.max((float) getWidth() / photo.getWidth(), (float) getHeight() / photo.getHeight());
        crop.setScale(s, s);
        crop.postTranslate((getWidth() - photo.getWidth() * s) / 2, (getHeight() - photo.getHeight() * s) / 2);
        Matrix m = new Matrix(crop);
        m.preScale((float) photo.getWidth() / blurred.getWidth(), (float) photo.getHeight() / blurred.getHeight());
        BitmapShader shader = new BitmapShader(blurred, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(m);
        glass.setShader(shader);
    }

    @Override protected void onDraw(Canvas c) {
        int w = getWidth(), h = getHeight();
        c.save();
        c.clipPath(clip);
        if (photo != null) c.drawBitmap(photo, crop, image);
        else c.drawColor(placeholder);

        // The word fills the width and sinks past the bottom edge, filled with the blurred photo.
        tint.setTextSize(100);
        float size = 100 * (w - 28 * dp) / Math.max(1, tint.measureText(word));
        size = Math.min(size, h * 0.62f);
        for (Paint p : new Paint[] {glass, tint, edge}) p.setTextSize(size);
        float x = (w - tint.measureText(word)) / 2, y = h + size * 0.12f;
        if (photo != null) c.drawText(word, x, y, glass);
        c.drawText(word, x, y, tint);
        c.drawText(word, x, y, edge);

        chip(c, left, 14 * dp, false);
        chip(c, right, w - 14 * dp, true);
        if (dim > 0) c.drawColor(Math.round(dim * 255) << 24);
        c.restore();
    }

    private void chip(Canvas c, String text, float at, boolean alignRight) {
        if (text.isEmpty()) return;
        float pad = 12 * dp, ch = 28 * dp, cw = chipText.measureText(text) + 2 * pad, top = 14 * dp;
        float l = alignRight ? at - cw : at;
        RectF r = new RectF(l, top, l + cw, top + ch);
        c.drawRoundRect(r, ch / 2, ch / 2, chipFill);
        c.drawRoundRect(r, ch / 2, ch / 2, chipLine);
        Paint.FontMetrics fm = chipText.getFontMetrics();
        c.drawText(text, l + pad, top + ch / 2 - (fm.ascent + fm.descent) / 2, chipText);
    }

    @Override public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        animate().scaleX(pressed ? 0.97f : 1f).scaleY(pressed ? 0.97f : 1f).setDuration(120).start();
    }
}
