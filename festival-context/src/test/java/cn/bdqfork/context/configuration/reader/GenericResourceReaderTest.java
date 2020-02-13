package cn.bdqfork.context.configuration.reader;

import cn.bdqfork.context.AnnotationApplicationContext;
import cn.bdqfork.model.configuration.CustomLocationConfig;
import cn.bdqfork.model.configuration.ReadPropertyConfig;
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
    public void testReadProperty() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.configuration");
        annotationApplicationContext.start();
        ReadPropertyConfig config = annotationApplicationContext.getBean(ReadPropertyConfig.class);
        assertEquals(config.getPropertyByte(), 1);
        assertEquals(config.getPropertyChar(), 'a');
        assertEquals(config.getPropertyFloat(), 1.234, 0.0001);
        assertEquals(config.getPropertyDouble(), 1.234, 0.0001);
        assertEquals(config.getPropertyInteger(), 5);
        assertTrue(config.isPropertyBoolean());
    }

    @Test
    public void testReadCustomLocationProperty() throws Exception {
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("cn.bdqfork.model.configuration");
        annotationApplicationContext.start();
        CustomLocationConfig customLocationConfig = annotationApplicationContext.getBean(CustomLocationConfig.class);
        assertEquals(customLocationConfig.getDriver(), "com.mysql.cj.jdbc.driver");
        assertEquals(customLocationConfig.getPort(), 3306);
    }
}