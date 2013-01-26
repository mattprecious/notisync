
package com.mattprecious.otherdevice.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import java.util.Set;

public class Preferences {
    private static final Joiner commaJoiner = Joiner.on(',').skipNulls();
    private static final Splitter commaSplitter = Splitter.on(',').omitEmptyStrings();

    public static final String KEY_GLOBAL_MODE = "global_mode";
    public static final String KEY_GLOBAL_START_ON_BOOT = "global_start_on_boot";
    public static final String KEY_GLOBAL_ANALYTICS = "global_analytics";

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

    public static enum Mode {
        PRIMARY,
        SECONDARY;
    }

    public static void populateDefaults(Context context) {
        setMode(context, getMode(context));
        setStartOnBoot(context, getStartOnBoot(context));
        setAnalytics(context, getAnalytics(context));
        setDevices(context, getDevices(context));

        setPrimaryReconnectDelay(context, getPrimaryReconnectDelay(context));
        setPrimaryTextMessageEnabled(context, getPrimaryTextMessageEnabled(context));
        setPrimaryPhoneCallEnabled(context, getPrimaryPhoneCallEnabled(context));
        setPrimaryGtalkEnabled(context, getPrimaryGtalkEnabled(context));

        setSecondaryReconnectDelay(context, getSecondaryReconnectDelay(context));

        setSecondaryTextMessageEnabled(context, getSecondaryTextMessageEnabled(context));
        setSecondaryTextMessageRingtone(context, getSecondaryTextMessageRingtone(context));
        setSecondaryTextMessageVibrate(context, getSecondaryTextMessageVibrate(context));
        setSecondaryTextMessageLights(context, getSecondaryTextMessageLights(context));

        setSecondaryPhoneCallEnabled(context, getSecondaryPhoneCallEnabled(context));
        setSecondaryPhoneCallRingtone(context, getSecondaryPhoneCallRingtone(context));
        setSecondaryPhoneCallVibrate(context, getSecondaryPhoneCallVibrate(context));
        setSecondaryPhoneCallLights(context, getSecondaryPhoneCallLights(context));

        setSecondaryGtalkEnabled(context, getSecondaryGtalkEnabled(context));
        setSecondaryGtalkRingtone(context, getSecondaryGtalkRingtone(context));
        setSecondaryGtalkVibrate(context, getSecondaryGtalkVibrate(context));
        setSecondaryGtalkLights(context, getSecondaryGtalkLights(context));
    }

    // /////////////////////
    // GLOBAL
    // /////////////////////
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean hasMode(Context context) {
        return getPreferences(context).contains(KEY_GLOBAL_MODE);
    }

    public static Mode getMode(Context context) {
        return Mode
                .valueOf(getPreferences(context).getString(KEY_GLOBAL_MODE, Mode.PRIMARY.name()));
    }

    public static void setMode(Context context, Mode mode) {
        getPreferences(context).edit().putString(KEY_GLOBAL_MODE, mode.name()).commit();
    }

    public static boolean isPrimary(Context context) {
        return getMode(context) == Mode.PRIMARY;
    }

    public static boolean getStartOnBoot(Context context) {
        return getPreferences(context).getBoolean(KEY_GLOBAL_START_ON_BOOT, true);
    }

    public static void setStartOnBoot(Context context, boolean boot) {
        getPreferences(context).edit().putBoolean(KEY_GLOBAL_START_ON_BOOT, boot).commit();
    }

    public static boolean getAnalytics(Context context) {
        return getPreferences(context).getBoolean(KEY_GLOBAL_ANALYTICS, true);
    }

    public static void setAnalytics(Context context, boolean analytics) {
        getPreferences(context).edit().putBoolean(KEY_GLOBAL_ANALYTICS, analytics).commit();
    }

    // /////////////////////
    // PRIMARY
    // /////////////////////
    public static Set<String> getDevices(Context context) {
        String devicesStr = getPreferences(context).getString(KEY_PRIMARY_DEVICES, "");
        return Sets.newHashSet(commaSplitter.split(devicesStr));
    }

    public static void setDevices(Context context, Set<String> devices) {
        getPreferences(context).edit().putString(KEY_PRIMARY_DEVICES, commaJoiner.join(devices))
                .commit();
    }

    public static int getPrimaryReconnectDelay(Context context) {
        return Integer
                .parseInt(getPreferences(context).getString(KEY_PRIMARY_RECONNECT_DELAY, "5"));
    }

    public static void setPrimaryReconnectDelay(Context context, int delay) {
        getPreferences(context).edit()
                .putString(KEY_PRIMARY_RECONNECT_DELAY, String.valueOf(delay)).commit();
    }

    // //////////////////////////
    // PRIMARY: TEXT MESSAGE
    // //////////////////////////
    public static boolean getPrimaryTextMessageEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_PRIMARY_TEXT_MESSAGE_ENABLED, true);
    }

    public static void setPrimaryTextMessageEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_PRIMARY_TEXT_MESSAGE_ENABLED, enabled)
                .commit();
    }

    // //////////////////////////
    // PRIMARY: PHONE CALL
    // //////////////////////////
    public static boolean getPrimaryPhoneCallEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_PRIMARY_PHONE_CALL_ENABLED, true);
    }

    public static void setPrimaryPhoneCallEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_PRIMARY_PHONE_CALL_ENABLED, enabled).commit();
    }

    // //////////////////////////
    // PRIMARY: GTALK
    // //////////////////////////
    public static boolean getPrimaryGtalkEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_PRIMARY_GTALK_ENABLED, true);
    }

    public static void setPrimaryGtalkEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_PRIMARY_GTALK_ENABLED, enabled).commit();
    }

    // //////////////////////////
    // SECONDARY
    // //////////////////////////
    public static int getSecondaryReconnectDelay(Context context) {
        return Integer.parseInt(getPreferences(context).getString(KEY_SECONDARY_RECONNECT_DELAY,
                "5"));
    }

    public static void setSecondaryReconnectDelay(Context context, int delay) {
        getPreferences(context).edit()
                .putString(KEY_SECONDARY_RECONNECT_DELAY, String.valueOf(delay)).commit();
    }

    // //////////////////////////
    // SECONDARY: TEXT MESSAGE
    // //////////////////////////
    public static boolean getSecondaryTextMessageEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_TEXT_MESSAGE_ENABLED, true);
    }

    public static void setSecondaryTextMessageEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_SECONDARY_TEXT_MESSAGE_ENABLED, enabled)
                .commit();
    }

    public static String getSecondaryTextMessageRingtone(Context context) {
        return getPreferences(context).getString(KEY_SECONDARY_TEXT_MESSAGE_RINGTONE,
                getDefaultRingtone());
    }

    public static void setSecondaryTextMessageRingtone(Context context, String ringtone) {
        getPreferences(context).edit().putString(KEY_SECONDARY_TEXT_MESSAGE_RINGTONE, ringtone)
                .commit();
    }

    public static boolean getSecondaryTextMessageVibrate(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_TEXT_MESSAGE_VIBRATE, true);
    }

    public static void setSecondaryTextMessageVibrate(Context context, boolean vibrate) {
        getPreferences(context).edit().putBoolean(KEY_SECONDARY_TEXT_MESSAGE_VIBRATE, vibrate)
                .commit();
    }

    public static boolean getSecondaryTextMessageLights(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_TEXT_MESSAGE_LIGHTS, true);
    }

    public static void setSecondaryTextMessageLights(Context context, boolean lights) {
        getPreferences(context).edit().putBoolean(KEY_SECONDARY_TEXT_MESSAGE_LIGHTS, lights)
                .commit();
    }

    // //////////////////////////
    // SECONDARY: PHONE CALL
    // //////////////////////////
    public static boolean getSecondaryPhoneCallEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_PHONE_CALL_ENABLED, true);
    }

    public static void setSecondaryPhoneCallEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_SECONDARY_PHONE_CALL_ENABLED, enabled)
                .commit();
    }

    public static String getSecondaryPhoneCallRingtone(Context context) {
        return getPreferences(context).getString(KEY_SECONDARY_PHONE_CALL_RINGTONE,
                getDefaultRingtone());
    }

    public static void setSecondaryPhoneCallRingtone(Context context, String ringtone) {
        getPreferences(context).edit().putString(KEY_SECONDARY_PHONE_CALL_RINGTONE, ringtone)
                .commit();
    }

    public static boolean getSecondaryPhoneCallVibrate(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_PHONE_CALL_VIBRATE, true);
    }

    public static void setSecondaryPhoneCallVibrate(Context context, boolean vibrate) {
        getPreferences(context).edit().putBoolean(KEY_SECONDARY_PHONE_CALL_VIBRATE, vibrate)
                .commit();
    }

    public static boolean getSecondaryPhoneCallLights(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_PHONE_CALL_LIGHTS, true);
    }

    public static void setSecondaryPhoneCallLights(Context context, boolean lights) {
        getPreferences(context).edit().putBoolean(KEY_SECONDARY_PHONE_CALL_LIGHTS, lights).commit();
    }

    // //////////////////////////
    // SECONDARY: GTALK
    // //////////////////////////
    public static boolean getSecondaryGtalkEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_GTALK_ENABLED, true);
    }

    public static void setSecondaryGtalkEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_SECONDARY_GTALK_ENABLED, enabled).commit();
    }

    public static boolean getSecondaryGtalkUnconnectedOnly(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_GTALK_UNCONNECTED_ONLY, true);
    }

    public static void setSecondaryGtalkUnconnectedOnly(Context context, boolean unconnected) {
        getPreferences(context).edit()
                .putBoolean(KEY_SECONDARY_GTALK_UNCONNECTED_ONLY, unconnected).commit();
    }

    public static String getSecondaryGtalkRingtone(Context context) {
        return getPreferences(context)
                .getString(KEY_SECONDARY_GTALK_RINGTONE, getDefaultRingtone());
    }

    public static void setSecondaryGtalkRingtone(Context context, String ringtone) {
        getPreferences(context).edit().putString(KEY_SECONDARY_GTALK_RINGTONE, ringtone).commit();
    }

    public static boolean getSecondaryGtalkVibrate(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_GTALK_VIBRATE, true);
    }

    public static void setSecondaryGtalkVibrate(Context context, boolean vibrate) {
        getPreferences(context).edit().putBoolean(KEY_SECONDARY_GTALK_VIBRATE, vibrate).commit();
    }

    public static boolean getSecondaryGtalkLights(Context context) {
        return getPreferences(context).getBoolean(KEY_SECONDARY_GTALK_LIGHTS, true);
    }

    public static void setSecondaryGtalkLights(Context context, boolean lights) {
        getPreferences(context).edit().putBoolean(KEY_SECONDARY_GTALK_LIGHTS, lights).commit();
    }

    // quick helper function
    private static String getDefaultRingtone() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
    }
}
