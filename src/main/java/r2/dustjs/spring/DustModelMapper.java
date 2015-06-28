package r2.dustjs.spring;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author chanwook
 */
public interface DustModelMapper {
    void bind(DustModel dm, Map<String, Object> mergedOutputModel, HttpServletRequest request);
}
