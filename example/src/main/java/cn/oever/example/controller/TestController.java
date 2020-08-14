package cn.oever.example.controller;

import cn.oever.example.constant.Constant;
import cn.oever.example.entity.CustomParam;
import cn.oever.example.service.CustomSignedService;
import cn.oever.signature.entity.SignedParam;
import cn.oever.signature.service.BaseSignedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

/**
 * Get signature for testing example api
 */
@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private BaseSignedService baseSignedService;
    @Autowired
    private CustomSignedService customSignedService;

    @RequestMapping("base")
    public SignedParam base(String data) throws Exception {

        SignedParam signedParam = new SignedParam();
        signedParam.setAppId(Constant.APP_ID);
        signedParam.setData(data);
        signedParam.setTimestamp(System.currentTimeMillis() / 1000);
        signedParam.setNonce(new Random().nextInt());

        Map map = baseSignedService.object2Map(signedParam);
        String signature = baseSignedService.getSignature(Constant.APP_ID, map);

        signedParam.setSignature(signature);
        return signedParam;
    }


    @RequestMapping("param")
    public CustomParam param(String param2, String param3) throws Exception {

        CustomParam customParam = new CustomParam();
        customParam.setParam1(Constant.APP_ID);
        customParam.setParam2(param2);
        customParam.setParam3(param3);
        customParam.setParam4(System.currentTimeMillis() / 1000);
        customParam.setParam5(new Random().nextInt());

        Map map = baseSignedService.object2Map(customParam);
        String signature = baseSignedService.getSignature(Constant.APP_ID, map);

        customParam.setParam6(signature);
        return customParam;
    }

    @RequestMapping("service")
    public SignedParam service(String data) throws Exception {

        SignedParam signedParam = new SignedParam();
        signedParam.setAppId(Constant.APP_ID);
        signedParam.setData(data);
        signedParam.setTimestamp(System.currentTimeMillis() / 1000);
        signedParam.setNonce(new Random().nextInt());

        Map map = customSignedService.object2Map(signedParam);
        String signature = customSignedService.getSignature(Constant.APP_ID, map);

        signedParam.setSignature(signature);
        return signedParam;
    }
}
