package com.ppmessage.sdk.core.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.L;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by ppmessage on 5/12/16.
 */
public class PicassoImageLoader implements IImageLoader {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 400;

    private static final String IMAGE_LOAD_LOG = "Load image: %s, width: %d, height: %d";

    private Context context;

    public PicassoImageLoader(Context context) {
        this.context = context;
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
        L.d(IMAGE_LOAD_LOG, imageUri, width, height);
        Picasso.with(context)
                .load(imageUri)
                .placeholder(placeHolder)
                .resize(width, height)
                .centerCrop()
                .into(target, new Callback() {

                    @Override
                    public void onSuccess() {
                        L.d("imageUri:%s load success", imageUri);
                    }

                    @Override
                    public void onError() {
                        L.e("imageUri:%s load error", imageUri);
                    }

                });
    }

    @Override
    public void loadImage(final String imageUri, int width, int height, Drawable placeHolder, ImageView target) {
        L.d(IMAGE_LOAD_LOG, imageUri, width, height);
        Picasso.with(context)
                .load(imageUri)
                .placeholder(placeHolder)
                .resize(width, height)
                .centerCrop()
                .into(target, new Callback() {
                    @Override
                    public void onSuccess() {
                        L.d("imageUri:%s load success", imageUri);
                    }

                    @Override
                    public void onError() {
                        L.e("imageUri:%s load error", imageUri);
                    }
                });
    }

}
