package cn.bdqfork.web.convertor;

/**
 * @author bdq
 * @since 2020/2/2
 */
public class StringToIntegerConvertor implements Convertor<String, Integer> {
    @Override
    public Integer convert(String value) {
        return Integer.valueOf(value);
    }
}
