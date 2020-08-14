package cn.oever.example.service;

import cn.oever.signature.service.BaseSignedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Service
public class CustomSignedService extends BaseSignedService {

    private static final Logger logger = LoggerFactory.getLogger(CustomSignedService.class);

    @Override
    public void entry(Object obj) throws Exception {
        //  This method is an entry method, and other methods are called.
        //  If your signature verification only requires one or a few steps,
        //  or if your method parameters are different and you need to call a custom method,
        //  you can override this method
        logger.info("The customize entry...");
        super.entry(obj);
    }

    @Override
    public String getAppSecret(String appId) {
        //  You can override this method to change the way of getting appSecret,
        //  such as using other caches, Memcached, etc.
        logger.info("The customize getAppSecretByAppId...");
        return super.getAppSecret(appId);
    }


    @Override
    public void isTimeDiffLarge(long timestamp) {
        //  his method is used to determine
        //  whether the time difference between the client and the server is too large
        logger.info("The customize isTimeDiffLarge...");
        super.isTimeDiffLarge(timestamp);
    }

    @Override
    public void isReplayAttack(String appId, long timestamp, int nonce, String signature) {
        //  This method is used to determine whether the request is repeated or replay attack
        logger.info("The customize isReplayAttack...");
        super.isReplayAttack(appId, timestamp, nonce, signature);
    }

    @Override
    public String getSignature(String appId, Map map) throws NoSuchAlgorithmException, InvalidKeyException {
        //  By default, this method provides a set of signature verification methods,
        //  you can override this method to implement your own signature verification
        logger.info("The customize signatureVerification...");
        return super.getSignature(appId, map);
    }
}
