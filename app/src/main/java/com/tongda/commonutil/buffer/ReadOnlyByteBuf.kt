package com.tongda.commonutil.buffer

import java.nio.charset.Charset

/**
 * Created by Zhou Jinlong on 2018/5/2.
 */
open class ReadOnlyByteBuf : ByteBuf(1) {

    override fun writeByte(value: Byte) {
        throw RuntimeException("writeByte not enabled in ReadOnlyByteBuf")
    }

    override fun writeInt(value: Int, len: Int) {
        throw RuntimeException("writeInt not enabled in ReadOnlyByteBuf")
    }

    override fun writeLong(value: Long, len: Int) {
        throw RuntimeException("writeLong not enabled in ReadOnlyByteBuf")
    }

    override fun writeByteArrayWithLenPrefix(value: ByteArray, lenCount: Int, occupied: Byte) {
        throw RuntimeException("writeByteArrayWithLenPrefix not enabled in ReadOnlyByteBuf")
    }

    override fun writeByteArray(value: ByteArray) {
        throw RuntimeException("writeByteArray not enabled in ReadOnlyByteBuf")
    }

    override fun writeStringWithLenPrefix(content: String, lenCount: Int, charset: Charset) {
        throw RuntimeException("writeStringWithLenPrefix not enabled in ReadOnlyByteBuf")
    }

    override fun writeCount(): Int {
        throw RuntimeException("writeCount not enabled in ReadOnlyByteBuf")
    }

    override fun ensureCapacity(extra: Int) {
        throw RuntimeException("ensureCapacity not enabled in ReadOnlyByteBuf")
    }
}