package com.tongda.commonutil

import java.lang.Math
import java.lang.StringBuffer
import java.lang.StringBuilder
import java.lang.System
import java.nio.charset.Charset
import kotlin.experimental.and
import kotlin.text.String
import kotlin.text.toInt
import kotlin.text.trim

/**
 * Created by chang on 2017-04-07.
 */


fun (Byte).int(): Int = this.toInt() and 0x00FF

fun (Byte).long(): Long = this.toLong() and 0x7FFFFFFFFFFFFFFF

fun Byte.toHex(): String = String.format("%02X", this)

fun Int.toHex(): String = Integer.toHexString(this)

fun Long.toHex(): String = java.lang.Long.toHexString(this)

fun String.fromHexString(): ByteArray {
    if (this.isBlank()) {
        return byteArrayOf()
    }
    return this.split("\\s+").map { it.toInt(16).toByte() }.toByteArray()
}

fun String.fromHex(): Int = this.toInt(16)

fun Byte.fromBCD(): Int {
    return ((this and 0xF0.toByte()).int() ushr 4) * 10 + (this and 0x0F.toByte()).int()
}


fun Float.round(): Int {
    return Math.round(this)
}

fun ByteArray.toHex() = this.joinToString(" ") { String.format("%02X", it) }

fun ByteArray.toHex(startIndex: Int, len: Int): String {
    if (startIndex > this.lastIndex) {
        return ""
    }
    val sb = StringBuilder()
    var end = startIndex + len
    if (end > this.count()) {
        end = this.count()
    }
    for (i in startIndex until end) {
        sb.append(String.format("%02X ", this[i]))
    }
    return sb.toString().trim()
}

fun ByteArray.tailHexFrom(startIndex: Int): String {
    val len = this.count() - startIndex
    if (len <= 0) {
        return ""
    }
    return this.toHex(startIndex, len)
}

fun ByteArray.trim(trimByte: Byte? = null): ByteArray {
    return trim(0, count(), trimByte)
}

fun ByteArray.trim(startPos: Int, len: Int, trimByte: Byte?): ByteArray {
    if (trimByte == null) {
        return this.copyOfRange(startPos, startPos + len)
    }
    var startIndex = startPos
    var endIndex = startPos + len - 1
    var startFound = false

    while (startIndex <= endIndex) {
        val index = if (!startFound) startIndex else endIndex
        val match = this[index] == trimByte

        if (!startFound) {
            if (!match)
                startFound = true
            else
                startIndex += 1
        } else {
            if (!match)
                break
            else
                endIndex -= 1
        }
    }

    return this.copyOfRange(startIndex, endIndex + 1)
}


fun ByteArray.toString(offset: Int, len: Int, charset: Charset = Charset.forName("UTF8"), trimByte: Byte? = null): String {
    if (trimByte == null) {
        return String(this, offset, len, charset)
    }

    var startIndex = offset
    var endIndex = offset + len - 1
    var startFound = false

    while (startIndex <= endIndex) {
        val index = if (!startFound) startIndex else endIndex
        val match = this[index] == trimByte

        if (!startFound) {
            if (!match)
                startFound = true
            else
                startIndex += 1
        } else {
            if (!match)
                break
            else
                endIndex -= 1
        }
    }

    return String(this, startIndex, endIndex - startIndex + 1, charset)
}

fun ByteArray.fromGbkTrimEnd(offset: Int, len: Int, trimByte: Byte? = null): String {
    return this.toString(offset, len, Charset.forName("gbk"), trimByte)
}

fun ByteArray.fromGbkTrimEndZero(offset: Int, len: Int): String {
    return this.toString(offset, len, Charset.forName("gbk"), 0x00.toByte())
}

fun ByteArray.toHex(len: Int): String {
    return this.sliceArray(kotlin.ranges.IntRange(0, len - 1)).toHex()
}

fun (String).unicode2Gbk(): String {
    var index = 0
    val buffer = StringBuffer()

    val li_len = this.length
    while (index < li_len) {
        if (index >= li_len - 1 || "\\u" != this.substring(index, index + 2)) {
            buffer.append(this[index])

            index++
            continue
        }

        var charStr = this.substring(index + 2, index + 6)

        val letter = Integer.parseInt(charStr, 16).toChar()

        buffer.append(letter)
        index += 6
    }

    return buffer.toString()

}

fun (String).toGBKByteArray(): ByteArray {
    return this.toByteArray(java.nio.charset.Charset.forName("gbk"))
}

fun Double.round(): Long {
    return Math.round(this)
}

fun Double.ceil(): Int {
    return Math.ceil(this).toInt()
}

fun Float.ceil(): Int {
    return Math.ceil(this.toDouble()).toInt()
}


fun ByteArray.indexOf(element: Byte, startIndex: Int): Int {
    for (index in startIndex until count()) {
        if (element == this[index]) {
            return index
        }
    }
    return -1
}

fun ByteArray.indexOf(element: Byte, startIndex: Int, len: Int): Int {
    for (index in startIndex until startIndex + len) {
        if (element == this[index]) {
            return index
        }
    }
    return -1
}


@Deprecated("This cause to allocate memory", ReplaceWith("ByteArray.fromGbk(offset:int,len:Int)"))
fun ByteArray.fromGbk(): String = String(this, Charset.forName("gbk")).trim()

fun ByteArray.fromGbk(offset: Int, len: Int): String {
    return String(this, offset, len, Charset.forName("gbk")).trim()
}

fun ByteArray.fromGbk(offset: Int, endByte: Byte): String {
    val index = indexOf(endByte, offset)

    val len = if (index == -1) {
        lastIndex + 1 - offset
    } else {
        index + 1 - offset
    }
    return fromGbk(offset, len)
}


fun ByteArray.fromGbk(offset: Int, len: Int, trimByte: Byte?) {

}

@Deprecated("This cause to allocate memory", ReplaceWith("ByteArray.fromUtf8(offset:int,len:Int)"))
fun ByteArray.fromUtf8(): String = String(this)


fun ByteArray.fromUtf8(offset: Int, len: Int): String {
    return String(this, offset, len)
}


fun ByteArray.toText() = String(this)

fun ByteArray.fromHex() = String(this).toInt(16) and 0x00FFFF
fun ByteArray.toInt() = String(this).toInt() and 0x00FFFF

fun getInt(low: Byte, high: Byte) = (low.toInt() and 0x00FF) + ((high.toInt() shl 8) and 0x00FF00)

fun toInt(low: Byte, high: Byte): Int = (low.toInt() and 0x00FF) + ((high.toInt() shl 8) and 0x00FF00)
fun toLong(low1: Byte, low2: Byte, high1: Byte, high2: Byte) = low1.int() + ((low2.toInt() shl 8) and 0x00FF00) + ((high1.toInt() shl 16) and 0x00FF0000) + ((high2.toLong() shl 24) and 0x00FF000000)


fun (ByteArray).fromTimeStamp(): Long {
    return ((this[7].toLong() shl 56) and 0x7F00000000000000) +
            ((this[6].toLong() shl 48) and 0x00FF000000000000) +
            ((this[5].toLong() shl 40) and 0x0000FF0000000000) +
            ((this[4].toLong() shl 32) and 0x000000FF00000000) +
            ((this[3].toLong() shl 24) and 0x00000000FF000000) +
            ((this[2].toLong() shl 16) and 0x0000000000FF0000) +
            ((this[1].toLong() shl 8) and 0x0000000000000FF00) +
            ((this[0].toLong()) and 0x00000000000000FF)
}

fun Long.toTimeStampByteArray(): ByteArray {
    return byteArrayOf(
            this.toByte(),
            (this ushr 8).toByte(),
            (this ushr 16).toByte(),
            (this ushr 24).toByte(),
            (this ushr 32).toByte(),
            (this ushr 40).toByte(),
            (this ushr 48).toByte(),
            (this ushr 56).toByte()
    )
}

/**
 * 从[ByteArray]中获取时间戳，精确到毫米
 * @param fromIndex [ByteArray]中时间戳的起始字节
 */
fun (ByteArray).toTime(fromIndex: Int): Long {
    return this.sliceArray(kotlin.ranges.IntRange(fromIndex, fromIndex + 8 - 1)).fromTimeStamp()
}

fun Int.lowByte(): Byte {
    return (this and 0x00FF).toByte()
}

fun Int.highByte(): Byte {
    return ((this ushr 8) and 0x00FF).toByte()
}

fun Long.lowByte(): Byte {
    return (this and 0x00FF).toByte()
}


fun ByteArray.feed(startIndex: Int, feedByteArray: ByteArray): Int {
    return feed(startIndex, feedByteArray, feedByteArray.count())
}

fun ByteArray.feed(startIndex: Int, feedByteArray: ByteArray, len: Int): Int {
    System.arraycopy(feedByteArray, 0, this, startIndex, len)
    return len
}

fun ByteArray.feed(startIndex: Int, content: String, lenCount: Int, charset: Charset = Charsets.UTF_8): Int {
    var i = startIndex
    val contentByteArray = content.toByteArray(charset)
    val contentLen = contentByteArray.count()
    i = this.feed(i, contentLen, lenCount)
    i = this.feed(i, contentByteArray)
    return i
}

fun ByteArray.feed(startIndex: Int, feedInt: Int, len: Int): Int {
    var i = startIndex
    var count = 0
    if (len <= 0) {
        return 0
    }

    var temp = feedInt
    while (count < len) {
        this[i++] = temp.lowByte()
        temp = temp ushr 8
        count++
    }
    return i
}

fun ByteArray.feed(startIndex: Int, feedLong: Long, len: Int): Int {
    var i = startIndex
    var count = 0
    if (len <= 0) {
        return 0
    }

    var temp = feedLong
    while (count < len) {
        this[i++] = temp.lowByte()
        temp = temp ushr 8
        count++
    }
    return i
}


fun ByteArray.readInt(startIndex: Int, len: Int): Int {
    var result = 0
    var count = 0
    var i = startIndex
    while (count < len) {
        result += (this[i].int() shl (count * 8))
        i++
        count++
    }
    return result
}


fun ByteArray.readUtf8(startIndex: Int, len: Int): String {
    val bytes = this.sliceArray(kotlin.ranges.IntRange(startIndex, startIndex + len - 1))
    return bytes.fromUtf8()
}

fun <T> List<T>.zipWith(comparator: (previous: T, current: T) -> Boolean): List<List<T>> {
    if (this.isEmpty()) {
        return listOf()
    }
    val l = mutableListOf<List<T>>()
    var ll = mutableListOf<T>()
    var t = this.first()
    ll.add(t)
    for (i in 1 until count()) {
        if (comparator(t, this[i])) {
            ll.add(this[i])
        } else {
            l.add(ll)
            ll = mutableListOf()
            t = this[i]
            ll.add(this[i])
        }
    }
    l.add(ll)
    return l
}

fun <T : Number> getValue(initValue: T, condValue: T, cond: (a: T, condValue: T) -> Boolean): T {
    return if (cond(initValue, condValue)) {
        initValue
    } else {
        condValue
    }
}

fun <T> T?.nullOrString(): String {
    return if (this == null) {
        "空"
    } else {
        this.toString()
    }
}
