package com.tongda.commonutil.buffer

import com.tongda.commonutil.*
import java.nio.charset.Charset
import java.util.*

/**
 * Created by Zhou Jinlong on 2018/5/2.
 */
open class ByteBuf : AbstractByteBuf {

    companion object {
        private const val TAG = "ByteBuf"

        private var gbk = Charset.forName("gbk")
    }

    private var pos: Int = 0

    private var data: ByteArray

    var isBigEndian = false

    constructor() {
        data = ByteArray(DEFAULT_CAPACITY)
    }


    constructor(maxCapacity: Int) {
        this.data = ByteArray(maxCapacity)
    }

    constructor(data: ByteArray) {
        this.data = data
    }

    override fun reset(data: ByteArray, bigEndian: Boolean) {
        reset(data, 0, bigEndian)
    }

    override fun reset(data: ByteArray, pos: Int, bigEndian: Boolean) {
        this.data = data
        this.pos = pos
        this.isBigEndian = bigEndian
    }

    override fun peekUint16(): Int {
        return if (isBigEndian) {
            toInt(data[pos + 1], data[pos])
        } else {
            toInt(data[pos], data[pos + 1])
        }
    }

    override fun peekData(len: Int): ByteArray {
        return data.sliceArray(IntRange(pos, pos + len - 1))
    }

    override fun readBitAndNoSkip(bitPos: Int): Int {
        val byte = data[pos]

        return (byte.int() shr bitPos) and 0x01
    }

    override fun readBitAndSkipByte(bitPos: Int): Int {
        val result = readBitAndNoSkip(bitPos)
        pos++
        return result
    }

    override fun readByte(): Byte {
        return data[pos++]
    }

    override fun readByteWithLen1(): Byte {
        skip(1)
        return readByte()
    }

    override fun readByteWithLen2(): Byte {
        skip(2)
        return readByte()
    }

    override fun readByteWithLenPrefix(lenCount: Int): Byte {
        val len = readInt(lenCount)
        val result = data[pos]
        pos += len
        return result
    }

    override fun readBCD(): Int {
        return data[pos++].fromBCD()
    }

    override fun readBCDWithLenPrefix(lenCount: Int): Int {
        val len = readInt(2)
        val result = data[pos].fromBCD()
        pos += len
        return result
    }

    override fun readInt(len: Int): Int {
        var result = 0
        var count = 0
        if (isBigEndian) {
            var startPos = pos + len - 1
            while (count < len) {
                result += (data[startPos].int() shl (count * 8))
                startPos--
                count++
            }
            pos += len
        } else {
            while (count < len) {
                result += (data[pos].int() shl (count * 8))
                pos++
                count++
            }
        }
        return result
    }

    override fun readIntWithLenPrefix(lenCount: Int): Int {
        val len = readInt(lenCount)
        return readInt(len)
    }

    override fun readIntWithLen1(): Int {
        return readIntWithLenPrefix(1)
    }

    override fun readIntWithLen2(): Int {
        return readIntWithLenPrefix(2)
    }

    override fun readUint8(): Int {
        return data[pos++].int()
    }

    override fun readUint8WithLen1(): Int {
        skip(1)
        return readUint8()
    }

    override fun readUint8WithLen2(): Int {
        skip(2)
        return readUint8()
    }

    override fun readUint16(): Int {
        return readInt(2)
    }

    override fun readUint16WithLen1(): Int {
        skip(1)
        return readUint16()
    }

    override fun readUint16WithLen2(): Int {
        skip(2)
        return readUint16()
    }

    override fun readUint32(): Long {
        return readLong(4)
    }

    override fun readUint32WithLen1(): Long {
        skip(1)
        return readUint32()
    }

    override fun readUint32WithLen2(): Long {
        skip(2)
        return readUint32()
    }

    override fun readUint64(): Long {
        return readLong(8)
    }

    override fun readUint64WithLen1(): Long {
        skip(1)
        return readLong(8)
    }

    override fun readUint64WithLen2(): Long {
        skip(2)
        return readLong(8)
    }


    override fun readLong(len: Int): Long {
        var result = 0L
        var count = 0
        if (isBigEndian) {
            var startPos = pos + len - 1
            while (count < len) {
                result += (data[startPos].int() shl (count * 8))
                startPos--
                count++
            }
            pos += len
        } else {
            while (count < len) {
                val current = (data[pos++]).int().toLong() shl (count * 8)
                result += current
                count++
            }
        }
        return result
    }

    override fun readLongWithLenPrefix(lenCount: Int): Long {
        val len = readInt(lenCount)
        return readLong(len)
    }

    override fun readLongWithLen1(): Long {
        return readLongWithLenPrefix(1)
    }

    override fun readLongWithLen2(): Long {
        return readLongWithLenPrefix(2)
    }

    override fun readByteArray(count: Int): ByteArray {
        val start = pos
        val result = data.sliceArray(IntRange(start, start + count - 1))
        pos += count
        return result
    }

    override fun readByteArrayHex(count: Int, separator: CharSequence): String {
        return readByteArray(count).joinToString(separator) { it.toHex() }
    }

    override fun readByteArrayHexWithLen1(separator: CharSequence): String {
        val len = readUint16()
        return readByteArrayHex(len, separator)
    }

    override fun readByteArrayHexWithLen2(separator: CharSequence): String {
        val len = readUint16()
        return readByteArrayHex(len, separator)
    }

    override fun readUtf8WithLenPrefix(lenCount: Int): String {
        val len = readInt(lenCount)
        return readUtf8(len)
    }

    override fun readUtf8(len: Int): String {
        val startPos = pos
        pos += len

        return data.fromUtf8(startPos, len)
    }

    override fun readUtf8TrimEnd(len: Int, trimByte: Byte): String {
        val startPos = pos


        val index = data.indexOf(trimByte, startPos, len)
        val l = if (index != -1) {
            len
        } else {
            index - startPos
        }

        pos += len
        return data.fromUtf8(startPos, l)
    }

    override fun readGbk(len: Int): String {
        val startPos = pos
        pos += len
        return data.fromGbk(startPos, len)
    }

    override fun readGbkWithLenPrefix(lenCount: Int): String {
        val len = readInt(lenCount)
        return readGbk(len)
    }

    override fun readGbkWithLen1(): String {
        return readGbkWithLenPrefix(1)
    }

    override fun readGbkWithLen2(): String {
        return readGbkWithLenPrefix(2)
    }

    override fun readGbkTrimEnd(len: Int, trimByte: Byte): String {
        val result = data.fromGbkTrimEnd(pos, len, trimByte)
        pos += len
        return result
    }

    override fun readGbkTrimEndWithLen1(trimByte: Byte): String {
        val len = readUint8()
        return readGbkTrimEnd(len, trimByte)
    }

    override fun readGbkTrimEndWithLen2(trimByte: Byte): String {
        val len = readUint16()
        return readGbkTrimEnd(len, trimByte)
    }

    override fun readGbkEndedZero(): String {
        var index = data.indexOf(0x00.toByte(), pos)

        if (index == -1) {
            index = data.lastIndex
        }
        return readGbk(index - pos)
    }

    override fun readGbkTrimEndZeroWithLenPrefix(lenCount: Int): String {
        val len = readInt(lenCount)
        return readGbkTrimEnd(len, 0x00.toByte())
    }

    override fun readGbkTrimEndZeroWithLen1(): String {
        return readGbkTrimEndZeroWithLenPrefix(1)
    }

    override fun readGbkTrimEndZeroWithLen2(): String {
        return readGbkTrimEndZeroWithLenPrefix(2)
    }

    override fun writeByte(value: Byte) {
        ensureCapacity(1)
        internalWriteByte(value)
    }

    override fun setByte(value: Byte, pos: Int) {
        data[pos] = value
    }

    override fun pos(): Int {
        return pos
    }

    override fun setPos(pos: Int) {
        this.pos = pos
    }


    /**
     * 内部写入字节，不需要确认容量大小
     */
    private fun internalWriteByte(value: Byte) {
        data[pos++] = value
    }

    override fun writeInt(value: Int, len: Int) {
        ensureCapacity(len)
        internalWriteInt(value, len)
    }

    private fun internalWriteInt(value: Int, len: Int) {
        var count = 0
        if (len <= 0) {
            return
        }

        var i = pos
        var temp = value
        while (count < len) {
            data[i++] = temp.lowByte()
            temp = temp ushr 8
            count++
        }
        pos = i
    }

    override fun writeLong(value: Long, len: Int) {
        ensureCapacity(len)
        internalWriteLong(value, len)
    }

    private fun internalWriteLong(value: Long, len: Int) {
        var count = 0
        if (len <= 0) {
            return
        }
        var temp = value
        while (count < len) {
            internalWriteByte(temp.lowByte())
            temp = temp ushr 8
            count++
        }
    }

    override fun writeByteArrayWithLenPrefix(value: ByteArray, lenCount: Int, occupied: Byte) {
        ensureCapacity(lenCount + value.count())
        internalWriteInt(value.count(), lenCount)
        internalWriteByteArray(value, lenCount, occupied)
    }

    override fun writeByteArray(value: ByteArray) {
        ensureCapacity(value.count())
        internalWriteByteArray(value, value.count())

    }

    override fun writeByteArray(value: ByteArray, len: Int, occupied: Byte) {
        ensureCapacity(len)
        internalWriteByteArray(value, len, occupied)
    }

    private fun internalWriteByteArray(value: ByteArray, len: Int, occupied: Byte = 0x00.toByte()) {
        val valueSize = value.count()
        for (i in 0 until len) {
            if (i < valueSize) {
                internalWriteByte(value[i])
            } else {
                internalWriteByte(occupied)
            }
        }

    }

    override fun writeString(value: String, len: Int, occupied: Byte, charset: Charset) {
        ensureCapacity(len)
        val byteArray = value.toByteArray(charset)
        internalWriteByteArray(byteArray, len, occupied)
    }

    override fun writeStringWithLenPrefix(content: String, lenCount: Int, charset: Charset) {
        val contentByteArray = content.toByteArray(charset)
        val contentLen = contentByteArray.count()

        ensureCapacity(lenCount + contentByteArray.count())

        internalWriteInt(contentLen, lenCount)
        internalWriteByteArray(contentByteArray, contentLen)
    }

    override fun writeGBKWithLen1(content: String) {
        val value = content.toByteArray(gbk)
        val len = value.count()
        ensureCapacity(1 + len)

        internalWriteInt(len, 1)
        internalWriteByteArray(value, len)
    }

    override fun writeCount(): Int {
        return pos
    }

    override fun skip(count: Int) {
        pos += count
    }

    override fun skipWithLenPrefix(lenCount: Int) {
        val len = readInt(lenCount)
        skip(len)
    }

    override fun skipWithLen1() {
        skipWithLenPrefix(1)
    }

    override fun skipWithLen2() {
        skipWithLenPrefix(2)
    }

    open fun ensureCapacity(extra: Int) {
        if (pos + extra > data.count()) {
            val newCapacity = (pos + extra) * 4 / 3 + 1
            data = Arrays.copyOf(data, newCapacity)
        }
    }

    override fun getData(start: Int, len: Int): ByteArray {
        return data.sliceArray(IntRange(start, start + len - 1))
    }

    override fun getData(): ByteArray {
        return data
    }

    override fun setData(byteArray: ByteArray) {
        this.data = byteArray
    }
}