package cn.bdqfork.context.configuration.reader;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class GenericResourceReaderTest {

    @Test
    public void readProperty() throws IOException {
        GenericResourceReader genericResourceReader = new GenericResourceReader();
        String value = genericResourceReader.readProperty("server.localhost", String.class);
        assertEquals(value, "127.0.0.1");
    }

    @Test
    public void testReadYaml() throws IOException {
        GenericResourceReader genericResourceReader = new GenericResourceReader("testReadYaml.yaml");
        long propertyLong = genericResourceReader.readProperty("test.int", long.class);
        assertEquals(propertyLong, 5);

        byte propertyByte = genericResourceReader.readProperty("test.byte", Byte.class);
        assertEquals(propertyByte, 1);

        float propertyFloat = genericResourceReader.readProperty("test.float", Float.class);
        assertEquals(propertyFloat, 1.234, 0.0001);

        double propertyDouble = genericResourceReader.readProperty("test.double", Double.class);
        assertEquals(propertyDouble, 2.234, 0.0001);

        boolean propertyBoolean = genericResourceReader.readProperty("test.boolean", Boolean.class);
        assertTrue(propertyBoolean);

        char propertyChar = genericResourceReader.readProperty("test.char", Character.class);
        assertEquals(propertyChar, 'a');

        String driver = genericResourceReader.readProperty("jdbc.driver", String.class);
        assertEquals(driver, "com.mysql.cj.jdbc.driver");

        int port = genericResourceReader.readProperty("jdbc.port", Integer.class);
        assertEquals(port, 3306);

        String serverLocalhost = genericResourceReader.readProperty("server.localhost", String.class);
        assertEquals(serverLocalhost, "127.0.0.1");

        int serverPort = genericResourceReader.readProperty("server.port", Integer.class);
        assertEquals(serverPort, 8080);
    }

    @Test
    public void testReadProperties() throws IOException {
        GenericResourceReader genericResourceReader = new GenericResourceReader("testReadProperties.properties");
        int propertyInteger = genericResourceReader.readProperty("server.int", Integer.class);
        assertEquals(propertyInteger, 5);

        long propertyLong = genericResourceReader.readProperty("server.long", long.class);
        assertEquals(propertyLong, 1);

        float propertyFloat = genericResourceReader.readProperty("server.float", Float.class);
        assertEquals(propertyFloat, 2.234, 0.0001);

        double propertyDouble = genericResourceReader.readProperty("server.double", Double.class);
        assertEquals(propertyDouble, 1.234, 0.0001);

        boolean propertyBoolean = genericResourceReader.readProperty("server.boolean", Boolean.class);
        assertTrue(propertyBoolean);

        char propertyChar = genericResourceReader.readProperty("server.char", Character.class);
        assertEquals(propertyChar, 'b');

        String testDriver = genericResourceReader.readProperty("jdbc.driver", String.class);
        assertEquals(testDriver, "com.mysql.cj.jdbc.driver");

        int testPort = genericResourceReader.readProperty("jdbc.port", Integer.class);
        assertEquals(testPort, 3306);

        String webLocalhost = genericResourceReader.readProperty("web.localhost", String.class);
        assertEquals(webLocalhost, "127.0.0.1");

        int webPort = genericResourceReader.readProperty("web.port", Integer.class);
        assertEquals(webPort, 8080);
    }
}