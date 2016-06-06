package com.ppmessage.sdk.core.model;

import java.util.Locale;

/**
 * Created by ppmessage on 5/17/16.
 */
public class HistoryPageIndex {

    private static final String FORMAT = "[HistoryPageIndex]: maxUUID:%s, pageOffset:%d, totalCount:%d";

    private String maxUUID;
    private int pageOffset;
    private int pageSize;
    private int totalCount;

    public HistoryPageIndex() {
    }

    public HistoryPageIndex(String maxUUID, int pageOffset, int pageSize, int totalCount) {
        this.maxUUID = maxUUID;
        this.pageOffset = pageOffset;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
    }

    public String getMaxUUID() {
        return maxUUID;
    }

    public void setMaxUUID(String maxUUID) {
        this.maxUUID = maxUUID;
    }

    public int getPageOffset() {
        return pageOffset;
    }

    public void setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), FORMAT,
                getMaxUUID(),
                getPageOffset(),
                getTotalCount());
    }
}
