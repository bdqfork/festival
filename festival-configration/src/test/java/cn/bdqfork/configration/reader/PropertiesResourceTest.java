package cn.bdqfork.configration.reader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author fbw
 * @since 2020/1/9
 */
public class PropertiesResourceTest {

    @Test
    public void testLoadProperties() throws Throwable {
        ResourceReader resourceReader = new PropertiesResourceReader("jdbcConfig.properties");
        assertEquals(resourceReader.readProperty("driver"), "com.mysql.cj.jdbc.driver");
        assertEquals(resourceReader.readProperty("username"), "Trey");
        assertEquals(resourceReader.readProperty("password"), "1234");
    }
}
