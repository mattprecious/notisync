
package com.mattprecious.notisync.util;

import android.os.Build;

public class Helpers {
    public static boolean hasBluetoothIssue() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }
}
