package cn.bdqfork.web.convertor;

import org.junit.Test;

public class DefaultConvertorFactoryTest {

    @Test
    public void getConvertor() {
        DefaultConvertorFactory convertorFactory = new DefaultConvertorFactory();
        StringToIntegerConvertor stringToIntegerConvertor = new StringToIntegerConvertor();
        convertorFactory.registerConvertor(Integer.class, stringToIntegerConvertor);
        StringToIntegerConvertor convertor = (StringToIntegerConvertor) convertorFactory.getConvertor(Integer.class);
        int i = convertor.convert("1");
        assert i == 1;
    }

    @Test
    public void registerConvertor() {
        DefaultConvertorFactory convertorFactory = new DefaultConvertorFactory();
        StringToIntegerConvertor stringToIntegerConvertor = new StringToIntegerConvertor();
        convertorFactory.registerConvertor(Integer.class, stringToIntegerConvertor);
        assert convertorFactory.getConvertor(Integer.class) == stringToIntegerConvertor;
    }
}