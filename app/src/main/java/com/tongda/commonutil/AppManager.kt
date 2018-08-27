package com.tongda.commonutil

import android.app.Activity
import android.content.Context
import java.util.*

/**
 * Created by changfeng on 2016/3/28.
 */
object AppManager {

    private val TAG: String = L.makeLogTag(AppManager::class.java)

    var activityStack: Stack<Activity> = Stack()

    @JvmStatic
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    @JvmStatic
    fun currentActivity(): Activity {
        return activityStack.lastElement()
    }

    /**
     * 结束指定的Activity
     */
    @JvmStatic
    fun removeActivity(activity: Activity?) {
        if (activity != null && activityStack.contains(activity)) {
            activityStack.remove(activity)
        }
    }


    @JvmStatic
    fun finishActivity() {
        finishActivity(activityStack.lastElement())
    }

    @JvmStatic
    fun finishActivity(activity: Activity?) {
        if (activity != null && !activity.isFinishing) {
            L.i(TAG, "finishActivity() ${activity.localClassName}")
            activityStack.remove(activity)
            activity.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     */
    @JvmStatic
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
                break
            }
        }
    }

    /**
     * 结束所有Activity
     */
    @JvmStatic
    fun finishAllActivity() {
        var i = 0
        val size = activityStack.size
        while (i < size) {
            if (null != activityStack[i]) {
                //finishActivity方法中的activity.isFinishing()方法会导致某些activity无法销毁
                //貌似跳转的时候最后一个activity 是finishing状态，所以没有执行
                //内部实现不是很清楚，但是实测结果如此，使用下面代码则没有问题
                // find by TopJohn
                //finishActivity(activityStack.get(i));

                activityStack[i].finish()
                //break;
            }
            i++
        }
        activityStack.clear()
    }

    /**
     * 获取指定的Activity

     * @author kymjs
     */
    @JvmStatic
    fun getActivity(cls: Class<*>): Activity? {
        activityStack
                .filter { it.javaClass == cls }
                .forEach { return it }
        return null
    }

    /**
     * 退出应用程序
     */
    @JvmStatic
    fun AppExit(context: Context) {
        try {
            finishAllActivity()
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        } catch (e: Exception) {
        }

    }
}