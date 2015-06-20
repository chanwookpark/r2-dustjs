package r2.dustjs.core;

import javax.script.ScriptEngine;

/**
 * JavaScipt 파일 로딩 역할을 수행하기 위한 지점을 인터페이스로 정의함
 *
 * @author chanwook
 */
public interface TemplateScriptLoader {
    /**
     * 스크립트 파일 로딩
     *
     * @param engine
     */
    void load(ScriptEngine engine);

}
