package cn.bdqfork.value.reader;

import java.io.IOException;

/**
 * @author bdq
 * @since 2020/1/9
 */
public abstract class AbstractResourceReader implements ResourceReader {

    private String resourcePath;

    public AbstractResourceReader(String resourcePath) throws IOException {
        this.resourcePath = resourcePath;
        load();
    }

    protected abstract void load() throws IOException;

    public String getResourcePath() {
        return resourcePath;
    }
}
