package cn.oever.signature.entity;

import cn.oever.signature.annotation.*;

@SignedEntity
public class SignedParam {
    @SignedAppId
    private String appId;
    private String data;
    @SignedTimestamp
    private long timestamp;
    @SignedNonce
    private int nonce;
    @Signature
    private String signature;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "ParamSigned{" +
                "appId='" + appId + '\'' +
                ", data='" + data + '\'' +
                ", timestamp=" + timestamp +
                ", nonce=" + nonce +
                ", signature='" + signature + '\'' +
                '}';
    }
}

