
package com.mattprecious.otherdevice.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

public class ContactHelper {
    private static final String TAG = "ContactHelper";

    public static long getIdByNumber(Context context, String number) {
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] columns = new String[] {
            PhoneLookup._ID
        };
        Cursor c = context.getContentResolver().query(uri, columns, null, null, null);

        long id = -1;
        if (c.moveToFirst()) {
            id = c.getLong(c.getColumnIndex(PhoneLookup._ID));
        }

        c.close();

        return id;
    }

    public static String getNameByNumber(Context context, String number) {
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] columns = new String[] {
            PhoneLookup.DISPLAY_NAME
        };
        Cursor c = context.getContentResolver().query(uri, columns, null, null, null);

        String name = null;
        if (c.moveToFirst()) {
            name = c.getString(c.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }

        c.close();

        return name;
    }

    public static Bitmap getContactPhoto(Context context, String number) {
        long contactId = getIdByNumber(context, number);
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);

        InputStream stream = null;
        try {
            stream = context.getContentResolver().openInputStream(photoUri);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "could not find contact picture");
            return null;
        }

        if (stream == null) {
            return null;
        }

        return BitmapFactory.decodeStream(stream);
    }

}
