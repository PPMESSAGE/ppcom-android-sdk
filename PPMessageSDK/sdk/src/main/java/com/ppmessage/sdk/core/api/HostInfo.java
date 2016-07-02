package com.ppmessage.sdk.core.api;

/**
 * Created by ppmessage on 5/19/16.
 */
public final class HostInfo {

    private String host;
    private String ppcomApiKey;
    private String ppcomApiSecret;
    private String ppkefuApiKey;
    private String wsHost;
    private String httpHost;
    private boolean ssl;
    private String downloadHost;
    private String uploadHost;

    public HostInfo(String ppkefuApiKey, String ppcomApiSecret, String ppcomApiKey, String host, boolean ssl) {
        this.ppkefuApiKey = ppkefuApiKey;
        this.ppcomApiSecret = ppcomApiSecret;
        this.ppcomApiKey = ppcomApiKey;
        this.host = host;
        this.ssl = ssl;

        if (this.ssl) {
            setHttpHost("https://" + host);
            setWsHost("wss://" + host + "/pcsocket/WS");
        } else {
            setHttpHost("http://" + host);
            setWsHost("ws://" + host + "/pcsocket/WS");
        }

        setDownloadHost(getHttpHost() + "/download/");
        setUploadHost(getHost() + "/upload");
    }

    public String getUploadHost() {
        return uploadHost;
    }

    public void setUploadHost(String uploadHost) {
        this.uploadHost = uploadHost;
    }

    public String getDownloadHost() {
        return downloadHost;
    }

    public void setDownloadHost(String downloadHost) {
        this.downloadHost = downloadHost;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPpcomApiKey() {
        return ppcomApiKey;
    }

    public void setPpcomApiKey(String ppcomApiKey) {
        this.ppcomApiKey = ppcomApiKey;
    }

    public String getPpcomApiSecret() {
        return ppcomApiSecret;
    }

    public void setPpcomApiSecret(String ppcomApiSecret) {
        this.ppcomApiSecret = ppcomApiSecret;
    }

    public String getPpkefuApiKey() {
        return ppkefuApiKey;
    }

    public void setPpkefuApiKey(String ppkefuApiKey) {
        this.ppkefuApiKey = ppkefuApiKey;
    }

    public String getWsHost() {
        return wsHost;
    }

    public void setWsHost(String wsHost) {
        this.wsHost = wsHost;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public void setHttpHost(String httpHost) {
        this.httpHost = httpHost;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    @Override
    public String toString() {
        return "HostInfo{" +
                "host='" + host + '\'' +
                ", ppcomApiKey='" + ppcomApiKey + '\'' +
                ", ppcomApiSecret='" + ppcomApiSecret + '\'' +
                ", ppkefuApiKey='" + ppkefuApiKey + '\'' +
                ", wsHost='" + wsHost + '\'' +
                ", httpHost='" + httpHost + '\'' +
                ", ssl=" + ssl +
                ", downloadHost='" + downloadHost + '\'' +
                ", uploadHost='" + uploadHost + '\'' +
                '}';
    }
}
