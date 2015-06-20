package r2.dustjs.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import r2.common.R2Exception;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.StringWriter;

/**
 * JDK8 Nashorn 기반 dustjs 렌더링 엔진
 *
 * @author chanwook
 */
public class RenderingEngine {
    private final Logger logger = LoggerFactory.getLogger(RenderingEngine.class);

    private ScriptEngine scriptEngine;
    private String encoding = "UTF-8";

    public RenderingEngine(ScriptEngine engine) {
        this.scriptEngine = engine;
    }

    public String compile(String key, String html) {
        String compiled = "";
        try {
            final Object result = ((Invocable) scriptEngine).invokeFunction("dustCompile", key, html);
            if (logger.isDebugEnabled()) {
                logger.debug("템플릿 컴파일[key: " + key + "]\n컴파일전: " + html + "컴파일후:" + result);
            }
            compiled = (String) result;
        } catch (Throwable e) {
            throw new R2Exception("템플릿 컴파일 중 에러가 발생했습니다", e);
        }
        return compiled;
    }

    public void load(String template) {
        try {
            ((Invocable) scriptEngine).invokeFunction("dustLoad", template);
        } catch (Throwable e) {
            throw new R2Exception("템플릿 로딩 중 에러가 발생했습니다", e);
        }
    }

    public String render(String key, String json) {
        String view = "";
        try {
            StringWriter successWriter = new StringWriter();
            ((Invocable) scriptEngine).invokeFunction("dustRender", key, json, successWriter);

            view = new String(successWriter.getBuffer().toString().getBytes(encoding), encoding);
            if (logger.isDebugEnabled()) {
                logger.debug("최종 렌더링 완료>>" + key + ", JSON: " + json + "HTML: " + view);
            }
        } catch (Throwable e) {
            throw new R2Exception("렌더링 중 에러가 발생했습니다", e);
        }
        return view;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }
}
