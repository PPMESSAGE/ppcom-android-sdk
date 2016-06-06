package com.ppmessage.sdk.core;

import android.content.Context;

import com.ppmessage.sdk.core.api.DefaultAPIImpl;
import com.ppmessage.sdk.core.api.IAPI;
import com.ppmessage.sdk.core.api.IToken;
import com.ppmessage.sdk.core.api.Token;
import com.ppmessage.sdk.core.notification.DefaultNotification;
import com.ppmessage.sdk.core.notification.INotification;
import com.ppmessage.sdk.core.query.DataCenter;
import com.ppmessage.sdk.core.query.IDataCenter;
import com.ppmessage.sdk.core.utils.IImageLoader;

/**
 * Before calling any api, MUST confirm you have called
 *
 * <pre>
 *     // PPCOM
 *     PPMessageSDK.getInstance().config('YOUR_APP_UUID');
 *
 *     // OR PPKEFU
 *     PPMessageSDK.getInstance().config('KEFU_USER_EMAIL', 'KEFU_USER_SHA1_PASSWORD');
 * </pre>
 *
 * to init <b>PPMessageSDK</b>, or it will throw Exception.
 *
 * <b>PPMessageSDK</b> will consider to use `YOU_APP_UUID` first if you both provided <b>YOUR_APP_UUID</b> and
 * <b>KEFU_USER_EMAIL</b> and <b>KEFU_USER_SHA1_PASSWORD</b> to generate avaliable <b>access_token</b>
 *
 * Created by ppmessage on 5/5/16.
 */
public class PPMessageSDK {

    private static final String SDK_VERSION = "0.0.1";
    private static final String CONFIGURATION_EMPTY_LOG = "[PPMessageSDK] can not be initialized with empty configuration";

    public static final String TAG = "[" + PPMessageSDK.class.getSimpleName() + "]";

    private static PPMessageSDK ourInstance = new PPMessageSDK();

    private IAPI api;
    private INotification notification;
    private IToken token;
    private IDataCenter dataCenter;

    private PPMessageSDKConfiguration configuration;

    public static PPMessageSDK getInstance() {
        return ourInstance;
    }

    public synchronized void init(PPMessageSDKConfiguration configuration) {
        if (configuration == null) {
            throw new PPMessageException(CONFIGURATION_EMPTY_LOG);
        } else {
            this.configuration = configuration;
            L.writeLogs(this.configuration.enableLogging);
            L.writeDebugLogs(this.configuration.enableDebugLogging);
        }
    }

    private PPMessageSDK() {
    }

    public IAPI getAPI() {
        checkConfig();
        if (api == null) api = new DefaultAPIImpl(this);
        return api;
    }

    public INotification getNotification() {
        checkConfig();
        if (notification == null) notification = new DefaultNotification(this, this.configuration.webSocket);
        return notification;
    }

    public IToken getToken() {
        checkConfig();
        if (token == null) token = new Token();
        return token;
    }

    public IDataCenter getDataCenter() {
        checkConfig();
        if (dataCenter == null) dataCenter = new DataCenter(this, this.configuration.context);
        return dataCenter;
    }

    public IImageLoader getImageLoader() {
        checkConfig();
        return this.configuration.imageLoader;
    }

    public Context getContext() {
        checkConfig();
        return this.configuration.context;
    }

    public String getVersion() {
        checkConfig();
        return SDK_VERSION;
    }

    public String getAppUUID() {
        checkConfig();
        return this.configuration.appUUID;
    }

    public String getUserEmail() {
        checkConfig();
        return this.configuration.userEmail;
    }

    public String getUserPassword() {
        checkConfig();
        return this.configuration.userSha1Password;
    }

    private void checkConfig() {
        if (configuration == null) throw new PPMessageException(CONFIGURATION_EMPTY_LOG);
    }

}
