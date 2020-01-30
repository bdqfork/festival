package cn.bdqfork.web.context.service;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author bdq
 * @since 2020/1/26
 */
public class HessianMessageCodec implements MessageCodec<Object, Object> {
    public static final String NAME = "hessian";

    @Override
    public void encodeToWire(Buffer buffer, Object o) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(os);
        try {
            output.writeObject(o);
            output.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        byte[] bytes = os.toByteArray();
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);
    }

    @Override
    public Object decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        pos += 4;
        byte[] bytes = buffer.slice(pos, length).getBytes();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Hessian2Input input = new Hessian2Input(bis);
        try {
            return input.readObject();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
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
