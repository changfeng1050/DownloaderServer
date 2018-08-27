package com.tongda.ftpclient.bean

/**
* Created by chang on 2017-05-18.
* Mail:changfeng1050@hotmail.com
*/
data class FileInfo(val code:String, val type:Int, val files: List<FileBean>)

data class FileBean(val dir:String, val items:List<FileItem>)

data class FileItem(val name:String, val size:Long, val md5:String)