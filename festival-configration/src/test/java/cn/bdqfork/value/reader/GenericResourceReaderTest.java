package cn.bdqfork.value.reader;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

/**
 * @author fbw
 * @since 2020/1/9
 */
public class GenericResourceReaderTest {
    @Test
    public void testLoadYaml() throws Throwable {
        ResourceReader resourceReader = new GenericResourceReader("test.yaml");
        String host = resourceReader.readProperty("server.localhost");
        assert host.equals("127.0.0.1");
        Integer port = resourceReader.readProperty("server.port");
        assert port == 80;
    }

    @Test
    public void testLoadProperties() throws Throwable {
        ResourceReader resourceReader = new GenericResourceReader("jdbcConfig.properties");
        assertEquals(resourceReader.readProperty("driver"), "com.mysql.cj.jdbc.driver");
        assertEquals(resourceReader.readProperty("username"), "Trey");
        assertEquals(resourceReader.readProperty("password"), "1234");
    }
}
