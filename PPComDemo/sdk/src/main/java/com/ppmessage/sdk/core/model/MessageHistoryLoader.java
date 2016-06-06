package com.ppmessage.sdk.core.model;

import com.ppmessage.sdk.core.L;
import com.ppmessage.sdk.core.bean.message.PPMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * for {@link MessageHistorysModel} wrapper class
 *
 * Compared with {@link MessageHistorysModel}, {@link MessageHistoryLoader} will record the last history pageIndex. you can get
 * last history pageIndex by calling {@link MessageHistoryLoader#getLastHistoryPageIndex(String)} method
 *
 * Created by ppmessage on 5/18/16.
 */
public class MessageHistoryLoader {

    private static final String LOG_LOAD = "load historys: pageOffset:%d, maxUUID:%s";

    private MessageHistorysModel historysModel;

    private Map<String, HistoryPageIndex> historyPageIndexMap;

    public MessageHistoryLoader(MessageHistorysModel historysModel) {
        this.historysModel = historysModel;
        this.historyPageIndexMap = new HashMap<>();
    }

    public void loadHistorys(final MessageHistorysModel.MessageHistoryRequestParam requestParam, final MessageHistorysModel.OnLoadHistoryEvent event) {
        L.d(LOG_LOAD, requestParam.pageOffset, requestParam.maxUUID);

        final String conversationUUID = requestParam.conversationUUID;
        historysModel.loadHistorys(requestParam, new MessageHistorysModel.OnLoadHistoryEvent() {
            @Override
            public void onCompleted(HistoryPageIndex pageIndex, List<PPMessage> messageList) {
                if (messageList != null) {
                    historyPageIndexMap.put(conversationUUID, pageIndex);
                }
                if (event != null) {
                    event.onCompleted(pageIndex, messageList);
                }
            }
        });
    }

    /**
     * return last history page index which associated with conversaitonUUID
     *
     * @param conversationUUID
     * @return
     */
    public HistoryPageIndex getLastHistoryPageIndex(String conversationUUID) {
        if (historyPageIndexMap.containsKey(conversationUUID)) {
            return historyPageIndexMap.get(conversationUUID);
        }
        return null;
    }

    public MessageHistorysModel getHistorysModel() {
        return this.historysModel;
    }

}
