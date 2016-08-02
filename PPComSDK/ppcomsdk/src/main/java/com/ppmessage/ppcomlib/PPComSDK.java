package com.ppmessage.ppcomlib;

import com.ppmessage.ppcomlib.services.PPComStartupHelper;
import com.ppmessage.ppcomlib.services.message.IMessageService;
import com.ppmessage.ppcomlib.services.message.MessageService;
import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.PPMessageSDKConfiguration;
import com.ppmessage.sdk.core.bean.message.PPMessage;

/**
 * Example:
 * <p>
 * <pre>
 *     PPComSDK sdk = PPComSDK.getInstance();
 *     sdk.init(new PPComSDKConfiguration.Builder().setAppUUID("YOUR_APP_UUID").build());
 *
 *     sdk.getStartupHelper().startUp(new PPComStartupHelper.OnStartCallback() {
 *
 *         public void onSuccess() {
 *
 *         }
 *
 *         public void onError(PPComSDKException exception) {
 *
 *         }
 *     });
 * </pre>
 * <p>
 * Created by ppmessage on 5/13/16.
 */
public class PPComSDK {

    private static final String CONFIG_ERROR_LOG = "[PPComSDK] can not be initialized with empty config";
    private static final String VERSION = "0.0.1";

    private static PPComSDK ourInstance = null;

    private PPMessageSDK ppMessageSDK = null;
    private PPComSDKConfiguration configuration;
    private PPComStartupHelper startupHelper;
    private IMessageService messageService;

    private PPComSDK() {
    }

    public static PPComSDK getInstance() {
        if (ourInstance == null) {
            ourInstance = new PPComSDK();
        }
        return ourInstance;
    }

    public String getVersion() {
        return VERSION;
    }

    public PPComSDKConfiguration getConfiguration() {
        return configuration;
    }

    public synchronized void init(PPComSDKConfiguration config) {
        if (config == null) {
            throw new PPComSDKException(CONFIG_ERROR_LOG);
        }
        this.configuration = config;
        initPPMessageSDK(config);
    }

    public synchronized void update(PPComSDKConfiguration configuration) {
        if (configuration == null) {
            throw new PPComSDKException(CONFIG_ERROR_LOG);
        }
        this.configuration.update(configuration);
        updatePPMessageSDK(configuration);
    }

    public PPMessageSDK getPPMessageSDK() {
        if (this.ppMessageSDK == null) {
            this.ppMessageSDK = PPMessageSDK.getInstance();
        }
        return this.ppMessageSDK;
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

    private void initPPMessageSDK(PPComSDKConfiguration configuration) {
        if (this.ppMessageSDK == null) {
            this.ppMessageSDK = PPMessageSDK.getInstance();
        }

        PPMessageSDKConfiguration.Builder builder = new PPMessageSDKConfiguration.Builder();
        builder.setEnableLogging(true).setEnableDebugLogging(true);

        this.ppMessageSDK.init(builder

                .setContext(configuration.getContext())
                .setAppUUID(configuration.getAppUUID())
                .setServerUrl(configuration.getServerUrl())
                .setPpcomApiKey(configuration.getApiKey())
                .setPpcomApiSecret(configuration.getApiSecret())

                .setEntUserUUID(configuration.getEntUserUUID())
                .setEntUserData(configuration.getEntUserData())
                .setEntUserType(configuration.getEntUserType())

                .setUserEmail(configuration.getUserEmail())
                .setUserFullName(configuration.getUserFullName())
                .setUserIcon(configuration.getUserIcon())

                .setJpushRegistrationId(configuration.getJpushRegistrationId())
                .setInputHint(configuration.getInputHint())

                .setEnableDebugLogging(configuration.enableLog)

                .build());

    }

    private void updatePPMessageSDK(PPComSDKConfiguration configuration) {
        if (this.ppMessageSDK == null || this.configuration == null) {
            throw new PPComSDKException(CONFIG_ERROR_LOG);
        }

        PPMessageSDKConfiguration.Builder builder = new PPMessageSDKConfiguration.Builder();

        if (builder.getEntUserData() != null) {
            builder.setEntUserData(builder.getEntUserData());
        }

        if (configuration.getUserFullName() != null) {
            builder.setUserFullName(configuration.getUserFullName());
        }

        if (configuration.getUserIcon() != null) {
            builder.setUserIcon(configuration.getUserIcon());
        }

        if (configuration.getJpushRegistrationId() != null) {
            builder.setJpushRegistrationId(configuration.getJpushRegistrationId());
        }

        this.ppMessageSDK.update(builder.build());
    }
}
