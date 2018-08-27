package com.tongda.commonutil;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class FileUtils {
    public static final String TAG = "FileUtils";

    public static String getStringFromFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(new File(filename));
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "utf-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            return result.toString();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "getStringFromFile() " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "getStringFromFile() " + e.getMessage());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException expected) {
                Log.e(TAG, "getStringFromFile() " + expected.getMessage());
            }
        }
        return "";
    }

    public static String getText(InputStream inputStream, String charsetName) throws Exception {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charsetName));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            return result.toString();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException expected) {
                Log.e(TAG, "getText() " + expected.getMessage());
            }
        }
    }


    public static boolean exist(String path) {
        return new File(path).exists();
    }


    public static boolean fileIsJpeg(String fn) {
        String suff = fn.substring(fn.lastIndexOf('.') + 1).toLowerCase();
        if (" .jpeg .jpg".indexOf(suff) > 0)
            return true;
        else
            return false;
    }

    public static boolean fileIsImage(String fn) {
        String suffix = fn.substring(fn.lastIndexOf('.') + 1).toLowerCase();
        return " .jpeg .bmp .jpg .png .gif".indexOf(suffix) > 0;
    }

    public static String joinPath(String... args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(File.separator).append(arg.replaceAll("^/+", ""));
        }
        return stringBuilder.toString();
    }

    public static boolean isDirectory(String dir, String fileName) {
        return (new File(joinPath(dir, fileName)).isDirectory());
    }

    public static boolean createDir(String path) {
        File file = new File(path);
        Log.i(TAG, "createDir() path:" + path);
        if (!file.exists()) {
            return file.mkdirs();
        } else if (file.isFile()) {
            file.delete();
            return file.mkdirs();
        }
        return true;
    }


    public static void writeFile(String filename, String text) throws Exception {
        File f = new File(filename);
        if (!f.exists())
            f.createNewFile();
        else {
            f.delete();
            f.createNewFile();
        }

        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename), "utf-8"));

        // new BufferedWriter(new FileWriter(filename,
        // false),"utf-8");
        output.write(text);

        output.flush();
        output.close();
    }

    public static boolean copyFile(String srcFilename, String dstFilename) {
        try {

            String srcMd5 = null;
            String dstMd5 = null;
            try {
                srcMd5 = MD5Utils.getMd5String(srcFilename);
            } catch (Exception e) {
                Log.e(TAG, "copyFile() get md5 failed", e);
                return false;
            }

            try {
                dstMd5 = MD5Utils.getMd5String(dstFilename);
            } catch (Exception e) {
                copy(srcFilename, dstFilename);
                return true;
            }

            if (!srcMd5.equals(dstMd5)) {
                copy(srcFilename, dstFilename);
                return true;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "copyFile() src:" + srcFilename + " dst:" + dstFilename, e);
            return false;
        }
    }

    public static void copy(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                //int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }

                inStream.close();
                fs.close();
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("error 1 ");
            e.printStackTrace();
        }
    }


    public static void defileFile(File file) {
        if (file.isFile()) {
            Log.i(TAG, "deleteFile() " + file.getAbsolutePath());
            if (!file.delete()) {
                Log.e(TAG, "deleteFile() failed " + file.getAbsolutePath());
            }
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                Log.i(TAG, "deleteFile() " + file.getAbsolutePath());
                if (!file.delete()) {
                    Log.e(TAG, "deleteFile() failed " + file.getAbsolutePath());
                }
                return;
            }

            for (File f : childFiles) {
                Log.i(TAG, "deleteFile() " + file.getAbsolutePath());
                defileFile(f);
            }
            file.delete();
        }

    }

    public static long getFolderSize(File file) {
        long size = 0;
        File[] fileList = file.listFiles();
        for (File f : fileList) {
            if (f.isDirectory()) {
                size += getFolderSize(f);
            } else {
                size += f.length();
            }
        }
        return size;
    }

    public static boolean isMd5Equal(String path1, String path2) {
        try {
            String md1 = MD5Utils.getMd5String(path1);
            String md2 = MD5Utils.getMd5String(path2);
            return md1.equals(md2);
        } catch (Exception e) {
            return false;
        }
    }

    public static String byteToMB(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size > kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }
}
