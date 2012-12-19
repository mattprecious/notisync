package com.mattprecious.otherdevice.util;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

public class Preferences {
    private static final Joiner commaJoiner = Joiner.on(',').skipNulls();
    private static final Splitter commaSplitter = Splitter.on(',').omitEmptyStrings();

    public static final String KEY_GLOBAL_TYPE = "global_type";
    public static final String KEY_GLOBAL_BOOT_ON_START = "global_boot_on_start";
    public static final String KEY_GLOBAL_THEME = "global_theme";

    public static final String KEY_PRIMARY_DEVICES = "primary_devices";
    public static final String KEY_PRIMARY_RECONNECT_DELAY = "primary_reconnect_delay";
    public static final String KEY_PRIMARY_TEXT_MESSAGE_ENABLED = "primary_text_message_enabled";
    public static final String KEY_PRIMARY_PHONE_CALL_ENABLED = "primary_phone_call_enabled";
    public static final String KEY_PRIMARY_GTALK_ENABLED = "primary_gtalk_enabled";

    public static final String KEY_SECONDARY_RECONNECT_DELAY = "secondary_reconnect_delay";

    public static final String KEY_SECONDARY_TEXT_MESSAGE_ENABLED = "secondary_text_message_enabled";
    public static final String KEY_SECONDARY_TEXT_MESSAGE_RINGTONE = "secondary_text_message_ringtone";
    public static final String KEY_SECONDARY_TEXT_MESSAGE_VIBRATE = "secondary_text_message_vibrate";
    public static final String KEY_SECONDARY_TEXT_MESSAGE_LIGHTS = "secondary_text_message_lights";

    public static final String KEY_SECONDARY_PHONE_CALL_ENABLED = "secondary_phone_call_enabled";
    public static final String KEY_SECONDARY_PHONE_CALL_RINGTONE = "secondary_phone_call_ringtone";
    public static final String KEY_SECONDARY_PHONE_CALL_VIBRATE = "secondary_phone_call_vibrate";
    public static final String KEY_SECONDARY_PHONE_CALL_LIGHTS = "secondary_phone_call_lights";

    public static final String KEY_SECONDARY_GTALK_ENABLED = "secondary_gtalk_enabled";
    public static final String KEY_SECONDARY_GTALK_UNCONNECTED_ONLY = "secondary_gtalk_unconnected_only";
    public static final String KEY_SECONDARY_GTALK_RINGTONE = "secondary_gtalk_ringtone";
    public static final String KEY_SECONDARY_GTALK_VIBRATE = "secondary_gtalk_vibrate";
    public static final String KEY_SECONDARY_GTALK_LIGHTS = "secondary_gtalk_lights";

    // TODO: enum?
    public static final String TYPE_PRIMARY = "primary";
    public static final String TYPE_SECONDARY = "secondary";

    // /////////////////////
    // GLOBAL
    // /////////////////////
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean hasType(Context context) {
        return getPreferences(context).contains(KEY_GLOBAL_TYPE);
    }

    public static String getType(Context context) {
        return getPreferences(context).getString(KEY_GLOBAL_TYPE, TYPE_PRIMARY);
    }

    public static void setType(Context context, String type) {
        Editor editor = getPreferences(context).edit();
        editor.putString(KEY_GLOBAL_TYPE, type);
        editor.commit();
    }

    public static boolean getBootOnStart(Context context) {
        return getPreferences(context).getBoolean(KEY_GLOBAL_BOOT_ON_START, true);
    }

    public static String getTheme(Context context) {
        return getPreferences(context).getString(KEY_GLOBAL_THEME, "holo_dark");
    }

    // /////////////////////
    // PRIMARY
    // /////////////////////
    public static Set<String> getDevices(Context context) {
        String devicesStr = getPreferences(context).getString(KEY_PRIMARY_DEVICES, "");
        return Sets.newHashSet(commaSplitter.split(devicesStr));
    }

    public static void setDevices(Context context, Set<String> devices) {
        Editor editor = getPreferences(context).edit();
        editor.putString(KEY_PRIMARY_DEVICES, commaJoiner.join(devices));
        editor.commit();
    }

    public static int getPrimaryReconnectDelay(Context context) {
        return Integer
                .parseInt(getPreferences(context).getString(KEY_PRIMARY_RECONNECT_DELAY, "5"));
    }

    // //////////////////////////
    // PRIMARY: TEXT MESSAGE
    // //////////////////////////
    public static boolean getPrimaryTextMessageEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_PRIMARY_TEXT_MESSAGE_ENABLED, true);
    }

    // //////////////////////////
    // PRIMARY: PHONE CALL
    // //////////////////////////
    public static boolean getPrimaryPhoneCallEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_PRIMARY_PHONE_CALL_ENABLED, true);
    }

    // //////////////////////////
    // PRIMARY: GTALK
    // //////////////////////////
    public static boolean getPrimaryGtalkEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_PRIMARY_GTALK_ENABLED, true);
    }

    // //////////////////////////
    // SECONDARY
    // //////////////////////////
    public static int getSecondaryReconnectDelay(Context context) {
        return Integer.parseInt(getPreferences(context).getString(KEY_SECONDARY_RECONNECT_DELAY,
                "5"));
    }

    // //////////////////////////
    // SECONDARY: TEXT MESSAGE
    // //////////////////////////
    public static boolean getSecondaryTextMessageEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_TEXT_MESSAGE_ENABLED, true);
    }

    public static String getSecondaryTextMessageRingtone(Context context) {
        return getPreferences(context).getString(KEY_SECONDARY_TEXT_MESSAGE_RINGTONE, null);
    }

    public static boolean getSecondaryTextMessageVibrate(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_TEXT_MESSAGE_VIBRATE, true);
    }

    public static boolean getSecondaryTextMessageLights(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_TEXT_MESSAGE_LIGHTS, true);
    }

    // //////////////////////////
    // SECONDARY: PHONE CALL
    // //////////////////////////
    public static boolean getSecondaryPhoneCallEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_PHONE_CALL_ENABLED, true);
    }

    public static String getSecondaryPhoneCallRingtone(Context context) {
        return getPreferences(context).getString(KEY_SECONDARY_PHONE_CALL_RINGTONE, null);
    }

    public static boolean getSecondaryPhoneCallVibrate(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_PHONE_CALL_VIBRATE, true);
    }

    public static boolean getSecondaryPhoneCallLights(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_PHONE_CALL_LIGHTS, true);
    }

    // //////////////////////////
    // SECONDARY: GTALK
    // //////////////////////////
    public static boolean getSecondaryGtalkEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_GTALK_ENABLED, true);
    }

    public static boolean getSecondaryGtalkUnconnectedOnly(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_GTALK_UNCONNECTED_ONLY, true);
    }

    public static String getSecondaryGtalkRingtone(Context context) {
        return getPreferences(context).getString(KEY_SECONDARY_GTALK_RINGTONE, null);
    }

    public static boolean getSecondaryGtalkVibrate(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_GTALK_VIBRATE, true);
    }

    public static boolean getSecondaryGtalkLights(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_GTALK_LIGHTS, true);
    }
}
