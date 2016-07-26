package com.ppmessage.sdk.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.ppmessage.sdk.core.L;

import java.io.File;

/**
 * Created by ppmessage on 7/26/16.
 */
public class UILImageLoader implements IImageLoader {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 400;

    private static final String LOG_LOADING_FAILED = "[UILImageLoader] load image %s failed: %s";

    private Context context;

    public UILImageLoader(Context context) {
        this.context = context;
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    @Override
    public Bitmap inMemory(String imageUri, int width, int height) {
        String memoryCacheKey = MemoryCacheUtils.generateKey(imageUri, new ImageSize(width, height));
        return ImageLoader.getInstance().getMemoryCache().get(memoryCacheKey);
    }

    @Override
    public File imageFile(String imageUri) {
        return ImageLoader.getInstance().getDiskCache().get(imageUri);
    }

    @Override
    public void loadImage(String imageUri, ImageView target) {
        loadImage(imageUri, DEFAULT_WIDTH, DEFAULT_HEIGHT, target);
    }

    @Override
    public void loadImage(String imageUri, int width, int height, ImageView target) {
        loadImage(imageUri, width, height, null, target);
    }

    @Override
    public void loadImage(String imageUri, int width, int height, int placeHolder, ImageView target) {
        loadImage(imageUri, width, height, context.getResources().getDrawable(placeHolder), target);
    }

    @Override
    public void loadImage(String imageUri, int width, int height, Drawable placeHolder, ImageView target) {
        loadImage(imageUri, width, height, placeHolder, target, null);
    }

    @Override
    public void loadImage(String imageUri, int width, int height, Drawable placeHolder, ImageView target, final Callback callback) {
        ImageLoader.getInstance().displayImage(
                imageUri,
                target,
                new DisplayImageOptions.Builder()
                        .showImageOnLoading(placeHolder)
                        .showImageOnFail(placeHolder)
                        .showImageForEmptyUri(placeHolder)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build(),
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                        L.e(LOG_LOADING_FAILED, imageUri, failReason != null ? failReason.toString() : "null");

                        if (callback != null) callback.onError();
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);

                        if (callback != null) callback.onSuccess();
                    }
                });
    }
}
