package com.ppmessage.sdk.core.utils;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by ppmessage on 5/12/16.
 */
public interface IImageLoader {

    void loadImage(String imageUri, ImageView target);

    void loadImage(String imageUri, int width, int height, ImageView target);

    void loadImage(String imageUri, int width, int height, int placeHolder, ImageView target);

    void loadImage(String imageUri, int width, int height, Drawable placeHolder, ImageView target);

}
