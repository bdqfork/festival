package cn.bdqfork.model.configuration;

import cn.bdqfork.context.configuration.Configuration;
import cn.bdqfork.context.configuration.Value;

@Configuration(location = "testReadProperties.yaml")
public class ReadPropertyConfig {
    @Value("int")
    private int propertyInteger;
    @Value("byte")
    private byte propertyByte;
    @Value("float")
    private float propertyFloat;
    @Value("float")
    private double propertyDouble;
    @Value("boolean")
    private boolean propertyBoolean;
    @Value("char")
    private char propertyChar;

    public int getPropertyInteger() {
        return propertyInteger;
    }

    public void setPropertyInteger(int propertyInteger) {
        this.propertyInteger = propertyInteger;
    }

    public byte getPropertyByte() {
        return propertyByte;
    }

    public void setPropertyByte(byte propertyByte) {
        this.propertyByte = propertyByte;
    }

    public float getPropertyFloat() {
        return propertyFloat;
    }

    public void setPropertyFloat(float propertyFloat) {
        this.propertyFloat = propertyFloat;
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

    public char getPropertyChar() {
        return propertyChar;
    }

    public void setPropertyChar(char propertyChar) {
        this.propertyChar = propertyChar;
    }
}
