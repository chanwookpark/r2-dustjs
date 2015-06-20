package r2.dustjs;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import r2.dustjs.core.RenderingEngine;
import r2.dustjs.core.RenderingEngineFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author chanwook
 */
public class RenderingEngineTests {

    public static final String COMPILED_HTML = "(function(dust){dust.register(\"key1\",body_0);function body_0(chk,ctx){return chk.w(\"<h1>Hello!</h1><span>\").f(ctx.get([\"name\"], false),ctx,\"h\").w(\"</span>\");}body_0.__dustBody=!0;return body_0}(dust));";
    public static final String RENDER_HTML = "<h1>Hello!</h1><span>chanwook</span>";

    @Test
    public void start() throws Exception {
        RenderingEngineFactory ref = new RenderingEngineFactory();
        RenderingEngine re = ref.getObject();

        assertThat(re, is(notNullValue()));

        final String key = "key1";
        String template = re.compile(key, "<h1>Hello!</h1><span>{name}</span>");
        assertThat(template, is(COMPILED_HTML));
        re.load(template);
        final String view = re.render(key, "{\"name\":\"chanwook\"}");
        assertThat(view, is(RENDER_HTML));
    }

    @Test
    public void template1() throws Exception {
        RenderingEngineFactory ref = new RenderingEngineFactory();
        RenderingEngine re = ref.getObject();

        String html = new String(Files.readAllBytes(Paths.get(new ClassPathResource("/templates/template1/template.html").getURI())));
        String json = new String(Files.readAllBytes(Paths.get(new ClassPathResource("/templates/template1/data.json").getURI())));

        final String key = "key1";
        final String template = re.compile(key, html);
        re.load(template);
        final String view = re.render(key, json);
    }
}
