package com.ppmessage.ppcomlib.model;

import com.ppmessage.ppcomlib.PPComSDK;
import com.ppmessage.sdk.core.PPMessageSDK;
import com.ppmessage.sdk.core.model.MessagesModel;

/**
 * Created by ppmessage on 5/17/16.
 */
public class PPComMessagesModel extends MessagesModel {

    public PPComMessagesModel(PPComSDK sdk) {
        super(sdk.getConfiguration().getMessageSDK());
    }

}
