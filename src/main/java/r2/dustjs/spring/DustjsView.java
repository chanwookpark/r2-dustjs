package r2.dustjs.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.InternalResourceView;
import r2.common.R2Exception;
import r2.dustjs.core.RenderingEngine;
import r2.dustjs.core.RenderingEngineFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
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
//TODO AbstractView로 해야할까..
public class DustjsView extends InternalResourceView {

    private String partialTemplatePath = "/templates/partial/";

    private String viewHtmlKey = "_view_html";
    private String jsonDataKey = "_view_data";
    private String templateHtmlKey = "_view_template";

    //TODO 개선요
    private RenderingEngine renderingEngine = new RenderingEngineFactory().getObject();

    private TemplateFileLoader templateLoader = new MultipathTemplateFileLoader();

    private ObjectMapper objectMapper = new ObjectMapper();
    private boolean usePartial = true;

    protected Map<String, Object> createMergedOutputModel(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        final Map<String, Object> mergedOutputModel = super.createMergedOutputModel(model, request, response);

        DustModel dm = createDustModel(mergedOutputModel, request);

        final String templateKey = getUrl();
        final String template = templateLoader.getTemplate(templateKey);
        if (StringUtils.hasText(template)) {
            //TODO 한 번 로딩하면 계속 사용하도록 개선
            createPartial();

            createRenderingHtml(mergedOutputModel, dm, templateKey, template);
        }

        // DM에 담았던 객체 정보를 그대로 mergedModel에 저장해 View에서의 접근도 가능하게 지원한다
        mergedOutputModel.putAll(dm.toMap());
        return mergedOutputModel;
    }

    protected DustModel createDustModel(Map<String, Object> mergedOutputModel, HttpServletRequest request) {
        DustModel dm = (DustModel) mergedOutputModel.get(MODEL_KEY);
        if (dm == null) {
            // 단순한 화면 네비게이션도 가능하도록 예외를 던지는 로직에서 기본 생성 로직으로 변경
            dm = new DustModel();
        }

        addParameter(request, dm);
        // TODO 객체 타입으로 JSON 변환시 에러가 발생할 여자가 많아 우선 주석처리 함
        //        addAttribute(request, dm);
        //        addSession(request, dm);

        Object o = getStaticAttributes().get(DustModel.MAPPER_KEY);
        if (o != null) {
            DustModelMapper mapper = (DustModelMapper) o;
            mapper.bind(dm, mergedOutputModel, request);
        }
        return dm;
    }

    private void addParameter(HttpServletRequest request, DustModel dm) {
        dm.put("param", request.getParameterMap());
    }


    protected void createRenderingHtml(Map<String, Object> mergedOutputModel, DustModel dm, String templateKey, String template) {
        final String compiled = renderingEngine.compile(templateKey, template);
        renderingEngine.load(compiled);
        final String json = toJson(dm.toMap());
        final String view = renderingEngine.render(templateKey, json);

        mergedOutputModel.put(templateHtmlKey, compiled);
        mergedOutputModel.put(jsonDataKey, json);
        mergedOutputModel.put(viewHtmlKey, view);
    }

    protected void createPartial() {
        if (usePartial) {
            final File file;
            try {
                //TODO Resource location으로 통합하기..
                file = new ClassPathResource(partialTemplatePath).getFile();
            } catch (IOException e) {
                logger.warn("Partial 폴더가 생성되어 있지 않아 Partial 로딩은 취소되었습니다!");
                return;
            }

            try {
                if (file == null) {
                    return;
                }
                final File[] files = file.listFiles();
                for (File f : files) {
                    loadPartial(f);
                }
            } catch (Exception e) {
                throw new R2Exception("Partial 로딩 중 에러가 발생했습니다.", e);
            }
        }
    }

    private void loadPartial(File f) {
        final String partialTemplateKey = f.getName();
        final String partialTemplate = templateLoader.getTemplate(partialTemplatePath + partialTemplateKey);

        //FIXME .html로 끝나지 않을 수도 있으니 정리해야해
        final String compiled = renderingEngine.compile(partialTemplateKey.replaceAll(".html", ""), partialTemplate);
        //partial은 로딩까지만 함
        renderingEngine.load(compiled);

        if (logger.isInfoEnabled()) {
            logger.info(">> Partial 로딩 완료 [" + partialTemplateKey + "] " + partialTemplate);
        }
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Dust HTML rendering
        if (model.containsKey(viewHtmlKey)) {
            //TODO jacksonview 참조해 createTemporaryOutputStream() 재정의 해야하는 케이스 구현 (for IE)
            OutputStream stream = response.getOutputStream();
            StreamUtils.copy((String) model.get(viewHtmlKey), getCharset(), stream);
        } else {
            // page navigation
            super.renderMergedOutputModel(model, request, response);
        }
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

    public void setTemplateLoader(TemplateFileLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public void setPartialTemplatePath(String partialTemplatePath) {
        this.partialTemplatePath = partialTemplatePath;
    }

    public void setUsePartial(boolean usePartial) {
        this.usePartial = usePartial;
    }

    public RenderingEngine getRenderingEngine() {
        return renderingEngine;
    }
}
