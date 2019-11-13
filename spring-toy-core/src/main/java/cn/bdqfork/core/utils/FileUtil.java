package cn.bdqfork.core.utils;

import java.io.File;

/**
 * FileUtil
 */
public class FileUtil {

    public static String getUniformAbsolutePath(File file) {
        return file.getAbsolutePath().replaceAll("\\\\","\\/");
    }
}