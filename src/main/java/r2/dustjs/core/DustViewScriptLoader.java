package r2.dustjs.core;

import r2.common.R2Exception;

import javax.script.ScriptEngine;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * https://github.com/linkedin/dustjs 지원 버전
 *
 * @author chanwook
 */
public class DustViewScriptLoader implements TemplateScriptLoader {
    public void load(ScriptEngine engine) {
        try {
            engine.eval(getFileReader("/r2/dustjs/dust-full-2.7.2.js"));
            engine.eval(getFileReader("/r2/dustjs/r2-dustjs.js"));
        } catch (Throwable e) {
            throw new R2Exception("스크립트 파일 로딩 중 에러가 발생했습니다", e);
        }
    }

    private InputStreamReader getFileReader(String filePath) throws UnsupportedEncodingException {
        InputStream fileStream = getClass().getResourceAsStream(filePath);
        return new InputStreamReader(fileStream, "UTF-8");
    }
}
