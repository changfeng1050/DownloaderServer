package com.jinggang.downloaderserver.service

import com.tongda.commonutil.L

/**
 * Created by chang on 2017-05-12.
 */
class FrameManager {

    companion object {
        val TAG: String = L.makeLogTag(FrameManager::class.java)

        var debug = false

        const val LEN_HEAD = 5
        const val LEN_END = 2

        const val BYTE_HEAD = 0x7E.toByte()
        const val BYTE_END = 0x7F.toByte()

        const val RESPONSE_CODE_ADD = 0x80.toByte()
        /**
         * 通用回复，成功
         */
        const val RESPONSE_STATE_SUCCESS = 0x00.toByte()
        /**
         * 通用回复，失败
         */
        const val RESPONSE_STATE_FAILED = 0x01.toByte()

        /**
         * 终端登陆帧
         */
        const val FRAME_LOGIN = 0x01.toByte()
        /**
         * 登陆通用回复帧
         */
        const val FRAME_LOGIN_RESPONSE = (FRAME_LOGIN + RESPONSE_CODE_ADD).toByte()
        /**
         * 时间同步帧
         */
        const val FRAME_SYNC_TIME = 0x02.toByte()
        /**
         * 时间同步通用回复帧
         */
        const val FRAEM_SYNC_TIME_RESPONSE = (FRAME_SYNC_TIME + RESPONSE_CODE_ADD).toByte()
        /**
         * 心跳帧
         */
        const val FRAME_HEART_BEAT = 0x03.toByte()
        /**
         * 心跳通用回复帧
         */
        const val FRAME_HEART_BEAT_RESPONSE = (FRAME_HEART_BEAT + RESPONSE_CODE_ADD).toByte()
        /**
         * 状态查询帧
         */
        const val FRAME_CHECK_STATE = 0x04.toByte()
        /**
         * 状态查询通用回复帧
         */
        const val FRAME_CHECK_STATE_RESPONSE = (FRAME_CHECK_STATE + RESPONSE_CODE_ADD).toByte()
        /**
         * 状态信息上报帧
         */
        const val FRAME_SEND_STATE = 0x05.toByte()
        /**
         * 状态信息上报通用回复帧
         */
        const val FRAME_SEND_STATE_RESPONSE = (FRAME_SEND_STATE + RESPONSE_CODE_ADD).toByte()
        /**
         * 远程控制帧
         */
        const val FRAME_REMOTE_CMD = 0x06.toByte()
        /**
         * 远程控制通用回复帧
         */
        const val FRAME_REMOTE_CMD_RESPONSE = (FRAME_REMOTE_CMD + RESPONSE_CODE_ADD).toByte()

        /**
         * 远程控制执行状态帧
         */
        const val FRAME_SEND_REMOTE_CMD_STATE = 0x07.toByte()
        /**
         * 远程控制执行状态通用回复帧
         */
        const val FRAME_SEND_REMOTE_CMD_STATE_RESPONSE = (FRAME_SEND_REMOTE_CMD_STATE + RESPONSE_CODE_ADD).toByte()

        /**
         * 通知下载文件帧
         */
        const val FRAME_REQUEST_DOWNLOAD_FILE = 0x08.toByte()
        /**
         * 通知下载文件通用回复帧
         */
        const val FRAME_REQUEST_DOWNLOAD_FILE_RESPONSE = (FRAME_REQUEST_DOWNLOAD_FILE + RESPONSE_CODE_ADD).toByte()
        /**
         * 下载文件状态帧
         */
        const val FRAME_SEND_DOWNLOAD_FILE_STATE = 0x09.toByte()
        /**
         * 下载文件状态回复帧
         */
        const val FRAME_SEND_DOWNLOAD_FILE_STATE_RESPONSE = (FRAME_SEND_DOWNLOAD_FILE_STATE + RESPONSE_CODE_ADD).toByte()
        /**
         * 通知上传文件帧
         */
        const val FRAME_REQUEST_UPLOAD_FILE = 0x0A.toByte()
        /**
         * 通知上传文件回复帧
         */
        const val FRAME_REQUEST_UPLOAD_FILE_RESPONSE = (FRAME_REQUEST_UPLOAD_FILE + RESPONSE_CODE_ADD).toByte()
        /**
         * 上传文件状态帧
         */
        const val FRAME_SEND_UPLOAD_FILE_STATE = 0x0B.toByte()
        /**
         * 上传文件状态回复帧
         */
        const val FRAME_SEND_UPLOAD_FILE_STATE_RESPONSE = (FRAME_SEND_UPLOAD_FILE_STATE + RESPONSE_CODE_ADD).toByte()
        /**
         * 通知删除文件帧
         */
        const val FRAME_REQUEST_DELETE_FILE = 0x0C.toByte()
        /**
         * 通知删除文件回复帧
         */
        const val FRAME_REQUEST_DELETE_FILE_RESPONSE = (FRAME_REQUEST_DELETE_FILE + RESPONSE_CODE_ADD).toByte()
        /**
         * 删除文件状态帧
         */
        const val FRAME_SEND_DELETE_FILE_STATE = 0x0D.toByte()
        /**
         * 删除文件状态回复帧
         */
        const val FRAME_SEND_DELETE_FILE_STATE_RESPONSE = (FRAME_SEND_DELETE_FILE_STATE + RESPONSE_CODE_ADD).toByte()
        /**
         * 下发通知帧
         */
        const val FRAME_NOTIFICATION = 0x0E.toByte()
        /**
         * 下发通知通用回复帧
         */
        const val FRAME_NOTIFICATION_RESPONSE = (FRAME_NOTIFICATION + RESPONSE_CODE_ADD).toByte()
        /**
         * 通知信息状态帧
         */
        const val FRAME_NOTIFICATION_STATE = 0x0F.toByte()
        /**
         * 通知信息状态通用回复帧
         */
        const val FRAME_NOTIFICATION_STATE_RESP0NSE = (FRAME_NOTIFICATION_STATE + RESPONSE_CODE_ADD).toByte()

        /**
         * 删除通知
         */
        const val FRAME_DELETE_NOTIFICATION = 0x10.toByte()

        /**
         *  信息下发
         */
        const val FRAME_SEND_MESSAGE = 0x40.toByte()

        /**
         * 请求下发线路营运信息
         */
        const val FRAME_REQUEST_ROUTE_SCHEDULE = 0x60.toByte()

        /**
         * 默认广告配置文件
         */
        const val FILE_TYPE_AD_DEFAULT = 1

        /**
         * 即时广告配置文件
         */
        const val FILE_TYPE_INSTANT_AD = 2
        /**
         * 广告配置文件
         */
        const val FILE_TYPE_NORMAL_AD = 3
        /**
         * 公告广告配置文件
         */
        const val FILE_TYPE_AD_ANNOUNCEMENT = 4
        /**
         * 终端软件配置文件
         */
        const val FILE_TYPE_APP_CONFIG = 5
        /**
         * 终端软件安装包
         */
        const val FILE_TYPE_APK = 6
        /**
         * Logo文件
         */
        const val FILE_TYPE_LOGO = 7
        /**
         * Splash文件
         */
        const val FILE_TYPE_SPLASH = 8
        /**
         * 广告播放统计文件
         */
        const val FILE_TYPE_AD_PLAY_STATISTICS = 9

        /**
         * 日志信息文件
         */
        const val FILE_TYPE_LOG = 10

        /**
         * 崩溃信息文件
         */
        const val FILE_TYPE_CRASH = 11
        /**
         * 线路文件
         */
        const val FILE_TYPE_ROUTE = 12

        /**
         * 线路站点换乘信息
         */
        const val FILE_TYPE_STATION_TRANSFER_INFO = 13

        /**
         * 文字广告文件
         */
        const val FILE_TYPE_TEXT_AD = 14

        /**
         * 下载文件状态码
         */
        const val DOWNLOAD_STATE_START = 0x00
        const val DOWNLOAD_STATE_TRANSFERRING = 0x01
        const val DOWNLOAD_STATE_COMPLETE = 0x02
        const val DOWNLOAD_STATE_ABORT = 0x03
        const val DOWNLOAD_STATE_FAILED = 0x04
        const val DOWNLOAD_STATE_PARAM_INVALID = 0x05
        const val DOWNLOAD_STATE_UNSUPPORTED_FILE_TYPE = 0x06
        const val DOWNLOAD_STATE_EXECUTE_UPDATE_ERROR = 0x07

        /**
         * 上传文件状态码
         */
        /**
         * 开始上传
         */
        const val UPLOAD_STATE_START = 0x00
        /**
         * 正在传输
         */
        const val UPLOAD_STATE_TRANSFERRING = 0x01
        /**
         * 传输完成
         */
        const val UPLOAD_STATE_COMPLETE = 0x02
        /**
         * 传输中断
         */
        const val UPLOAD_STATE_ABORT = 0x03
        /**
         * 传输失败
         */
        const val UPLOAD_STATE_FAILED = 0x04

        /**
         * 没有找到文件，不需要传输
         */
        const val UPLOAD_STATE_NO_FILE_FOUND = 0x05

        /**
         * 通知状态
         */
        const val NOTIFICATION_STATE_START = 0x00
        const val NOTIFICATION_STATE_END = 0x01

        /**
         * 状态查询类型
         */
        const val CHECK_STATE_TYPE_POSITIVE = 0x00 // 主动查询
        const val CHECK_STATE_TYPE_NEGATIVE = 0x01 // 被动查询


        /**
         * 状态编码
         */
        const val STATE_ID_DEVICE_ID = 0x01
        const val STATE_ID_DEVICE_ID_CODE_INVALID = 0x00
        const val STATE_ID_DEVICE_ID_CODE_VALID = 0x01

        /**
         * 消息类型
         */
//        const val MESSAGE_TYPE_UPDATE_TEXT_AD = 1
//        const val MESSAGE_TYPE_DELETE_TEXT_AD = 2
//        const val MESSAGE_TYPE_UPDATE_WEATHER = 3
//        const val MESSAGE_TYPE_TICKET = 4

        private const val RECEIVE_STATE_NO_START = 0
        private const val RECEIVE_STATE_START = 1
        private const val RECEIVE_STATE_RECEIVING = 2

        private const val MAX_FRAME_LEN = 4096

        private const val INDEX_FRAME_CODE = 1
        private const val INDEX_FRAME_NO = 2
        private const val INDEX_LEN_LOW = 3
        private const val INDEX_LEN_HIGH = 4

        fun getFileTypeText(fileType: Int): String {
            return when (fileType) {
                FILE_TYPE_AD_ANNOUNCEMENT -> "公告广告"
                FILE_TYPE_AD_DEFAULT -> "默认广告"
                FILE_TYPE_AD_PLAY_STATISTICS -> "广告统计"
                FILE_TYPE_CRASH -> "崩溃日志"
                FILE_TYPE_LOG -> "日志"
                FILE_TYPE_INSTANT_AD -> "即时广告"
                FILE_TYPE_NORMAL_AD -> "普通广告"
                FILE_TYPE_LOGO -> "Logo"
                FILE_TYPE_SPLASH -> "Splash"
                FILE_TYPE_APK -> "安装包APK"
                FILE_TYPE_APP_CONFIG -> "程序配置文件"
                else -> "未知类型"
            }
        }
    }
}
