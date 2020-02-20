package cn.bdqfork.web.service;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

/**
 * @author bdq
 * @since 2020/1/26
 */
public class JsonMessageCodec implements MessageCodec<Object, Object> {
    public static final String NAME = "json";

    @Override
    public void encodeToWire(Buffer buffer, Object o) {
        buffer.appendBuffer(Json.encodeToBuffer(o));
    }

    @Override
    public Object decodeFromWire(int pos, Buffer buffer) {
        return Json.decodeValue(buffer);
    }

    @Override
    public Object transform(Object o) {
        return o;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
