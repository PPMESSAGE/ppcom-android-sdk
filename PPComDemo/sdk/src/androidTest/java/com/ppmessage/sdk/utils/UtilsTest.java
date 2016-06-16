package com.ppmessage.sdk.utils;

import android.support.test.runner.AndroidJUnit4;

import com.ppmessage.sdk.core.api.BaseAPIRequest;
import com.ppmessage.sdk.core.utils.Utils;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

/**
 * Created by ppmessage on 5/6/16.
 */
@RunWith(AndroidJUnit4.class)
public class UtilsTest {

    @Test
    public void testGetFileUrl() {
        Assert.assertThat(Utils.getFileDownloadUrl(null), Matchers.nullValue());
        Assert.assertThat(Utils.getFileDownloadUrl(""), Matchers.isEmptyOrNullString());
        Assert.assertThat(Utils.getFileDownloadUrl("www.ppmessage.com"), Matchers.is("www.ppmessage.com"));
        Assert.assertThat(Utils.getFileDownloadUrl("http://ppmessage.com"), Matchers.is("http://ppmessage.com"));
        Assert.assertThat(Utils.getFileDownloadUrl("https://ppmessage.com"), Matchers.is("https://ppmessage.com"));
        Assert.assertThat(Utils.getFileDownloadUrl("abc-def"),
                Matchers.is(String.format(Locale.getDefault(), "%s%s", Utils.DOWNLOAD_HOST, "abc-def")));
    }

}
