package cn.bdqfork.cache.provider;

import cn.bdqfork.cache.constant.RedisProperty;
import cn.bdqfork.cache.util.SerializeUtil;
import cn.bdqfork.cache.util.VertxUtils;
import cn.bdqfork.context.aware.ResourceReaderAware;
import cn.bdqfork.context.configuration.reader.ResourceReader;
import cn.bdqfork.core.exception.BeansException;
import io.vertx.core.buffer.Buffer;

import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class RedisCacheProvider extends AbstractCacheProvider implements ResourceReaderAware {

    private RedisClient redisClient;
    private ResourceReader resourceReader;

//    {
//        String host = resourceReader.readProperty(RedisProperty.REDIS_HOST, String.class, "localhost:6379");
//        String password = resourceReader.readProperty(RedisProperty.REDIS_PASSWORD, String.class);
//        int port = 6379;
//
//        String[] hostAndPort = host.split(":");
//        if (hostAndPort.length == 2){
//            host = hostAndPort[0];
//            port = Integer.parseInt(hostAndPort[1]);
//        }
//
//        RedisOptions config = new RedisOptions().setHost(host).setPort(port);
//        redisClient = RedisClient.create(VertxUtils.getVertx(), config);
//    }


    @Override
    public void put(String key, Serializable value, long expireTime) {
        redisClient.setBinary(key, Buffer.buffer(SerializeUtil.serialize(value)), event -> {
            if (event.failed()) {
                System.out.println("fail");
            }
        });
        redisClient.expire(key, expireTime, event -> {
            if (event.failed()) {
                System.out.println("fail");
            }
        });
    }

    @Override
    public void remove(String key) {
        redisClient.del(key, event -> {
            if (event.failed()) {
                System.out.println("fail");
            }
        });
    }

    @Override
    public Object update(String key, Serializable value, long expireTime) {
        final Object[] res = new Object[1];
        redisClient.get(key, event -> {
            if (event.succeeded()) {
                redisClient.setBinary(key, Buffer.buffer(SerializeUtil.serialize(value)), event1 -> {
                    if (event.failed()) {
                        System.out.println("fail");
                    }
                });
                res[0] = event.result();
            }
        });
        return res[0];
    }

    @Override
    public Object get(String key) {
        final Object[] res = new Object[1];
        redisClient.getBinary(key, event -> {
            if (event.succeeded()) {
                res[0] = SerializeUtil.unserialize(event.result().getBytes());
            }
        });
        return res[0];
    }

    @Override
    public boolean containKey(String key) {
        AtomicBoolean res = new AtomicBoolean(false);
        redisClient.exists(key, event -> {
            if (event.succeeded()) {
                if (event.result() > 0) {
                    res.set(true);
                } else {
                    res.set(false);
                }
            }
        });
        return res.get();
    }

    @Override
    public void clear() {
        redisClient.flushall(event -> {
            if (event.succeeded()) {
                System.out.println("OK");
            }
        });
    }

    @Override
    public void setResourceReader(ResourceReader resourceReader) throws BeansException {
        this.resourceReader = resourceReader;
    }
}
