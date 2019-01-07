package com.maihaoche.volvo.util;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.maihaoche.volvo.AppApplication;

import java.io.File;


/**
 * Created by sanji on 2016/11/7.
 */

public class MediaScanner {


    public static MediaScannerConnection.MediaScannerConnectionClient client;
    public static MediaScannerConnection mediaScannerConnection;

    public static void scan(final String path, final String type) {
        if (client == null) {
            client = new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    mediaScannerConnection.disconnect();
                }
                @Override
                public void onMediaScannerConnected() {
                    mediaScannerConnection.scanFile(path, type);
                }
            };
        }
        if (mediaScannerConnection == null) {
            mediaScannerConnection = new MediaScannerConnection(AppApplication.getApplication(), client);
        }
        mediaScannerConnection.connect();

    }

    public static void scan(final File file, Context context) {
        Uri localUri = Uri.fromFile(file);
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
        context.sendBroadcast(localIntent);
    }
}
