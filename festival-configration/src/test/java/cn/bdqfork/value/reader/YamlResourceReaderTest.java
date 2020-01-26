package cn.bdqfork.value.reader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class YamlResourceReaderTest {

    @Test
    public void readProperty() throws Throwable {
        ResourceReader resourceReader = new YamlResourceReader("test.yaml");
        assertEquals(resourceReader.readProperty("server.localhost"), "127.0.0.1");
        Integer port = resourceReader.readProperty("server.port");
        assert port == 80;
    }
}