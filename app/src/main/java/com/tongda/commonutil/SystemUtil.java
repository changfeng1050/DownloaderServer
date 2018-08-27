package com.tongda.commonutil;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by changfeng on 2016/3/14.
 */
public class SystemUtil {
    private static final String TAG = L.makeLogTag(SystemUtil.class);

    /**
     * 硬件类型 瑞芯微
     */
    private static final String HARDWARE_TYPE_ROCKCHIP = "rockchip";
    /**
     * 硬件类型 A20
     */
    private static final String HARDWARE_TYPE_A20 = "a20";

    private static String sHardwareType = null;

    public static boolean isRockChipBoard() {
        if (sHardwareType == null) {
            sHardwareType = getsHardwareType();
        }
        return sHardwareType.contains(HARDWARE_TYPE_ROCKCHIP);
    }

    private static String sHardwareSerial = "";

    public static String getExternalSdPath(String buildSdPath) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            String mount = "";
            BufferedReader br = new BufferedReader(isr);
            int finds = 0;
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) {
                    continue;
                }
                if (line.contains("asec")) {
                    continue;
                }
                if (line.contains("/mnt/private")) {
                    continue;
                }
                if (line.contains("usbhost")) {
                    continue;
                }

                if (line.contains("fat")) {
                    String columns[] = line.split("\\s+");
                    if (columns.length > 1) {
                        finds++;
                        if (!columns[1].equals(buildSdPath)) {
                            mount = columns[1];
                        }
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split("\\s+");
                    if (columns.length > 1) {
                        mount = columns[1];
                    }
                }
            }
            if (finds == 1) {
                return "";
            }
            is.close();
            isr.close();
            br.close();
            return mount;
        } catch (FileNotFoundException e) {
            L.e(TAG, "getExternalSdPath()", e);
            return "";
        } catch (IOException e) {
            L.e(TAG, "getExternalSdPath()", e);
            return "";
        } catch (Exception e) {
            L.e(TAG, "getExternalSdPath()", e);
            return "";
        }
    }

    /**
     * 获取唯一识别码
     * <p>
     * 优先获取以太网卡的mac地址，因为无线网卡的物理地址，必须要在打开WIFI或者开启了热点，才能获取
     */
    public static String getHardWareSerial(Context context) {
        if (sHardwareSerial.isEmpty()) {
            // 如果是瑞芯微的CPU（通达的主板），以太网卡的物理地址是随机的，所以使用CPU的序列号作为唯一识别码
            if (isRockChipBoard()) {
                String serial = getCPUSerial();

                int len = serial.length();
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < len; k++) {
                    sb.append(serial.charAt(k));
                    if (k % 2 == 1 && k != len - 1) {
                        sb.append('-');
                    }
                }
                sHardwareSerial = sb.toString().toUpperCase();
            } else {
                String mac = DeviceUtil.getMac("eth0").replace(':', '-');
                if (mac.isEmpty()) {
                    mac = DeviceUtil.getMac("wlan0").replace(':', '-');
                    L.i(TAG, "getHardWareSerial() eth0:" + mac);
                    if (mac.length() <= 0) {
                        String aid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        mac = aid.substring(0, 2) + "-" + aid.substring(2, 4) + "-" + aid.substring(4, 6) + "-"
                                + aid.substring(6, 8) + "-" + aid.substring(8, 10) + "-" + aid.substring(10, 12);
                        L.i(TAG, "getHardWareSerial() android id:" + mac);
                    }
                }
                sHardwareSerial = mac.toUpperCase().replace(':', '-');

            }
        }
        return sHardwareSerial;
    }

    public static String getIp(Context context) {
        String ip = DeviceUtil.getIp("wlan0");
        if (ip.isEmpty() || ip.contains("0.0.0.0")) {
            ip = DeviceUtil.getIp("eth0");
        }
        return ip;
    }

    public static String getCurrentTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static long getTotalMemory(String rootPath) {
        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdir();
        }
        StatFs sf = new StatFs(root.getPath());

        long blockSize = sf.getBlockSize();
        return blockSize * sf.getBlockCount() / 1048576;
    }

    public static long getFreeMemory(String rootPath) {
        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdir();
        }
        StatFs sf = new StatFs(root.getPath());

        long blockSize = sf.getBlockSize();
        return blockSize * sf.getFreeBlocks() / 1048576;
    }

    public static String getsHardwareType() {
        return getCpuHardwareType();
    }

    /**
     * 获取CPU序列号
     *
     * @return CPU序列号(16位) 读取失败为"0000000000000000"
     */
    public static String getCPUSerial() {
        String str, strCPU, cpuAddress = "0000000000000000";
        try {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    // 查找到序列号所在行
                    if (str.contains("Serial")) {
                        // 提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        // 去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    // 文件结尾
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return cpuAddress;
    }

    public static String getCpuHardwareType() {
        String str, hardware = "";
        try {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    // 查找到序列号所在行
                    if (str.contains("Hardware")) {
                        // 提取序列号
                        String strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        // 去空格
                        hardware = strCPU.trim();
                        break;
                    }
                } else {
                    // 文件结尾
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return hardware;
    }

    public static long getElapsedTimes() {
        long ut = SystemClock.elapsedRealtime();
        if (ut == 0) {
            ut = 1;
        }

        return Math.abs(ut);
    }

    public static void setBrightness(Context context, int brightness) {
        L.i(TAG, "setBrightness() " + brightness);
        int real = (int) (brightness * 255.0 / 100);
        Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, real);
        context.getContentResolver().notifyChange(uri, null);
    }

    public static boolean screencap(String path) {
        Process su;
        try {
            su = Runtime.getRuntime().exec("/system/xbin/su");
            String cmd = "screencap " + path + " \n" + "exit\n";
            su.getOutputStream().write(cmd.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void hideNavBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


}
