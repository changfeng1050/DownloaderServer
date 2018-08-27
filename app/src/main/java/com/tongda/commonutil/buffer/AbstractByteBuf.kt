package com.tongda.commonutil.buffer

import java.nio.charset.Charset

/**
 * Created by Zhou Jinlong on 2018/5/2.
 */
abstract class AbstractByteBuf {

    companion object {
        const val DEFAULT_CAPACITY = 4096

        val EMPTY_BYTE_ARRAY = ByteArray(0)
    }

    abstract fun reset(data: ByteArray, bigEndian: Boolean = false)

    abstract fun reset(data: ByteArray, pos: Int, bigEndian: Boolean = false)

    abstract fun peekUint16(): Int

    abstract fun peekData(len: Int): ByteArray

    abstract fun readBitAndSkipByte(bitPos: Int): Int

    abstract fun readBitAndNoSkip(bitPos: Int): Int

    abstract fun readByte(): Byte

    abstract fun readByteWithLenPrefix(lenCount: Int): Byte

    abstract fun readByteWithLen1(): Byte

    abstract fun readByteWithLen2(): Byte

    abstract fun readBCD(): Int

    abstract fun readBCDWithLenPrefix(lenCount: Int): Int

    abstract fun readInt(len: Int): Int

    abstract fun readIntWithLen1(): Int

    abstract fun readIntWithLen2(): Int

    abstract fun readUint8(): Int

    abstract fun readUint8WithLen1(): Int

    abstract fun readUint8WithLen2(): Int

    abstract fun readUint16(): Int

    abstract fun readUint16WithLen1(): Int

    abstract fun readUint16WithLen2(): Int

    abstract fun readUint32(): Long

    abstract fun readUint32WithLen1(): Long

    abstract fun readUint32WithLen2(): Long

    abstract fun readUint64(): Long

    abstract fun readUint64WithLen1(): Long

    abstract fun readUint64WithLen2(): Long

    abstract fun readIntWithLenPrefix(lenCount: Int): Int


    abstract fun readLong(len: Int): Long

    abstract fun readLongWithLenPrefix(lenCount: Int): Long

    abstract fun readLongWithLen1(): Long

    abstract fun readLongWithLen2(): Long

    abstract fun readByteArray(count: Int): ByteArray

    abstract fun readByteArrayHex(count: Int, separator: CharSequence = ""): String

    abstract fun readByteArrayHexWithLen1(separator: CharSequence = ""): String

    abstract fun readByteArrayHexWithLen2(separator: CharSequence = ""): String

    abstract fun readUtf8WithLenPrefix(lenCount: Int): String

    abstract fun readUtf8(len: Int): String

    abstract fun readUtf8TrimEnd(len: Int, trimByte: Byte): String

    abstract fun readGbk(len: Int): String

    abstract fun readGbkWithLenPrefix(lenCount: Int): String

    abstract fun readGbkWithLen1(): String

    abstract fun readGbkWithLen2(): String

    abstract fun readGbkTrimEnd(len: Int, trimByte: Byte): String

    abstract fun readGbkTrimEndWithLen1(trimByte: Byte): String

    abstract fun readGbkTrimEndWithLen2(trimByte: Byte): String

    abstract fun readGbkEndedZero(): String

    abstract fun readGbkTrimEndZeroWithLenPrefix(lenCount: Int): String

    abstract fun readGbkTrimEndZeroWithLen1(): String

    abstract fun readGbkTrimEndZeroWithLen2(): String

    abstract fun writeByte(value: Byte)

    abstract fun setByte(value: Byte, pos: Int)

    abstract fun writeInt(value: Int, len: Int)

    abstract fun writeLong(value: Long, len: Int)

    abstract fun writeByteArrayWithLenPrefix(value: ByteArray, lenCount: Int, occupied: Byte = 0x00.toByte())

    abstract fun writeByteArray(value: ByteArray)

    abstract fun writeByteArray(value: ByteArray, len: Int, occupied: Byte = 0x00.toByte())

    abstract fun writeString(value: String, len: Int, occupied: Byte = 0x00.toByte(), charset: Charset = Charsets.UTF_8)

    abstract fun writeStringWithLenPrefix(content: String, lenCount: Int, charset: Charset = Charsets.UTF_8)

    abstract fun writeGBKWithLen1(content: String)

    abstract fun writeCount(): Int

    abstract fun pos(): Int

    abstract fun setPos(pos: Int)

    abstract fun skip(count: Int)

    abstract fun skipWithLenPrefix(lenCount: Int)

    abstract fun skipWithLen1()

    abstract fun skipWithLen2()

    abstract fun getData(start: Int, len: Int): ByteArray

    abstract fun getData(): ByteArray

    abstract fun setData(byteArray: ByteArray)
}