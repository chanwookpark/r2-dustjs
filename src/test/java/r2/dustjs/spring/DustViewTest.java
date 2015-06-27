package r2.dustjs.spring;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author chanwook
 */
public class DustViewTest {
    @Test
    public void resolveSrcTemplate() throws Exception {
        TemplateFileLoader l = new MultipathTemplateFileLoader();
//        /Users/chanwook/src/r2-dustjs/src/test/resources/templates/template2/template.html
        final String template = l.getTemplate("templates/template2/template.html");
        assertThat(template, notNullValue());
        assertThat(template, is("<html></html>"));
    }

    @Test
    public void loadPartial() throws Exception {
        DustjsView v = new DustjsView();
        v.setPartialTemplatePath("/templates/partial/config/");
        v.createPartial();

        assertThat(v.getRenderingEngine().render("partial1", "{}"), is("<h1>partial1</h1>"));
        assertThat(v.getRenderingEngine().render("partial2", "{}"), is("<h1>partial2</h1>"));

    }
}
