package com.jinggang.downloaderserver.util

/**
 * Created by Zhou Jinlong on 2018/7/21.
 */

fun sleepIgnoreInterrupt(millis: Long) {
    try {
        Thread.sleep(millis)

    } catch (e: Exception) {
    }
}