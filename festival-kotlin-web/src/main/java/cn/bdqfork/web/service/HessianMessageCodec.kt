package cn.bdqfork.web.service

import com.caucho.hessian.io.Hessian2Input
import com.caucho.hessian.io.Hessian2Output
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * @author bdq
 * @since 2020/1/26
 */
class HessianMessageCodec : MessageCodec<Any?, Any?> {
    override fun encodeToWire(buffer: Buffer, o: Any?) {
        val os = ByteArrayOutputStream()
        val output = Hessian2Output(os)
        try {
            output.writeObject(o)
            output.close()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
        val bytes = os.toByteArray()
        buffer.appendInt(bytes.size)
        buffer.appendBytes(bytes)
    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): Any? {
        var postion = pos
        val length = buffer.getInt(pos)
        postion += 4
        val bytes = buffer.slice(postion, length).bytes
        val bis = ByteArrayInputStream(bytes)
        val input = Hessian2Input(bis)
        return try {
            input.readObject()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    override fun transform(o: Any?): Any? {
        return o
    }

    override fun name(): String {
        return NAME
    }

    override fun systemCodecID(): Byte {
        return -1
    }

    companion object {
        const val NAME = "hessian"
    }
}