package com.ppmessage.sdk.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.L;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ppmessage on 5/12/16.
 */
public class PicassoImageLoader implements IImageLoader {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 400;

    private static final boolean LOG_ENABLE = false;
    private static final String IMAGE_LOAD_LOG = "[PicassoImageLoader] Load image: %s, width: %d, height: %d";
    private static final String IMAGE_LOAD_EXCEPTION_LOG = "[PicassoImageLoader] load image:%s, exception:%s";

    private Context context;
    private Picasso picasso;

    private Method quickMemoryCacheCheckMethod;

    public PicassoImageLoader(Context context) {
        this.context = context;
        picasso = Picasso.with(this.context);
        picasso.setLoggingEnabled(true);

        Class clazz = picasso.getClass();
        try {
            quickMemoryCacheCheckMethod = clazz.getDeclaredMethod("quickMemoryCacheCheck", String.class);
            quickMemoryCacheCheckMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            L.e(e);
        }
    }

    @Override
    public Bitmap inMemory(String imageUri, int width, int height) {
        if (quickMemoryCacheCheckMethod != null) {
            try {
                return (Bitmap) quickMemoryCacheCheckMethod.invoke(picasso, imageUri);
            } catch (IllegalAccessException e) {
                L.e(e);
            } catch (InvocationTargetException e) {
                L.e(e);
            }
        }
        return null;
    }

    @Override
    public File imageFile(String imageUri) {
        return null;
    }

    @Override
    public void loadImage(String imageUri, ImageView target) {
        loadImage(imageUri, DEFAULT_WIDTH, DEFAULT_HEIGHT, target);
    }

    @Override
    public void loadImage(String imageUri, int width, int height, ImageView target) {
        loadImage(imageUri, width, height, R.drawable.empty_photo, target);
    }

    @Override
    public void loadImage(final String imageUri, int width, int height, int placeHolder, ImageView target) {
        if (TextUtils.isEmpty(imageUri)) return;
        if (LOG_ENABLE) L.d(IMAGE_LOAD_LOG, imageUri, width, height);

        buildCreator(imageUri, width, height).placeholder(placeHolder).into(target);
    }

    @Override
    public void loadImage(final String imageUri, int width, int height, Drawable placeHolder, ImageView target) {
        loadImage(imageUri, width, height, placeHolder, target, null);
    }

    private RequestCreator buildCreator(final String imageUri, int width, int height) {
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                L.e(IMAGE_LOAD_EXCEPTION_LOG, uri, exception);
            }
        });
        return builder.build().load(imageUri).resize(width, height).centerCrop();
    }

    @Override
    public void loadImage(String imageUri, int width, int height, Drawable placeHolder, ImageView target, final Callback callback) {
        if (TextUtils.isEmpty(imageUri)) return;
        if (LOG_ENABLE) L.d(IMAGE_LOAD_LOG, imageUri, width, height);

        buildCreator(imageUri, width, height).placeholder(placeHolder).into(target, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onSuccess();
            }

            @Override
            public void onError() {
                if (callback != null) callback.onError();
            }
        });
    }

}
