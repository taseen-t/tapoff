// From Shizuku-API (https://github.com/RikkaApps/Shizuku-API), Apache License 2.0, Copyright RikkaApps.
// Unnumbered, so the order is the wire protocol: keep it exactly as upstream.
package moe.shizuku.server;

interface IRemoteProcess {
    ParcelFileDescriptor getOutputStream();

    ParcelFileDescriptor getInputStream();

    ParcelFileDescriptor getErrorStream();

    int waitFor();

    int exitValue();

    void destroy();

    boolean alive();

    boolean waitForTimeout(long timeout, String unit);
}
