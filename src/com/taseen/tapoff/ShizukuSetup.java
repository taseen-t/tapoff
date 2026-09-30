package com.taseen.tapoff;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import moe.shizuku.api.BinderContainer;
import moe.shizuku.server.IShizukuApplication;
import moe.shizuku.server.IShizukuService;

// Gives TapOff its one permission without a computer, through Shizuku: an app that runs with adb's rights once it's
// started from the phone's own Wireless debugging. Shizuku hands its binder to every app that asks for its API
// permission by calling this provider, which only shell and system can reach (INTERACT_ACROSS_USERS_FULL).
public class ShizukuSetup extends ContentProvider {
    static final String PACKAGE = "moe.shizuku.privileged.api";
    private static final String EXTRA_BINDER = "moe.shizuku.privileged.api.intent.extra.BINDER";
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private static volatile IBinder binder; // from Shizuku, while it runs
    // Shizuku keeps one client record per process, with the app binder from the first attach; attaching again
    // throws. So this process attaches once per Shizuku binder, and the one APP binder runs whatever is pending.
    private static IBinder attached;
    private static volatile Runnable pending;

    private static final IShizukuApplication.Stub APP = new IShizukuApplication.Stub() {
        @Override public void bindApplication(Bundle data) {}

        @Override public void dispatchRequestPermissionResult(int requestCode, Bundle data) {
            Runnable after = pending;
            pending = null;
            if (after == null) return;
            if (data != null && data.getBoolean("shizuku:request-permission-reply-allowed")) pmGrant();
            MAIN.post(after);
        }

        @Override public void showPermissionConfirmation(int uid, int pid, String pkg, int requestCode) {} // Sui only
    };

    static boolean installed(Context c) {
        try {
            c.getPackageManager().getPackageInfo(PACKAGE, 0);
            return true;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    static boolean running() {
        IBinder b = binder;
        return b != null && b.pingBinder();
    }

    // Asks Shizuku for its permission (it shows its own Allow dialog the first time), then runs the same pm grant a
    // computer would. `after` runs on the main thread when it's over, granted or not; LockService.canLock tells.
    static void grant(String packageName, Runnable after) {
        IBinder b = binder;
        new Thread(() -> {
            try {
                IShizukuService s = IShizukuService.Stub.asInterface(b);
                synchronized (ShizukuSetup.class) {
                    if (attached != b) {
                        Bundle args = new Bundle();
                        args.putString("shizuku:attach-package-name", packageName);
                        args.putInt("shizuku:attach-api-version", 13);
                        s.attachApplication(APP, args);
                        attached = b;
                    }
                }
                if (s.checkSelfPermission()) {
                    pmGrant();
                    MAIN.post(after);
                } else {
                    pending = after;
                    s.requestPermission(1);
                }
            } catch (RemoteException | RuntimeException e) {
                Log.w("TapOff", "Shizuku setup failed", e);
                MAIN.post(after);
            }
        }).start();
    }

    private static void pmGrant() {
        try {
            IShizukuService.Stub.asInterface(binder).newProcess(new String[] {"pm", "grant", "com.taseen.tapoff",
                Manifest.permission.WRITE_SECURE_SETTINGS}, null, null).waitFor();
        } catch (RemoteException | RuntimeException e) {
            Log.w("TapOff", "pm grant through Shizuku failed", e);
        }
    }

    @Override public Bundle call(String method, String arg, Bundle extras) {
        if ("sendBinder".equals(method) && extras != null) {
            extras.setClassLoader(BinderContainer.class.getClassLoader());
            BinderContainer c = extras.getParcelable(EXTRA_BINDER, BinderContainer.class);
            if (c != null && c.binder != null) binder = c.binder;
        }
        return new Bundle(); // Shizuku counts a null reply as a failed send
    }

    @Override public boolean onCreate() { return true; }

    @Override public Cursor query(Uri u, String[] p, String s, String[] a, String o) { return null; }

    @Override public String getType(Uri u) { return null; }

    @Override public Uri insert(Uri u, ContentValues v) { return null; }

    @Override public int delete(Uri u, String s, String[] a) { return 0; }

    @Override public int update(Uri u, ContentValues v, String s, String[] a) { return 0; }
}
