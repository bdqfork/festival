package cn.bdqfork.kotlin.web.service

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.Json

/**
 * @author bdq
 * @since 2020/1/26
 */
class JsonMessageCodec : MessageCodec<Any?, Any?> {
    override fun encodeToWire(buffer: Buffer, o: Any?) {
        buffer.appendBuffer(Json.encodeToBuffer(o))
    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): Any? {
        return Json.decodeValue(buffer)
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
        const val NAME = "json"
    }
}