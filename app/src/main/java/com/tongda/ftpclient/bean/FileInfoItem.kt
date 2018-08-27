package com.tongda.adv3.net.ftp.bean

/**
* Created by chang on 2017-05-24.
* Mail:changfeng1050@hotmail.com
*/
data class FileInfoItem(val path: String, val size: Long, val md5: String, val parentDir: String = "")