// From Shizuku-API (https://github.com/RikkaApps/Shizuku-API), Apache License 2.0, Copyright RikkaApps.
// Trimmed to what TapOff calls; the "= N" ids must stay as they are, they are the wire protocol.
package moe.shizuku.server;

import moe.shizuku.server.IRemoteProcess;
import moe.shizuku.server.IShizukuApplication;

interface IShizukuService {
    IRemoteProcess newProcess(in String[] cmd, in String[] env, in String dir) = 7;

    void requestPermission(int requestCode) = 14;

    boolean checkSelfPermission() = 15;

    void attachApplication(in IShizukuApplication application, in Bundle args) = 17;
}
