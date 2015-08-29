package r2.dustjs.spring;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Dustjs를 사용해 렌더링 시 사용하는 객체만 분리해 내기 위한 기능 추가 제공
 *
 * @author chanwook
 */
public class DustModel {

    public static final String PREFIX = "_d.";
    public static final String MODEL_KEY = "_dustModel";
    public static final String MAPPER_KEY = "_dm_initializer";

    private Map<String, Object> modelMap = new HashMap<String, Object>();

    private ModelMapper m = new ModelMapper();

    public Object put(String key, Object value) {
        return modelMap.put(PREFIX + key, value);
    }

    public Object get(Object key) {
        final Object value = modelMap.get(key);
        if (value == null) {
            return modelMap.get(PREFIX + key);
        }
        return value;
    }

    public <T> T bind(String key, Object source, Class<T> target) {
        final T result = m.map(source, target);
        put(key, result);
        return result;
    }

    public <T> T bind(String key, Object source, TypeToken<T> typeToken) {
        final T result = m.map(source, typeToken.getType());
        put(key, result);
        return result;
    }

    public <T> T bind(String key, Object source, T destination) {
        m.map(source, destination);
        put(key, destination);
        return destination;
    }


    /*
    public <T> List<T> bindList(String key, Object source, Class<T> target) {
        Type type = new TypeToken<List<String>>() {}.getType();
        final List<T> result = m.map(source, type);
        put(key, result);
        return result;
    }
    */

    public Map<String, ?> toMap() {
        return modelMap;
    }

    public static class ListType<T> extends TypeToken<T> {

    }
}
