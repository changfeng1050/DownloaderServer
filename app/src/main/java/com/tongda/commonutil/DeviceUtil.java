package com.tongda.commonutil;


/**
 * Created by changfeng on 2016/3/1.
 * Mail:changfeng1050@hotmail.com
 */
public class DeviceUtil {
    private static final String TAG = L.makeLogTag(DeviceUtil.class);

    private static final String JNI_LIB_NAME = "deviceutil";

    static {
        try {
            System.loadLibrary(JNI_LIB_NAME);
        } catch (Exception e) {
            L.e(TAG, "loadLibrary");
        }
    }

    public static native String getMac(String eth0);

    public static native String getIp(String eth0);

    public static native String getRoute(String eth0);

    public static native String getNetmask(String eth0);

    public static native String getUname();

    public static native int openDevice(String path);

    public static native int readDevice(String fid);

    public static native int closeDevice(String fid);

    public static native String getTestString(String testString);

    public native static String read();

    // 最长 1024个字节
    public native static byte[] readBytes(int fd);

    public native static int openSerial(String path, int baudRate, int dataBits, int stopBits, char parity, int flags);

    public native static int write(int fd, byte[] buffer, int len);

    public native static byte[] readTemperature(int fd);
}
