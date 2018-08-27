package com.tongda.commonutil

/**
 * Created by Zhou Jinlong on 2018/5/23.
 */
class LunarYearDateUtil {

    companion object {

        private val tgdz = arrayOf(
                arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")//10天干
                , arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"))//12地支


        //12生肖，（注：12生肖对应12地支，即子鼠，丑牛,寅虎依此类推）

        private val animalYear = arrayOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")


        private val startYear = 1804//定义起始年，1804年为甲子年属鼠


        /**获取当前年份与起始年之间的差值 */

        fun subtractYear(year: Int): Int {

            var jiaziYear = startYear

            if (year < jiaziYear) {//如果年份小于起始的甲子年(startYear = 1804),则起始甲子年往前偏移

                jiaziYear -= (60 + 60 * ((jiaziYear - year) / 60))//60年一个周期

            }

            return year - jiaziYear

        }

        /**获取该年的天干名称 */

        fun getTianGanName(year: Int): String {

            return tgdz[0][subtractYear(year) % 10]

        }

        /**获取该年的地支名称 */

        fun getDiZhiName(year: Int): String {

            return tgdz[1][subtractYear(year) % 12]

        }

        /**
         *
         * 获取该年的天干、地支名称
         *
         * @param year 年份
         *
         * @return
         */

        fun getTGDZName(year: Int): String {

            return getTianGanName(year) + getDiZhiName(year)

        }

        /**
         *
         * 获取该年的生肖名称
         *
         * @param year 年份
         *
         * @return
         */

        fun getAnimalYearName(year: Int): String {

            return animalYear[subtractYear(year) % 12]

        }
    }
}