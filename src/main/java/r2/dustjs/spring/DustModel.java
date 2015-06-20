package r2.dustjs.spring;

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

    public Map<String, Object> modelMap = new HashMap<String, Object>();

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

    public Map<String, ?> toMap() {
        return modelMap;
    }
}
