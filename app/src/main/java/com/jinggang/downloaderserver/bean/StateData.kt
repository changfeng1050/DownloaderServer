package com.jinggang.downloaderserver.bean

/**
 * Created by chang on 2017-05-13.
 */
data class StateData(val id: Int, val stateCode: Int, val info: String) {
    override fun toString(): String {
        return "[$id $stateCode $info]"
    }
}
