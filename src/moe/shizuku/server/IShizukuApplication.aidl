// From Shizuku-API (https://github.com/RikkaApps/Shizuku-API), Apache License 2.0, Copyright RikkaApps.
// Trimmed to what TapOff calls; the "= N" ids must stay as they are, they are the wire protocol.
package moe.shizuku.server;

interface IShizukuApplication {
    oneway void bindApplication(in Bundle data) = 1;

    oneway void dispatchRequestPermissionResult(int requestCode, in Bundle data) = 2;

    void showPermissionConfirmation(int requestUid, int requestPid, in String requestPackageName, int requestCode) = 10000;
}
