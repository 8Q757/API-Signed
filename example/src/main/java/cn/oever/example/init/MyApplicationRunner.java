package cn.oever.example.init;

import cn.oever.example.constant.Constant;
import cn.oever.signature.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private RedisUtil redisUtil;
    private Logger logger = LoggerFactory.getLogger(MyApplicationRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initAppId();
    }

    public void initAppId() {
        // You can initialize the cache here,
        // for example, write the appIds and appSecrets to redis from db
        logger.info("Initialization the appId and appSecret...");
        redisUtil.set(Constant.APP_ID, Constant.APP_SECRET);
    }
}
