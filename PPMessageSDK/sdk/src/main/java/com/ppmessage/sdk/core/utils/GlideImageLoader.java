package com.ppmessage.sdk.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ppmessage.sdk.core.L;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by ppmessage on 8/1/16.
 */
public class GlideImageLoader implements IImageLoader {

    private Context context;
    private File diskCacheFolder;
    private Map<String, String> diskCacheKeyMap;

    public GlideImageLoader(Context context) {
        this.context = context;
        diskCacheFolder = Glide.getPhotoCacheDir(context);
        diskCacheKeyMap = new HashMap<>();
    }

    @Nullable
    @Override
    public Bitmap inMemory(String imageUri, int width, int height) {
        return null;
    }

    @Nullable
    @Override
    public File imageFile(String imageUri) {
        if (imageUri == null) return null;

        String sha256Key = diskCacheKeyMap.get(imageUri);
        if (sha256Key == null) {
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                L.e(e);
            }

            messageDigest.update(imageUri.getBytes());
            // Depends on the detail inner implementation of {@link Glide}
            // The final generated file name is something like this:
            // c2c24e846e01c7ad83f8326fad64e0026df6098811016ebe7008848333af0951.0
            // so, we add ".0" manaually to the end of the sha256Key
            sha256Key = com.bumptech.glide.util.Util.sha256BytesToHex(messageDigest.digest()) + ".0";
            diskCacheKeyMap.put(imageUri, sha256Key);
        }

        if (sha256Key == null) return null;
        return new File(diskCacheFolder, sha256Key);
    }

    @Override
    public void loadImage(String imageUri, ImageView target) {
        loadImage(imageUri, 300, 400, target);
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
        loadImage(imageUri, width, height, false, placeHolder, target, callback);
    }

    @Override
    public void loadImage(String imageUri, int width, int height, boolean asGif, Drawable placeHolder, ImageView target, final Callback callback) {
        if (asGif) {
            Glide.with(context)
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(width, height)
                    .placeholder(placeHolder)
                    .centerCrop()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            L.w("[GlideImageLoader] onException (%s, %s, %s, %s)", e, model, target, String.valueOf(isFirstResource));

                            if (callback != null) {
                                callback.onError();
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            L.d("[GlideImageLoader] onResourceReady(%s, %s, %s, %s)", resource, model, target, String.valueOf(isFirstResource));

                            if (callback != null) {
                                callback.onSuccess();
                            }
                            return false;
                        }
                    })
                    .into(target);
        } else {
            Glide.with(context)
                    .load(imageUri)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(width, height)
                    .placeholder(placeHolder)
                    .centerCrop()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            L.w("[GlideImageLoader] onException (%s, %s, %s, %s)", e, model, target, String.valueOf(isFirstResource));

                            if (callback != null) {
                                callback.onError();
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            L.d("[GlideImageLoader] onResourceReady(%s, %s, %s, %s)", resource, model, target, String.valueOf(isFirstResource));

                            if (callback != null) {
                                callback.onSuccess();
                            }
                            return false;
                        }
                    })
                    .into(target);
        }
    }
}
