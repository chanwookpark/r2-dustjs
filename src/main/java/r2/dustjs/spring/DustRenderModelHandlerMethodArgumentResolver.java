package r2.dustjs.spring;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static r2.dustjs.spring.DustModel.MODEL_KEY;

/**
 * 핸들러 메서드 인자로 {@link DustModel}을 받을 수 있도록 해준다.
 *
 * @author chanwook
 */
public class DustRenderModelHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(DustModel.class);
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final DustModel dm = new DustModel();
        mavContainer.addAttribute(MODEL_KEY, dm);
        return dm;
    }
}
