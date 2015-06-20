package r2.dustjs.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.view.InternalResourceView;
import r2.common.R2Exception;
import r2.dustjs.core.RenderingEngine;
import r2.dustjs.core.RenderingEngineFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static r2.dustjs.spring.DustModel.MODEL_KEY;
import static r2.dustjs.spring.DustModel.PREFIX;

/**
 * <pre>
 * {@link org.springframework.web.servlet.view.JstlView}를 확장해 Dust.js로 렌더링하는 HTML을
 * {@link org.springframework.web.servlet.ModelAndView}로 저장해주는 역할 수행
 *
 * </pre>
 *
 * @author chanwook
 */
public class DustjsView extends InternalResourceView { //FIXME AbstractView로 해야할까..

    private String viewHtmlKey = "_view_html";
    private String jsonDataKey = "_view_data";
    private String templateHtmlKey = "_view_template";

    //TODO 개선요
    private RenderingEngine renderingEngine = new RenderingEngineFactory().getObject();

    private ObjectMapper objectMapper = new ObjectMapper();
    private boolean usePartial = true;

    protected Map<String, Object> createMergedOutputModel(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        final Map<String, Object> mergedOutputModel = super.createMergedOutputModel(model, request, response);

        final DustModel dm = (DustModel) mergedOutputModel.get(MODEL_KEY);

        if (dm == null) {
            throw new R2Exception("렌더링을 위한 데이터가 지정되지 않았습니다. 코드를 확인해주세요!!");
        }

        //TODO 한 번 로딩하면 계속 사용하도록 개선
        createPartial();

        final String templateKey = getUrl();
        final String template = getTemplate(templateKey);
        final String compiled = renderingEngine.compile(templateKey, template);
        renderingEngine.load(compiled);
        final String json = toJson(dm.toMap());
        final String view = renderingEngine.render(templateKey, json);

        mergedOutputModel.put(templateHtmlKey, compiled);
        mergedOutputModel.put(jsonDataKey, json);
        mergedOutputModel.put(viewHtmlKey, view);
        // DM에 담았던 객체 정보를 그대로 mergedModel에 저장해 View에서의 접근도 가능하게 지원한다
        mergedOutputModel.putAll(dm.toMap());

        return mergedOutputModel;
    }

    protected void createPartial() {
        if (usePartial) {
            try {
                final File[] files = new ClassPathResource("/templates/partial").getFile().listFiles();
                for (File f : files) {
                    loadPartial(f);
                }
            } catch (IOException e) {
                throw new R2Exception("Partial 로딩 중 에러가 발생했습니다.", e);
            }
        }
    }

    private void loadPartial(File f) {
        final String partialTemplateKey = f.getName().replaceAll(".html", "");
        final String partialTemplate = getTemplate(f.toURI());
        final String compiled = renderingEngine.compile(partialTemplateKey, partialTemplate);
        //partial은 로딩까지만 함
        renderingEngine.load(compiled);

        if (logger.isInfoEnabled()) {
            logger.info(">> Partial 로딩 완료 [" + partialTemplateKey + "] " + partialTemplate);
        }
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //TODO jacksonview 참조해 createTemporaryOutputStream() 재정의 해야하는 케이스 구현 (for IE)
        OutputStream stream = response.getOutputStream();
        StreamUtils.copy((String) model.get(viewHtmlKey), getCharset(), stream);
    }

    protected Charset getCharset() {
        //TODO 속성으로..
        return Charset.forName("UTF-8");
    }

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        setResponseContentType(request, response);

        //TODO 구현 jacksonview 참조
        super.prepareResponse(request, response);

    }

    private String toJson(Map<String, ?> originalModel) {
        try {
            Map<String, Object> renderModel = new HashMap<String, Object>();
            for (Map.Entry<String, ?> e : originalModel.entrySet()) {
                if (e.getKey().startsWith(PREFIX)) {
                    renderModel.put(e.getKey().replaceAll(PREFIX, ""), e.getValue());
                }
            }
            return objectMapper.writeValueAsString(renderModel);
        } catch (JsonProcessingException e) {
            throw new R2Exception("데이터 모델 변환 중 에러가 발생했습니다.", e);
        }
    }

    protected String getTemplate(URI uri) {
        //TODO 개선요..
        try {
            String template = new String(Files.readAllBytes(Paths.get(uri)));
            return template;
        } catch (IOException e) {
            throw new R2Exception("템플릿 파일 로딩 중 에러가 발생했습니다.", e);
        }
    }

    private String getTemplate(String templateKey) {
        try {
            return getTemplate(new ClassPathResource(templateKey).getURI());
        } catch (IOException e) {
            throw new R2Exception("템플릿 파일 로딩 중 에러가 발생했습니다.", e);
        }
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {
        this.renderingEngine = renderingEngine;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setJsonDataKey(String jsonDataKey) {
        this.jsonDataKey = jsonDataKey;
    }

    public void setViewHtmlKey(String viewHtmlKey) {
        this.viewHtmlKey = viewHtmlKey;
    }

    public void setTemplateHtmlKey(String templateHtmlKey) {
        this.templateHtmlKey = templateHtmlKey;
    }
}
