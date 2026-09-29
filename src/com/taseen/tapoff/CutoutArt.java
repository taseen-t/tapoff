package com.taseen.tapoff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.view.DisplayCutout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Wallpapers drawn around this phone's camera hole, at its exact position and the screen's exact size.
// Every design treats the hole as something: an event horizon, a galaxy's core, a flower's centre, a record's
// spindle, a golf cup, a donut's hole, a pupil, a keyhole, a bullseye, a lollipop's centre, a neon bulb, a tap.
final class CutoutArt {
    static final String SCHEME = "cutout:";
    private static final String[] NAMES = {"Gargantua", "Galaxy", "Sunflower", "Vinyl", "Hole in one", "Donut",
        "Iris", "Keyhole", "Bullseye", "Lollipop", "Neon", "Pixel pulse"};

    static Wallpapers.Collection collection() {
        List<Wallpapers.Item> items = new ArrayList<>();
        for (int i = 0; i < NAMES.length; i++)
            items.add(new Wallpapers.Item(NAMES[i], SCHEME + i, null, "Drawn around your camera", 0));
        return new Wallpapers.Collection("Cutout", "TapOff", items);
    }

    // A cover gets a made-up hole near the top of the card; a wallpaper gets the real one.
    static Bitmap render(Context c, Wallpapers.Item it, boolean cover, int w, int h) {
        float cx = w / 2f, cy = h * 0.22f, r = h * 0.07f;
        if (!cover) {
            RectF hole = hole(c);
            float dp = c.getResources().getDisplayMetrics().density;
            if (hole != null) {
                cx = hole.centerX();
                cy = hole.centerY();
                r = Math.max(hole.width(), hole.height()) / 2f;
            } else { // no camera hole: pretend there's one at the top centre
                cy = 24 * dp;
                r = 14 * dp;
            }
        }
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(b);
        switch (Integer.parseInt(it.full.substring(SCHEME.length()))) {
            case 0: gargantua(cv, w, h, cx, cy, r); break;
            case 1: galaxy(cv, w, h, cx, cy, r); break;
            case 2: sunflower(cv, w, h, cx, cy, r); break;
            case 3: vinyl(cv, w, h, cx, cy, r); break;
            case 4: golf(cv, w, h, cx, cy, r); break;
            case 5: donut(cv, w, h, cx, cy, r); break;
            case 6: iris(cv, w, h, cx, cy, r); break;
            case 7: keyhole(cv, w, h, cx, cy, r); break;
            case 8: bullseye(cv, w, h, cx, cy, r); break;
            case 9: lollipop(cv, w, h, cx, cy, r); break;
            case 10: neon(cv, w, h, cx, cy, r); break;
            default: pixelPulse(cv, w, h, cx, cy, r); break;
        }
        cv.drawCircle(cx, cy, r * 1.03f, fill(0xFF000000)); // the camera itself
        return b;
    }

    private static RectF hole(Context c) {
        try {
            DisplayCutout cut = c.getDisplay().getCutout();
            if (cut == null || cut.getCutoutPath() == null) return null;
            RectF r = new RectF();
            cut.getCutoutPath().computeBounds(r, true);
            return r.isEmpty() ? null : r;
        } catch (UnsupportedOperationException e) { // not an on-screen context
            return null;
        }
    }

    // ---- helpers ----

    private static Paint fill(int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        return p;
    }

    private static Paint stroke(int color, float width) {
        Paint p = fill(color);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(width);
        p.setStrokeCap(Paint.Cap.ROUND);
        return p;
    }

    private static int alpha(int a, int rgb) {
        return (Math.max(0, Math.min(255, a)) << 24) | (rgb & 0xFFFFFF);
    }

    private static void background(Canvas cv, int w, int h, int top, int bottom) {
        Paint p = new Paint();
        p.setShader(new LinearGradient(0, 0, 0, h, top, bottom, Shader.TileMode.CLAMP));
        cv.drawRect(0, 0, w, h, p);
    }

    private static void glow(Canvas cv, float x, float y, float radius, int inner, int outer) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setShader(new RadialGradient(x, y, radius, inner, outer, Shader.TileMode.CLAMP));
        cv.drawCircle(x, y, radius, p);
    }

    private static void stars(Canvas cv, int w, int h, int count, float u) {
        Random rnd = new Random(7); // the same sky every time
        Paint p = fill(0xFFFFFFFF);
        for (int i = 0; i < count; i++) {
            p.setAlpha(30 + rnd.nextInt(150));
            cv.drawCircle(rnd.nextFloat() * w, rnd.nextFloat() * h, u * (0.5f + rnd.nextFloat() * 1.3f), p);
        }
    }

    // ---- designs ----

    // A black hole seen almost edge-on: the disk glows brighter on the side turning towards us, and its far
    // side is bent up over the top by gravity.
    private static void gargantua(Canvas cv, int w, int h, float cx, float cy, float r) {
        float u = r / 30f, sh = Math.max(r * 1.1f, w * 0.085f); // the black hole's shadow, bigger than the camera
        background(cv, w, h, 0xFF020205, 0xFF090612);
        stars(cv, w, h, 420, u);
        glow(cv, cx, cy, w * 0.9f, 0x3DFF9A3C, 0x00000000);
        for (int k = 0; k < 24; k++) { // lensed far side, arching over the hole
            float rr = sh * (1.06f + k * 0.045f);
            Paint p = stroke(0xFFFFFFFF, sh * 0.07f);
            p.setShader(new LinearGradient(cx - rr, 0, cx + rr, 0,
                alpha((int) (240 * (1 - k / 24f)), 0xFFF1D0), alpha((int) (120 * (1 - k / 24f)), 0xFF8A3D), Shader.TileMode.CLAMP));
            cv.drawArc(cx - rr, cy - rr, cx + rr, cy + rr, 180, 180, false, p);
        }
        for (int k = 0; k < 10; k++) { // thin lower image
            float rr = sh * (1.04f + k * 0.03f);
            cv.drawArc(cx - rr, cy - rr, cx + rr, cy + rr, 20, 140, false,
                stroke(alpha((int) (120 * (1 - k / 10f)), 0xFFC98A), sh * 0.04f));
        }
        for (int k = 0; k < 44; k++) { // the disk, brighter on the side turning towards us
            float t = k / 44f, rx = sh * 1.25f + t * w * 0.85f, ry = rx * 0.1f;
            int hot = t < 0.25f ? 0xFFF4DE : t < 0.6f ? 0xFFB255 : 0xE5532A;
            Paint p = stroke(0xFFFFFFFF, sh * 0.085f);
            p.setShader(new LinearGradient(cx - rx, 0, cx + rx, 0,
                alpha((int) (255 * (1 - t)), hot), alpha((int) (80 * (1 - t)), hot), Shader.TileMode.CLAMP));
            cv.drawOval(cx - rx, cy - ry, cx + rx, cy + ry, p);
        }
        cv.drawCircle(cx, cy, sh * 1.02f, stroke(0xFFFFF3D6, sh * 0.05f)); // photon ring
        cv.drawCircle(cx, cy, sh, fill(0xFF000000));
    }

    // A two-armed spiral galaxy, tilted, with the camera as its bright core.
    private static void galaxy(Canvas cv, int w, int h, float cx, float cy, float r) {
        float u = r / 30f;
        background(cv, w, h, 0xFF03040B, 0xFF0A0B1E);
        stars(cv, w, h, 260, u);
        Random rnd = new Random(11);
        float maxR = h * 0.95f;
        double tilt = Math.toRadians(-62);
        Paint p = fill(0);
        for (int pass = 0; pass < 2; pass++) { // soft nebula first, then sharp stars
            int count = pass == 0 ? 2200 : 9000;
            for (int i = 0; i < count; i++) {
                double t = Math.pow(rnd.nextFloat(), 0.8);
                double theta = (i % 2) * Math.PI + t * 3.4 * Math.PI + rnd.nextGaussian() * 0.22;
                double rad = r * 1.3 + t * maxR + rnd.nextGaussian() * r * (0.5 + t * 3);
                double x = Math.cos(theta) * rad, y = Math.sin(theta) * rad * 0.62;
                float px = cx + (float) (x * Math.cos(tilt) - y * Math.sin(tilt));
                float py = cy + (float) (x * Math.sin(tilt) + y * Math.cos(tilt));
                int col = rnd.nextFloat() < 0.03f ? 0xFF7AB8 : t < 0.22 ? 0xFFE7C2 : t < 0.5 ? 0xE9EEFF : 0x9CC4FF;
                if (pass == 0) {
                    p.setColor(alpha(10 + rnd.nextInt(14), col));
                    cv.drawCircle(px, py, u * (7 + rnd.nextFloat() * 9), p);
                } else {
                    p.setColor(alpha(70 + rnd.nextInt(185), col));
                    cv.drawCircle(px, py, u * (0.5f + rnd.nextFloat() * 1.5f), p);
                }
            }
        }
        glow(cv, cx, cy, w * 0.4f, 0x66FFD9A3, 0x00000000);
        glow(cv, cx, cy, w * 0.11f, 0xFFFFF6E6, 0x00FFE0B0);
    }

    // A sunflower head: seeds laid out on the golden angle, two rings of petals, the camera at its heart.
    private static void sunflower(Canvas cv, int w, int h, float cx, float cy, float r) {
        background(cv, w, h, 0xFF1B3A28, 0xFF0A1811);
        float head = r * 7.2f;
        for (int layer = 0; layer < 2; layer++) {
            int n = 30;
            float len = r * (layer == 0 ? 7.2f : 6.0f), wid = r * 1.7f;
            Paint petal = new Paint(Paint.ANTI_ALIAS_FLAG);
            petal.setShader(new LinearGradient(0, cy - head, 0, cy - head - len,
                layer == 0 ? 0xFFE09200 : 0xFFF2A900, 0xFFFFDB57, Shader.TileMode.CLAMP));
            for (int i = 0; i < n; i++) {
                cv.save();
                cv.rotate(i * 360f / n + layer * 180f / n, cx, cy);
                Path pt = new Path();
                pt.moveTo(cx, cy - head * 0.8f);
                pt.quadTo(cx + wid, cy - head - len * 0.45f, cx, cy - head - len);
                pt.quadTo(cx - wid, cy - head - len * 0.45f, cx, cy - head * 0.8f);
                cv.drawPath(pt, petal);
                cv.drawLine(cx, cy - head * 0.85f, cx, cy - head - len * 0.8f, stroke(0x33A86400, r * 0.06f));
                cv.restore();
            }
        }
        glow(cv, cx, cy, head * 1.05f, 0xFF3A2413, 0xFF24160B);
        int seeds = 1100;
        float spacing = (head - r * 1.4f) / (float) Math.sqrt(seeds);
        int[] browns = {0xFF2B190C, 0xFF4A2E16, 0xFF6B4521};
        for (int i = 1; i <= seeds; i++) {
            double a = i * 2.39996323; // the golden angle
            float d = r * 1.3f + spacing * (float) Math.sqrt(i);
            float x = cx + (float) Math.cos(a) * d, y = cy + (float) Math.sin(a) * d;
            float s = r * (0.09f + 0.13f * d / head);
            cv.drawCircle(x, y, s, fill(browns[i % 3]));
            cv.drawCircle(x - s * 0.3f, y - s * 0.3f, s * 0.35f, fill(0x33FFE2B0));
        }
    }

    // A record spinning on its spindle, grooves catching the light, the tonearm resting on it.
    private static void vinyl(Canvas cv, int w, int h, float cx, float cy, float r) {
        float u = r / 30f, R = w * 0.66f;
        background(cv, w, h, 0xFF17171A, 0xFF0A0A0C);
        glow(cv, cx, cy + r, R * 1.15f, 0x22FFFFFF, 0x00000000);
        glow(cv, cx, cy, R, 0xFF1C1C1F, 0xFF0C0C0E);
        Random rnd = new Random(3);
        for (float gr = r * 4.8f; gr < R * 0.975f; gr += u * 1.7f) {
            boolean gap = rnd.nextFloat() < 0.025f; // the quiet bands between tracks
            cv.drawCircle(cx, cy, gr, stroke(gap ? 0x18000000 : alpha(8 + rnd.nextInt(22), 0xFFFFFF), gap ? u * 2 : u * 0.6f));
        }
        Paint sheen = new Paint(Paint.ANTI_ALIAS_FLAG);
        sheen.setShader(new SweepGradient(cx, cy,
            new int[] {0x00FFFFFF, 0x26FFFFFF, 0x00FFFFFF, 0x00FFFFFF, 0x1CFFFFFF, 0x00FFFFFF, 0x00FFFFFF},
            new float[] {0.02f, 0.1f, 0.2f, 0.52f, 0.6f, 0.7f, 1f}));
        cv.drawCircle(cx, cy, R, sheen);
        cv.drawCircle(cx, cy, R, stroke(0x33FFFFFF, u));
        float label = r * 4.3f;
        glow(cv, cx, cy, label, 0xFFF05A36, 0xFFC9391A);
        cv.drawCircle(cx, cy, label * 0.92f, stroke(0x33000000, u));
        Paint text = fill(0xE6FFF4E8);
        text.setTextSize(r * 0.52f);
        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        text.setLetterSpacing(0.12f);
        Path ring = new Path();
        ring.addCircle(cx, cy, label * 0.7f, Path.Direction.CW);
        cv.drawTextOnPath("TAPOFF RECORDS · SIDE A · 33⅓ RPM · ", ring, 0, 0, text);
        cv.drawCircle(cx, cy, r * 1.35f, fill(0xFFCFCFD4)); // spindle collar
        float px = w * 0.9f, py = cy + R * 1.05f;
        double a = Math.toRadians(58);
        float nx = cx + (float) Math.cos(a) * R * 0.78f, ny = cy + (float) Math.sin(a) * R * 0.78f;
        cv.drawLine(px + u * 3, py + u * 5, nx + u * 3, ny + u * 5, stroke(0x55000000, r * 0.34f)); // shadow
        Paint arm = stroke(0xFFFFFFFF, r * 0.3f);
        arm.setShader(new LinearGradient(px, py, nx, ny, 0xFFE9E9EE, 0xFF8E8E96, Shader.TileMode.CLAMP));
        cv.drawLine(px, py, nx, ny, arm);
        cv.save();
        cv.rotate((float) Math.toDegrees(Math.atan2(ny - py, nx - px)), nx, ny);
        cv.drawRoundRect(nx - r * 0.4f, ny - r * 0.55f, nx + r * 1.3f, ny + r * 0.55f, r * 0.2f, r * 0.2f, fill(0xFF2A2A2E));
        cv.restore();
        cv.drawCircle(px, py, r * 1.6f, fill(0xFF2C2C31));
        cv.drawCircle(px, py, r * 0.9f, fill(0xFFB9B9C0));
    }

    // A putting green seen from above: the camera is the cup, the ball has rolled right to its lip.
    private static void golf(Canvas cv, int w, int h, float cx, float cy, float r) {
        float u = r / 30f;
        cv.drawColor(0xFF2F8A3A);
        cv.save();
        cv.rotate(-32, w / 2f, h / 2f);
        float band = w * 0.13f;
        for (float x = -h; x < w + h; x += band * 2)
            cv.drawRect(x, -h, x + band, h * 2, fill(0x1AFFFFFF));
        cv.restore();
        Paint vignette = new Paint();
        vignette.setShader(new RadialGradient(w / 2f, h * 0.4f, h * 0.75f, 0x00000000, 0x66000000, Shader.TileMode.CLAMP));
        cv.drawRect(0, 0, w, h, vignette);
        float bx = cx - r * 1.9f, by = cy + r * 2.4f, br = r * 0.95f;
        Path roll = new Path(); // the line the ball rolled along
        roll.moveTo(w * 0.12f, h * 0.9f);
        roll.cubicTo(w * 0.05f, h * 0.55f, cx - r * 9, cy + r * 9, bx, by);
        Paint trail = stroke(0x2EFFFFFF, br * 1.6f);
        cv.drawPath(roll, trail);
        Paint dash = stroke(0x55FFFFFF, u * 1.4f);
        dash.setPathEffect(new DashPathEffect(new float[] {u * 8, u * 10}, 0));
        cv.drawPath(roll, dash);
        cv.drawLine(cx, cy, cx + w * 0.42f, cy + h * 0.2f, stroke(0x40000000, u * 3)); // the flagstick's shadow
        Path flag = new Path();
        flag.moveTo(cx + w * 0.42f, cy + h * 0.2f);
        flag.lineTo(cx + w * 0.3f, cy + h * 0.2f + r * 1.4f);
        flag.lineTo(cx + w * 0.37f, cy + h * 0.17f);
        flag.close();
        cv.drawPath(flag, fill(0x33000000));
        cv.drawCircle(cx, cy, r * 1.45f, fill(0xFF1E5E27)); // shaded lip
        cv.drawCircle(cx, cy, r * 1.18f, fill(0xFFF4F4F2)); // cup liner
        glow(cv, bx + br * 0.4f, by + br * 0.5f, br * 1.6f, 0x66000000, 0x00000000);
        cv.drawCircle(bx, by, br, fill(0xFFFAFAF7));
        Paint dimple = fill(0x14000000);
        Random rnd = new Random(5);
        for (int i = 0; i < 26; i++) {
            double a = rnd.nextFloat() * Math.PI * 2, d = Math.sqrt(rnd.nextFloat()) * br * 0.8;
            cv.drawCircle(bx + (float) (Math.cos(a) * d), by + (float) (Math.sin(a) * d), br * 0.09f, dimple);
        }
        glow(cv, bx - br * 0.35f, by - br * 0.4f, br * 0.8f, 0x99FFFFFF, 0x00FFFFFF);
    }

    // A glazed donut from above, sprinkles and all; the camera is the hole.
    private static void donut(Canvas cv, int w, int h, float cx, float cy, float r) {
        float u = r / 30f;
        background(cv, w, h, 0xFFFFD9E5, 0xFFFFC2D6);
        Random dots = new Random(9);
        for (int i = 0; i < 60; i++)
            cv.drawCircle(dots.nextFloat() * w, dots.nextFloat() * h, u * (3 + dots.nextFloat() * 5), fill(0x22FFFFFF));
        float R = w * 0.44f, k = R / 6.4f, hole = Math.max(r * 1.9f, k * 1.9f); // k: the donut's own unit
        glow(cv, cx + u * 6, cy + u * 14, R * 1.12f, 0x44B0506A, 0x00B0506A); // shadow on the table
        Paint dough = new Paint(Paint.ANTI_ALIAS_FLAG);
        dough.setShader(new RadialGradient(cx, cy, R, new int[] {0xFFB87838, 0xFFE8B477, 0xFFD6995A, 0xFFB5723A},
            new float[] {hole / R, (hole + k) / R, 0.8f, 1f}, Shader.TileMode.CLAMP));
        cv.drawCircle(cx, cy, R, dough);
        Path icing = new Path(); // wavy outer edge, wavy inner edge
        Random wob = new Random(4);
        int n = 48;
        for (int i = 0; i <= n; i++) {
            double a = i * Math.PI * 2 / n;
            float rr = R * 0.86f + (float) Math.sin(i * 1.7) * k * 0.32f + wob.nextFloat() * k * 0.18f;
            float x = cx + (float) Math.cos(a) * rr, y = cy + (float) Math.sin(a) * rr;
            if (i == 0) icing.moveTo(x, y); else icing.lineTo(x, y);
        }
        icing.close();
        Path inner = new Path();
        for (int i = 0; i <= n; i++) {
            double a = i * Math.PI * 2 / n;
            float rr = hole + k * 0.45f + (float) Math.sin(i * 2.3) * k * 0.14f;
            float x = cx + (float) Math.cos(a) * rr, y = cy + (float) Math.sin(a) * rr;
            if (i == 0) inner.moveTo(x, y); else inner.lineTo(x, y);
        }
        inner.close();
        icing.op(inner, Path.Op.DIFFERENCE);
        Paint pink = new Paint(Paint.ANTI_ALIAS_FLAG);
        pink.setShader(new LinearGradient(cx - R, cy - R, cx + R, cy + R, 0xFFFF8FBC, 0xFFE8528F, Shader.TileMode.CLAMP));
        cv.drawPath(icing, pink);
        cv.save();
        cv.clipPath(icing);
        glow(cv, cx - R * 0.35f, cy - R * 0.4f, R * 0.55f, 0x55FFFFFF, 0x00FFFFFF);
        int[] colors = {0xFFFFFFFF, 0xFFFFE066, 0xFF6EE7F9, 0xFFA78BFA, 0xFF34D399, 0xFFF97316};
        Random rnd = new Random(12);
        for (int i = 0; i < 170; i++) {
            double a = rnd.nextFloat() * Math.PI * 2;
            float d = hole + k * 0.6f + rnd.nextFloat() * (R * 0.8f - hole - k * 0.6f);
            float x = cx + (float) Math.cos(a) * d, y = cy + (float) Math.sin(a) * d;
            cv.save();
            cv.rotate(rnd.nextFloat() * 180, x, y);
            cv.drawRoundRect(x - k * 0.3f, y - k * 0.09f, x + k * 0.3f, y + k * 0.09f, k * 0.09f, k * 0.09f,
                fill(colors[rnd.nextInt(colors.length)]));
            cv.restore();
        }
        cv.restore();
        glow(cv, cx, cy, hole, 0xFF6B3A17, 0xFFA8692F); // the hole's inner wall
    }

    // An iris around the camera as its pupil: fibres, a golden collarette, a dark rim and a catchlight.
    private static void iris(Canvas cv, int w, int h, float cx, float cy, float r) {
        float I = w * 0.3f, pupil = Math.max(r * 1.35f, I * 0.28f);
        background(cv, w, h, 0xFF0C0E12, 0xFF050608);
        glow(cv, cx, cy, I * 1.9f, 0x3321C4B8, 0x00000000);
        Paint base = new Paint(Paint.ANTI_ALIAS_FLAG);
        base.setShader(new RadialGradient(cx, cy, I, new int[] {0xFFD9A441, 0xFF2BB3A5, 0xFF12606F, 0xFF06141C},
            new float[] {pupil / I, pupil * 1.7f / I, 0.82f, 1f}, Shader.TileMode.CLAMP));
        cv.drawCircle(cx, cy, I, base);
        Random rnd = new Random(21);
        for (int i = 0; i < 520; i++) { // radial fibres
            double a = rnd.nextFloat() * Math.PI * 2;
            float from = pupil * (1.02f + rnd.nextFloat() * 0.3f), to = I * (0.55f + rnd.nextFloat() * 0.42f);
            int col = rnd.nextFloat() < 0.35f ? 0xF3D27A : rnd.nextFloat() < 0.5f ? 0x8FF0E6 : 0x0A3440;
            cv.drawLine(cx + (float) Math.cos(a) * from, cy + (float) Math.sin(a) * from,
                cx + (float) Math.cos(a) * to, cy + (float) Math.sin(a) * to,
                stroke(alpha(40 + rnd.nextInt(90), col), I * (0.004f + rnd.nextFloat() * 0.01f)));
        }
        cv.drawCircle(cx, cy, pupil * 1.45f, stroke(0x66F6C75A, pupil * 0.25f)); // collarette
        cv.drawCircle(cx, cy, I * 0.985f, stroke(0xFF041016, I * 0.05f)); // limbal ring
        cv.drawCircle(cx, cy, pupil, fill(0xFF000000));
        glow(cv, cx - I * 0.38f, cy - I * 0.36f, I * 0.16f, 0xDDFFFFFF, 0x00FFFFFF); // catchlight
    }

    // A wooden door; the camera is the top of the keyhole and warm light spills through the slot below it.
    private static void keyhole(Canvas cv, int w, int h, float cx, float cy, float r) {
        float k = w / 1080f, plank = w / 5f;
        int[] woods = {0xFF5B3A22, 0xFF4F321D, 0xFF624026, 0xFF553620, 0xFF4A2F1B};
        for (int i = 0; i < 5; i++) cv.drawRect(i * plank, 0, (i + 1) * plank, h, fill(woods[i]));
        Random rnd = new Random(8);
        for (int i = 0; i < 90; i++) { // grain
            float x = rnd.nextFloat() * w, len = h * (0.1f + rnd.nextFloat() * 0.4f), y = rnd.nextFloat() * h;
            Path g = new Path();
            g.moveTo(x, y);
            g.cubicTo(x + k * 14, y + len * 0.3f, x - k * 14, y + len * 0.6f, x + k * 4, y + len);
            cv.drawPath(g, stroke(alpha(20 + rnd.nextInt(30), 0x2A170A), k * (1 + rnd.nextFloat() * 2)));
        }
        for (int i = 1; i < 5; i++) cv.drawLine(i * plank, 0, i * plank, h, stroke(0x66000000, k * 4));
        float slotTop = cy + r * 0.6f, slotBottom = cy + r * 4.4f;
        Paint beam = new Paint(Paint.ANTI_ALIAS_FLAG); // light falling on the door below
        beam.setShader(new LinearGradient(0, slotBottom, 0, h * 0.75f, 0x55FFD27A, 0x00FFD27A, Shader.TileMode.CLAMP));
        Path cone = new Path();
        cone.moveTo(cx - r * 1.2f, slotBottom);
        cone.lineTo(cx + r * 1.2f, slotBottom);
        cone.lineTo(cx + w * 0.3f, h * 0.75f);
        cone.lineTo(cx - w * 0.3f, h * 0.75f);
        cone.close();
        cv.drawPath(cone, beam);
        RectF plate = new RectF(cx - w * 0.1f, cy - w * 0.14f, cx + w * 0.1f, slotBottom + w * 0.12f);
        glow(cv, plate.centerX() + k * 8, plate.centerY() + k * 14, plate.height() * 0.6f, 0x55000000, 0x00000000);
        Paint brass = new Paint(Paint.ANTI_ALIAS_FLAG);
        brass.setShader(new LinearGradient(plate.left, plate.top, plate.right, plate.bottom,
            new int[] {0xFFF2D38A, 0xFFB88A3A, 0xFFE7C271, 0xFF8C6424}, null, Shader.TileMode.CLAMP));
        cv.drawRoundRect(plate, w * 0.1f, w * 0.1f, brass);
        cv.drawRoundRect(plate, w * 0.1f, w * 0.1f, stroke(0x66FFF3C8, k * 3));
        for (float sy : new float[] {plate.top + w * 0.05f, plate.bottom - w * 0.05f}) { // screws
            cv.drawCircle(cx, sy, k * 12, fill(0xFF9C7430));
            cv.drawLine(cx - k * 8, sy, cx + k * 8, sy, stroke(0xFF5E4219, k * 3));
        }
        Path slot = new Path();
        slot.moveTo(cx - r * 0.55f, slotTop);
        slot.lineTo(cx + r * 0.55f, slotTop);
        slot.lineTo(cx + r * 0.9f, slotBottom);
        slot.lineTo(cx - r * 0.9f, slotBottom);
        slot.close();
        Paint light = new Paint(Paint.ANTI_ALIAS_FLAG);
        light.setShader(new LinearGradient(0, slotTop, 0, slotBottom, 0xFFFFF1C9, 0xFFFFB84D, Shader.TileMode.CLAMP));
        cv.drawPath(slot, light);
        glow(cv, cx, (slotTop + slotBottom) / 2, r * 3, 0x44FFD27A, 0x00FFD27A);
        cv.drawCircle(cx, cy, r * 1.25f, fill(0xFF3B2A12)); // keyhole's rim
    }

    // An archery target with the camera as the bullseye, and three arrows that nearly made it.
    private static void bullseye(Canvas cv, int w, int h, float cx, float cy, float r) {
        float R = w * 0.46f, k = w / 1080f;
        background(cv, w, h, 0xFF2A2F36, 0xFF15181C);
        glow(cv, cx + k * 10, cy + k * 24, R * 1.1f, 0x66000000, 0x00000000);
        int[] rings = {0xFFF2F2EE, 0xFFF2F2EE, 0xFF1D1D1F, 0xFF1D1D1F, 0xFF2E7BD6, 0xFF2E7BD6, 0xFFE0342F, 0xFFE0342F, 0xFFF7C928, 0xFFF7C928};
        for (int i = 0; i < 10; i++) {
            float rr = R * (1 - i / 10f);
            cv.drawCircle(cx, cy, rr, fill(rings[i]));
            cv.drawCircle(cx, cy, rr, stroke(i < 2 ? 0x33000000 : 0x33FFFFFF, k * 2));
        }
        cv.drawCircle(cx, cy, R * 0.05f, stroke(0x55000000, k * 2)); // the X ring
        float[][] hits = {{0.16f, 0.12f, 38}, {-0.2f, 0.19f, 128}, {0.05f, -0.24f, 64}};
        int[] feathers = {0xFFFF5B8A, 0xFF6EE7F9, 0xFFFFE066};
        for (int i = 0; i < 3; i++) {
            float hx = cx + hits[i][0] * R, hy = cy + hits[i][1] * R;
            double a = Math.toRadians(hits[i][2]);
            float len = w * 0.32f, ex = hx + (float) Math.cos(a) * len, ey = hy + (float) Math.sin(a) * len;
            cv.drawLine(hx + k * 6, hy + k * 10, ex + k * 6, ey + k * 10, stroke(0x55000000, k * 12)); // shadow
            cv.drawLine(hx, hy, ex, ey, stroke(0xFF8D6A45, k * 10));
            cv.save();
            cv.rotate((float) Math.toDegrees(a), ex, ey);
            for (int f = -1; f <= 1; f += 2) {
                Path fl = new Path();
                fl.moveTo(ex - k * 70, ey);
                fl.lineTo(ex - k * 10, ey + f * k * 26);
                fl.lineTo(ex + k * 10, ey + f * k * 26);
                fl.lineTo(ex - k * 20, ey);
                fl.close();
                cv.drawPath(fl, fill(feathers[i]));
            }
            cv.restore();
            cv.drawCircle(hx, hy, k * 9, fill(0xFF3A2A1A));
        }
    }

    // A swirl lollipop on its stick; the camera is the centre of the swirl.
    private static void lollipop(Canvas cv, int w, int h, float cx, float cy, float r) {
        float R = w * 0.34f, k = w / 1080f;
        background(cv, w, h, 0xFFCFF5EA, 0xFF9FE3D2);
        Paint stick = new Paint(Paint.ANTI_ALIAS_FLAG);
        stick.setShader(new LinearGradient(cx - k * 26, 0, cx + k * 26, 0, 0xFFE9E4DA, 0xFFFFFFFF, Shader.TileMode.MIRROR));
        cv.drawRect(cx + k * 14, cy + R * 0.8f, cx + k * 60, h, fill(0x22000000)); // stick shadow
        cv.drawRoundRect(cx - k * 26, cy + R * 0.5f, cx + k * 26, h + k * 40, k * 26, k * 26, stick);
        glow(cv, cx + k * 14, cy + k * 26, R * 1.12f, 0x44006B55, 0x00006B55);
        cv.drawCircle(cx, cy, R, fill(0xFFFFFFFF));
        int[] colors = {0xFFFF4F8B, 0xFF3CC8E8, 0xFFFFC53D};
        for (int arm = 0; arm < 6; arm++) {
            Path sp = new Path();
            for (int i = 0; i <= 120; i++) {
                float t = i / 120f;
                double a = arm * Math.PI / 3 + t * Math.PI * 3.2;
                float rr = r * 1.3f + t * (R - r * 1.3f);
                float x = cx + (float) Math.cos(a) * rr, y = cy + (float) Math.sin(a) * rr;
                if (i == 0) sp.moveTo(x, y); else sp.lineTo(x, y);
            }
            cv.drawPath(sp, stroke(colors[arm % 3], R * 0.11f));
        }
        cv.drawCircle(cx, cy, R, stroke(0x22000000, k * 3));
        glow(cv, cx - R * 0.35f, cy - R * 0.4f, R * 0.5f, 0x88FFFFFF, 0x00FFFFFF); // shine
    }

    // A neon ring around the camera and a "tap tap" sign glowing on a dark brick wall.
    private static void neon(Canvas cv, int w, int h, float cx, float cy, float r) {
        float k = w / 1080f;
        cv.drawColor(0xFF141019);
        float bh = k * 70, bw = k * 180;
        Paint mortar = stroke(0x33000000, k * 5);
        for (int row = 0; row * bh < h; row++) { // bricks
            float y = row * bh;
            cv.drawLine(0, y, w, y, mortar);
            for (float x = (row % 2) * bw / 2; x < w; x += bw) cv.drawLine(x, y, x, y + bh, mortar);
        }
        glow(cv, cx, cy + h * 0.12f, w * 0.8f, 0x33FF2E88, 0x00000000);
        neonRing(cv, cx, cy, r * 2.3f, 0xFF2E88, k);
        neonArc(cv, cx, cy, r * 4.2f, 0x28E0FF, k);
        Paint sign = new Paint(Paint.ANTI_ALIAS_FLAG);
        sign.setTypeface(Typeface.create("sans-serif", Typeface.BOLD_ITALIC));
        sign.setTextAlign(Paint.Align.CENTER);
        sign.setTextSize(w * 0.2f);
        float ty = cy + h * 0.3f;
        for (int pass = 0; pass < 3; pass++) {
            sign.setColor(pass == 2 ? 0xFFFFE3F1 : alpha(pass == 0 ? 60 : 140, 0xFF2E88));
            sign.setShadowLayer(k * (pass == 0 ? 60 : pass == 1 ? 24 : 6), 0, 0, 0xFFFF2E88);
            cv.drawText("tap tap", cx, ty, sign);
        }
    }

    private static void neonRing(Canvas cv, float cx, float cy, float rr, int rgb, float k) {
        cv.drawCircle(cx, cy, rr, stroke(alpha(40, rgb), k * 40));
        cv.drawCircle(cx, cy, rr, stroke(alpha(110, rgb), k * 16));
        cv.drawCircle(cx, cy, rr, stroke(alpha(255, rgb | 0x404040), k * 6));
    }

    private static void neonArc(Canvas cv, float cx, float cy, float rr, int rgb, float k) {
        RectF box = new RectF(cx - rr, cy - rr, cx + rr, cy + rr);
        cv.drawArc(box, 20, 140, false, stroke(alpha(40, rgb), k * 34));
        cv.drawArc(box, 20, 140, false, stroke(alpha(110, rgb), k * 14));
        cv.drawArc(box, 20, 140, false, stroke(alpha(255, rgb | 0x404040), k * 5));
    }

    // TapOff's own look: a dark LED wall with rings of light rippling out from the camera, as if just tapped.
    private static void pixelPulse(Canvas cv, int w, int h, float cx, float cy, float r) {
        float k = w / 1080f, step = k * 30, dot = k * 11, wave = k * 150;
        cv.drawColor(0xFF09090B);
        Paint p = fill(0);
        for (float y = step / 2; y < h; y += step)
            for (float x = step / 2; x < w; x += step) {
                float d = (float) Math.hypot(x - cx, y - cy);
                float ring = (float) Math.pow(0.5 + 0.5 * Math.cos(d / wave * Math.PI * 2), 6);
                float fade = Math.max(0, 1 - d / (h * 0.9f));
                int a = (int) (16 + 225 * ring * fade);
                p.setColor(alpha(a, d < w * 0.25f ? 0xFFFFFF : 0xE8E8F0));
                cv.drawRoundRect(x - dot / 2, y - dot / 2, x + dot / 2, y + dot / 2, k * 2, k * 2, p);
            }
        glow(cv, cx, cy, w * 0.3f, 0x33FFFFFF, 0x00FFFFFF);
    }
}
