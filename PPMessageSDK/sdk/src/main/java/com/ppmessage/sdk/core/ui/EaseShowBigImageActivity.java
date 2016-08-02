/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ppmessage.sdk.core.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.ppmessage.sdk.R;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.ui.view.photoview.EasePhotoView;
import com.ppmessage.sdk.core.utils.IImageLoader;
import com.ppmessage.sdk.core.utils.Utils;

/**
 * download and show original image
 *
 */
public class EaseShowBigImageActivity extends Activity {

    public static final String EXTRA_IMAGE_URI_KEY = "remotepath";
    /** int **/
    public static final String EXTRA_IMAGE_WIDTH_KEY = "width";
    /** int **/
    public static final String EXTRA_IMAGE_HEIGHT_KEY = "height";

    private EasePhotoView image;
    private ProgressBar loadLocalPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_activity_show_big_image);

        image = (EasePhotoView) findViewById(R.id.image);
        loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);

        Point screenPoint = Utils.getDisplayPoint(this);

        final String remotepath = getIntent().getExtras().getString(EXTRA_IMAGE_URI_KEY);
        final int width = getIntent().getIntExtra(EXTRA_IMAGE_WIDTH_KEY, screenPoint.x);
        final int height = getIntent().getIntExtra(EXTRA_IMAGE_HEIGHT_KEY, screenPoint.y);

        int sampleSize = Utils.calculateInSampleSize(width, height, screenPoint.x, screenPoint.y);
        if (sampleSize < 1) sampleSize = 1;

        int targetWidth = width / sampleSize;
        int targetHeight = height / sampleSize;

        loadLocalPb.setVisibility(View.VISIBLE);
        PPMessageSDK.getInstance().getImageLoader().loadImage(
                remotepath,
                targetWidth,
                targetHeight,
                true,
                null,
                image,
                new IImageLoader.Callback() {
                    @Override
                    public void onSuccess() {
                        if (EaseShowBigImageActivity.this.isFinishing()) {
                            return;
                        }

                        setResult(Activity.RESULT_OK);
                        loadLocalPb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
