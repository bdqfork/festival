package cn.bdqfork.core.util;

import java.io.File;

/**
 * FileUtil
 */
public class FileUtils {

    public static String getUniformAbsolutePath(File file) {
        return file.getAbsolutePath().replaceAll("\\\\","\\/");
    }
}