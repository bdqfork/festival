package cn.bdqfork.cache.provider;

import cn.bdqfork.cache.constant.RedisProperty;
import cn.bdqfork.cache.util.SerializeUtil;
import cn.bdqfork.cache.util.VertxUtils;
import cn.bdqfork.context.configuration.reader.ResourceReader;
import io.vertx.core.buffer.Buffer;

import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class RedisCacheProvider extends AbstractCacheProvider {

    private String host;
    private int port;
    private String password;
    private ResourceReader resourceReader;
    private RedisClient redisClient;

    public void connect(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
        host = resourceReader.readProperty(RedisProperty.REDIS_HOST, String.class, "localhost:6379");
        password = resourceReader.readProperty(RedisProperty.REDIS_PASSWORD, String.class);
        port = 6379;

        String[] hostAndPort = host.split(":");
        if (hostAndPort.length == 2){
            host = hostAndPort[0];
            port = Integer.parseInt(hostAndPort[1]);
        }

        RedisOptions config = new RedisOptions().setHost(host).setPort(port);
        if (password != null) {
            config.setAuth(password);
        }
        redisClient = RedisClient.create(VertxUtils.getVertx(), config);
    }


    @Override
    public void put(String key, Serializable value, long expireTime) {
        redisClient.setBinary(key, Buffer.buffer(SerializeUtil.serialize(value)), event -> {
            if (event.failed()) {
                throw new IllegalStateException(String.format("put %s failed", key));
            }
        });
        redisClient.expire(key, expireTime, event -> {
            if (event.failed()) {
                throw new IllegalStateException(String.format("set expire time for %s failed", key));
            }
        });
    }

    @Override
    public void remove(String key) {
        redisClient.del(key, event -> {
            if (event.failed()) {
                throw new IllegalStateException(String.format("remove %s failed", key));
            }
        });
    }

    @Override
    public Object update(String key, Serializable value, long expireTime) {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        final Object[] res = new Object[1];
        redisClient.get(key, event -> {
            if (event.succeeded()) {
                countDownLatch.countDown();
                redisClient.setBinary(key, Buffer.buffer(SerializeUtil.serialize(value)), event1 -> {
                    if (event.failed()) {
                        throw new IllegalStateException(String.format("update %s failed", key));
                    }
                    if (event.succeeded()) {
                        countDownLatch.countDown();
                    }
                });
                res[0] = event.result();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return res[0];
    }

    @Override
    public Object get(String key) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final Object[] res = new Object[1];
        redisClient.getBinary(key, event -> {
            if (event.succeeded()) {
                res[0] = SerializeUtil.unserialize(event.result().getBytes());
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return res[0];
    }

    @Override
    public boolean containKey(String key) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean res = new AtomicBoolean(false);
        redisClient.exists(key, event -> {
            if (event.succeeded()) {
                countDownLatch.countDown();
                if (event.result() > 0) {
                    res.set(true);
                } else {
                    res.set(false);
                }
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return res.get();
    }

    @Override
    public void clear() {
        redisClient.flushall(event -> {
            if (event.failed()) {
                throw new IllegalStateException("clear failed");
            }
        });
    }

}
