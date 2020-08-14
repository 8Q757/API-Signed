package cn.oever.signature.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public void remove(String key) {
        if (exists(key)) {
            stringRedisTemplate.delete(key);
        }
    }

    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }


    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, Long expire) {
        stringRedisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }
}
