package cn.oever.example.controller;

import cn.oever.example.entity.CustomParam;
import cn.oever.example.service.CustomSignedService;
import cn.oever.signature.annotation.SignedMapping;
import cn.oever.signature.entity.SignedParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("example")
@SignedMapping
public class ExampleController {

    private Logger logger = LoggerFactory.getLogger(ExampleController.class);

    @RequestMapping("base")
    public String base(@RequestBody SignedParam signedParam) {

        logger.info("The request data is :" + signedParam.getData());
        return "Base test is ok.";
    }

    @RequestMapping("param")
    @SignedMapping
    public String param(@RequestBody CustomParam customParam) {

        logger.info("The request data is :" + customParam.getParam2() + ", " + customParam.getParam3());
        return "Custom param test is ok.";
    }

    @RequestMapping("service")
    @SignedMapping(CustomSignedService.class)
    public String service(@RequestBody SignedParam signedParam) {

        logger.info("The request data is :" + signedParam.getData());
        return "Custom service test is ok.";
    }
}
