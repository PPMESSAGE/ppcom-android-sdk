package com.ppmessage.sdk.core.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.api.BaseHttpRequest;
import com.ppmessage.sdk.core.api.HostConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by ppmessage on 5/6/16.
 */
public final class Utils {

    private static final String MMDD_HHMM_TIMESTAMP_FORMAT = "MM-dd hh:mm";

    /**
     * Download host
     */
    public static final String DOWNLOAD_HOST = HostConstants.HTTP_HOST + "/download/";

    private static final TxtLoader txtLoader = new TxtLoader();
    private static final TxtUploader txtUploader = new TxtUploader();

    private Utils() {}

    /**
     * get file download url
     *
     * @param fid
     * @return
     */
    public static String getFileDownloadUrl(String fid) {
        if (TextUtils.isEmpty(fid)) return fid;
        if (fid.startsWith("http")) return fid;
        if (fid.startsWith("www")) return fid;
        return DOWNLOAD_HOST + fid;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String humanReadableByteCount(long bytes) {
        boolean si = true;

        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static boolean isTextLargeThan128(String text) {
        int len = 0;
        try {
            len = new String(text.getBytes(), "ASCII").length();
        } catch (UnsupportedEncodingException e) {
            // Ignore
        }
        return len > 128;
    }

    public static Point getDisplayPoint(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        return outSize;
    }

    public static String formatTimestamp(long messageTimestamp) {
        Date dt = new Date(messageTimestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(MMDD_HHMM_TIMESTAMP_FORMAT, Locale.getDefault());
        return sdf.format(dt);
    }

    public static void makeToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void makeToast(Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void copyToClipboard(Context context, String text) {
        final String body = text;
        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(body);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                    .newPlainText("text label", body);
            clipboard.setPrimaryClip(clip);
        }
    }

    public static String getDeviceUUID(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * We consider
     *
     * <pre>
     *     {"error_code": 0, "uri": "/XXXXX", "error_string": "xxx"}
     * </pre>
     *
     * as an empty response
     *
     * @param jsonResponse
     * @return
     */
    public static boolean isJsonResponseEmpty(JSONObject jsonResponse) {
        try {
            if (jsonResponse.getInt("error_code") != 0) return false;
            if (jsonResponse.length() > 3) return false;
        } catch (JSONException e) {
            L.e(e);
        }
        return true;
    }

    public static long getTimestamp(String time) {
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS");
            Date parsedDate = dateFormat.parse(time);
            java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            return timestamp.getTime();
        }catch(Exception e){//this generic but you can control another types of exception
            L.e(e);
        }
        return 0;
    }

    public static boolean isNull(String string) {
        if (TextUtils.isEmpty(string)) return true;
        if (string.equals("null")) return true;

        return false;
    }

    public static String safeNull(String string) {
        if (isNull(string)) return null;

        return string;
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static String randomUUID() {
        return String.valueOf(UUID.randomUUID());
    }

    public static TxtLoader getTxtLoader() {
        return txtLoader;
    }

    public static TxtUploader getTxtUploader() {
        return txtUploader;
    }

}
