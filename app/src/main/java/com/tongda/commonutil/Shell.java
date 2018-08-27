package com.tongda.commonutil;

import java.io.File;


/**
 * Created by changfeng on 2016/3/14.
 */
public class Shell {

    private static final String TAG = L.makeLogTag(Shell.class);

    public static int execCmdSilent1(String cmd) {
        int result = -1;

        try {
            Process p;
            p = Runtime.getRuntime().exec(cmd);

            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            L.e(TAG, "execCmdSilent1()" + e.getMessage());
        }
        return result;
    }

    public static int execRootCmd(String cmd) {
        if (new File("/system/bin/su").exists()) {

            execCmdSilent1("/system/bin/su -c " + cmd);
        } else if (new File("/system/xbin/su").exists()) {
            execCmdSilent1("/system/xbin/su -c " + cmd);
        } else {
            execCmdSilent1("su -c " + cmd);
        }
        return 0;
    }

    public static int enableReadWriteExecute(String file) {
        return execRootCmd("chmod 777 " + file);
    }

    public static int enableScreenshot() {
        return enableReadWriteExecute("/dev/graphics/fb0");
    }

    public static int reboot() {
        return execRootCmd("reboot");
    }

}
