package com.tongda.commonutil

/**
 * Created by chang on 2017-11-11.
 */
class WeekUtil {
    companion object {
        fun replaceWeekLabel(dayText: String): String {
            return dayText.replace("周日", "星期日")
                    .replace("周一", "星期一")
                    .replace("周二", "星期二")
                    .replace("周三", "星期三")
                    .replace("周四", "星期四")
                    .replace("周五", "星期五")
                    .replace("周六", "星期六")
        }
    }
}