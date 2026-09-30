// From Shizuku-API (https://github.com/RikkaApps/Shizuku-API), Apache License 2.0, Copyright RikkaApps.
// Shizuku parcels its binder in this class, so the name and package must match exactly.
package moe.shizuku.api;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

public class BinderContainer implements Parcelable {
    public IBinder binder;

    public BinderContainer(IBinder binder) {
        this.binder = binder;
    }

    protected BinderContainer(Parcel in) {
        this.binder = in.readStrongBinder();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.binder);
    }

    public static final Creator<BinderContainer> CREATOR = new Creator<BinderContainer>() {
        @Override public BinderContainer createFromParcel(Parcel source) {
            return new BinderContainer(source);
        }

        @Override public BinderContainer[] newArray(int size) {
            return new BinderContainer[size];
        }
    };
}
