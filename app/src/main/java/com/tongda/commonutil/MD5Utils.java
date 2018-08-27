package com.tongda.commonutil;

import android.util.Log;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5Utils {

    private static final String TAG = "MD5Utils";
    public static final int LEN = 32;

    public static String getMd5String(String filepath) throws Exception {
        if (filepath == null || filepath.isEmpty()) {
            return "";
        }
        MessageDigest md = MessageDigest.getInstance("MD5");

        FileInputStream fis = new FileInputStream(filepath);

        byte[] dateBytes = new byte[1024];

        int numRead = 0;

        while ((numRead = fis.read(dateBytes)) != -1) {
            md.update(dateBytes, 0, numRead);
        }
        fis.close();
        byte[] mdBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte mdByte : mdBytes) {
            sb.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String getTextMd5(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = text.getBytes();
        md.update(bytes);
        byte[] mdBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte mdByte : mdBytes) {
            sb.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static boolean isMd5Equal(final String firstFilePath, final String secondFilePath) {
        try {
            return getMd5String(firstFilePath).equals(getMd5String(secondFilePath));
        } catch (Exception e) {
            Log.e(TAG, "isMd5Equal() ", e);
            return false;
        }
    }

    public static boolean equalToMd5(final String filePath, final String md5) {
        try {
            return getMd5String(filePath).equals(md5);
        } catch (Exception e) {
            Log.e(TAG, "equalToMd5()", e);
            return false;
        }
    }

}
