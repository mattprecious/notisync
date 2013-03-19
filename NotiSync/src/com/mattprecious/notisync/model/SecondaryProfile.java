/*
 * Copyright 2013 Matthew Precious
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mattprecious.notisync.model;

import android.media.RingtoneManager;
import android.os.Parcel;
import android.os.Parcelable;

public class SecondaryProfile implements Parcelable {

    private int id;
    private String name;
    private String tag;
    private boolean enabled;
    private boolean unconnectedOnly;
    private String ringtone;
    private boolean vibrate;
    private boolean led;

    public SecondaryProfile() {
        ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUnconnectedOnly() {
        return unconnectedOnly;
    }

    public void setUnconnectedOnly(boolean onlyUnconnected) {
        this.unconnectedOnly = onlyUnconnected;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public boolean isLed() {
        return led;
    }

    public void setLed(boolean led) {
        this.led = led;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("id: ").append(id);
        buffer.append(", name: ").append(name);
        buffer.append(", tag: ").append(tag);
        buffer.append(", enabled: ").append(enabled);
        buffer.append(", unconnectedOnly: ").append(unconnectedOnly);
        buffer.append(", ringtone: ").append(ringtone);
        buffer.append(", vibrate: ").append(vibrate);
        buffer.append(", led: ").append(led);

        buffer.insert(0, "SecondaryProfile {").append("}");

        return buffer.toString();
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
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeByte((byte) (unconnectedOnly ? 1 : 0));
        dest.writeString(ringtone);
        dest.writeByte((byte) (vibrate ? 1 : 0));
        dest.writeByte((byte) (led ? 1 : 0));
    }

    public SecondaryProfile(Parcel in) {
        id = in.readInt();
        name = in.readString();
        tag = in.readString();
        enabled = in.readByte() == 1;
        unconnectedOnly = in.readByte() == 1;
        ringtone = in.readString();
        vibrate = in.readByte() == 1;
        led = in.readByte() == 1;
    }

    public static final Parcelable.Creator<SecondaryProfile> CREATOR = new Parcelable.Creator<SecondaryProfile>() {

        @Override
        public SecondaryProfile createFromParcel(Parcel source) {
            return new SecondaryProfile(source);
        }

        @Override
        public SecondaryProfile[] newArray(int size) {
            return new SecondaryProfile[size];
        }

    };
}
