package cn.bdqfork.model.configuration;

import cn.bdqfork.context.configuration.Configuration;
import cn.bdqfork.context.configuration.Value;

/**
 * @author fbw
 * @since 2020/2/13
 */
@Configuration(location = "testReadProperties.properties")
public class CustomLocationConfig {
    @Value("driver")
    private String driver;
    @Value("int")
    private long propertyLong;
    @Value("float")
    private double propertyDouble;
    @Value("boolean")
    private boolean propertyBoolean;
    @Value("int")
    private int propertyInteger;
    @Value("int")
    private byte propertyByte;
    @Value("char")
    private char propertyChar;
    @Value("char")
    private String propertyString;

    public String getPropertyString() {
        return propertyString;
    }

    public void setPropertyString(String propertyString) {
        this.propertyString = propertyString;
    }

    public char getPropertyChar() {
        return propertyChar;
    }

    public void setPropertyChar(char propertyChar) {
        this.propertyChar = propertyChar;
    }

    public byte getPropertyByte() {
        return propertyByte;
    }

    public void setPropertyByte(byte propertyByte) {
        this.propertyByte = propertyByte;
    }

    public long getPropertyLong() {
        return propertyLong;
    }

    public void setPropertyLong(long propertyLong) {
        this.propertyLong = propertyLong;
    }

    public double getPropertyDouble() {
        return propertyDouble;
    }

    public void setPropertyDouble(double propertyDouble) {
        this.propertyDouble = propertyDouble;
    }

    public boolean isPropertyBoolean() {
        return propertyBoolean;
    }

    public void setPropertyBoolean(boolean propertyBoolean) {
        this.propertyBoolean = propertyBoolean;
    }

    public int getPropertyInteger() {
        return propertyInteger;
    }

    public void setPropertyInteger(int propertyInteger) {
        this.propertyInteger = propertyInteger;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

}
