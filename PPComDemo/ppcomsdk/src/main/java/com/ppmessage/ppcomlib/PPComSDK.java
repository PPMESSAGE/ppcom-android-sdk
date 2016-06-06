package com.ppmessage.ppcomlib;

import com.ppmessage.ppcomlib.services.PPComStartupHelper;
import com.ppmessage.ppcomlib.services.message.IMessageService;
import com.ppmessage.ppcomlib.services.message.MessageService;
import com.ppmessage.sdk.core.L;

/**
 * Example:
 *
 * <pre>
 *     PPComSDK sdk = PPComSDK.getInstance();
 *     sdk.init(new PPComSDKConfiguration.Builder().setAppUUID("YOUR_APP_UUID").build());
 *
 *     sdk.getStartupHelper().startUp(new PPComStartupHelper.OnStartCallback() {
 *         @Override
 *         public void onSuccess() {
 *
 *         }
 *         @Override
 *         public void onError(PPComSDKException exception) {
 *
 *         }
 *     });
 * </pre>
 *
 * Created by ppmessage on 5/13/16.
 */
public class PPComSDK {

    private static final String CONFIG_ERROR_LOG = "[PPComSDK] can not be initialized with empty config";
    private static final String VERSION = "0.0.1";

    private static final PPComSDK ourInstance = new PPComSDK();

    private PPComSDKConfiguration configuration;
    private PPComStartupHelper startupHelper;
    private IMessageService messageService;

    public static PPComSDK getInstance() {
        return ourInstance;
    }

    public synchronized void init(PPComSDKConfiguration config) {
        if (config == null) throw new PPComSDKException(CONFIG_ERROR_LOG);
        this.configuration = config;
    }

    public PPComSDKConfiguration getConfiguration() {
        return configuration;
    }

    public String getVersion() {
        return VERSION;
    }

    public PPComStartupHelper getStartupHelper() {
        if (startupHelper == null) {
            startupHelper = new PPComStartupHelper(this);
        }
        return startupHelper;
    }

    public IMessageService getMessageService() {
        if (messageService == null) {
            messageService = new MessageService(this);
        }
        return messageService;
    }

}
