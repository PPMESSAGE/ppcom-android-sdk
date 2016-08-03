package com.ppmessage.sdk.core.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by ppmessage on 5/6/16.
 */
public final class Utils {

    private static final String PPMESSAGE = "PPMessage";

    public static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private static final String HHMM_AA_TIMESTAMP_FORMAT = "hh:mm aa";
    private static final String MMDDYY_TIMESTAMP_FORMAT = "MM/dd/yy";
    private static final String MMDDYY_HHMM_AA_TIMESTAMP_FORMAT = "MM/dd/yy hh:mm aa";

    private static final TxtLoader txtLoader = new TxtLoader();
    private static final TxtUploader txtUploader = new TxtUploader(PPMessageSDK.getInstance());
    private static final FileUploader fileUploader = new FileUploader(PPMessageSDK.getInstance());

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

        final String DOWNLOAD_HOST = PPMessageSDK.getInstance().getHostInfo().getDownloadHost();
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
        return formatTimestamp(messageTimestamp, false);
    }

    public static String formatTimestamp(long messageTimestamp, boolean simpleStyle) {

        Calendar now = Calendar.getInstance();

        Calendar other = Calendar.getInstance();
        Date dt = new Date(messageTimestamp);
        other.setTime(dt);

        String format = null;

        // Today: [13:44 PM]
        if (sameDay(now, other)) {
            format = HHMM_AA_TIMESTAMP_FORMAT;
        }

        if (format == null) {
            if (simpleStyle) {
                // Normal: [06/15/16] ==> 2016.06.15
                format = MMDDYY_TIMESTAMP_FORMAT;
            } else {
                // Normal: [06/15/16 13:44 PM] ==> 2016.06.15 13:44 PM
                format = MMDDYY_HHMM_AA_TIMESTAMP_FORMAT;
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(dt);
    }

    private static boolean sameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
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

    // ======================
    // commons-io
    //
    // Copied from Apache Commons IO Library IOUtils.java
    // http://commons.apache.org/proper/commons-io/
    // ======================

    public static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(final InputStream input, final OutputStream output)
            throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    public static long copy(final InputStream input, final OutputStream output, final int bufferSize)
            throws IOException {
        return copyLarge(input, output, new byte[bufferSize]);
    }

    public static long copyLarge(final InputStream input, final OutputStream output, final byte[] buffer)
            throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    // ======================
    // commons-io
    // ======================

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

    public static boolean isJsonResponseError(JSONObject jsonObject) {
        try {
            return jsonObject.getInt("error_code") != 0;
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

    public static FileUploader getFileUploader() {
        return fileUploader;
    }

    public static File getPublicImageFolder() {
        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFolder = new File(storageDir, PPMESSAGE);
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        return imageFolder;
    }

    /**
     * http://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri uri) {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void calculateTargetDisplayImageSize(int[] outSize, int reqWidth, int reqHeight, int width, int height) {
        if (outSize == null || outSize.length != 2) {
            throw new IllegalArgumentException("outSize.length != 2");
        }

        if (width > reqWidth || height > reqHeight) {
            float ratio = Math.min((float) reqWidth / width, (float) reqHeight / height);
            outSize[0] = (int) (width * ratio);
            outSize[1] = (int) (height * ratio);
        } else {
            outSize[0] = width;
            outSize[1] = height;
        }
    }

    public static int calculateInSampleSize(int reqWidth, int reqHeight, int width, int height) {
        int sampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio;
            final int widthRatio;
            if (reqHeight == 0) {
                sampleSize = (int) Math.floor((float) width / (float) reqWidth);
            } else if (reqWidth == 0) {
                sampleSize = (int) Math.floor((float) height / (float) reqHeight);
            } else {
                heightRatio = (int) Math.floor((float) height / (float) reqHeight);
                widthRatio = (int) Math.floor((float) width / (float) reqWidth);
                sampleSize = Math.min(heightRatio, widthRatio);
            }
        }
        return sampleSize;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
