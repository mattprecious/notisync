package com.mattprecious.otherdevice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PrimaryProfile implements Parcelable {

    private int id;
    private String name;
    private String tag;
    private String packageName;
    private boolean enabled;

    public PrimaryProfile() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(tag);
        dest.writeString(packageName);
        dest.writeByte((byte) (enabled ? 1 : 0));
    }
    
    public PrimaryProfile(Parcel in) {
        id = in.readInt();
        name = in.readString();
        tag = in.readString();
        packageName = in.readString();
        enabled = in.readByte() == 1;
    }

    public static final Parcelable.Creator<PrimaryProfile> CREATOR = new Parcelable.Creator<PrimaryProfile>() {

        @Override
        public PrimaryProfile createFromParcel(Parcel source) {
            return new PrimaryProfile(source);
        }

        @Override
        public PrimaryProfile[] newArray(int size) {
            return new PrimaryProfile[size];
        }

    };

}
