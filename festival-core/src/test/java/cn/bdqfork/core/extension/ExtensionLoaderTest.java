package cn.bdqfork.core.extension;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExtensionLoaderTest {

    @Test
    public void getExtension() {
        ExtensionLoader<IExtensionTest> extensionLoader = ExtensionLoader.getExtensionLoader(IExtensionTest.class);
        IExtensionTest iExtensionTest = extensionLoader.getExtension("imp1");
        assert iExtensionTest != null;
    }

    @Test
    public void getExtensions() {
        ExtensionLoader<IExtensionTest> extensionLoader = ExtensionLoader.getExtensionLoader(IExtensionTest.class);
        assertEquals(extensionLoader.getExtensions().size(), 2);
    }
}