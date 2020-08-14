package cn.oever.example.entity;

import cn.oever.signature.annotation.*;

@SignedEntity
public class CustomParam {
    @SignedAppId
    private String param1;
    private String param2;
    private String param3;
    @SignedTimestamp
    private long param4;
    @SignedNonce
    private int param5;
    @Signature
    private String param6;

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public long getParam4() {
        return param4;
    }

    public void setParam4(long param4) {
        this.param4 = param4;
    }

    public int getParam5() {
        return param5;
    }

    public void setParam5(int param5) {
        this.param5 = param5;
    }

    public String getParam6() {
        return param6;
    }

    public void setParam6(String param6) {
        this.param6 = param6;
    }

    @Override
    public String toString() {
        return "DemoEntity{" +
                "param1='" + param1 + '\'' +
                ", param2='" + param2 + '\'' +
                ", param3='" + param3 + '\'' +
                ", param4=" + param4 +
                ", param5=" + param5 +
                ", param6='" + param6 + '\'' +
                '}';
    }
}
