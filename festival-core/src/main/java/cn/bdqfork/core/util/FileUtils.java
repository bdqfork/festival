package cn.bdqfork.core.util;

import java.io.File;
import java.net.URL;

/**
 * FileUtil
 */
public class FileUtils {

    public static String getUniformAbsolutePath(File file) {
        return file.getAbsolutePath().replaceAll("\\\\", "\\/");
    }

    public static URL loadResourceByPath(String path) {
        return FileUtils.class.getClassLoader().getResource(path);
    }

    public static boolean isResourceExists(String path) {
        return FileUtils.class.getClassLoader().getResource(path) != null;
    }
}