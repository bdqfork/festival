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
    public void testReadProperty() {
    }
}