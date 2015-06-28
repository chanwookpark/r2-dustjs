package r2.dustjs.spring;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
        final String template = l.getTemplate("/templates/template2/template.html");
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

    @Test
    public void createDustModel() throws Exception {
        DustjsView v = new DustjsView();
        v.addStaticAttribute(DustModel.MAPPER_KEY, new DustModelMapper() {
            public void bind(DustModel dm, Map<String, Object> mergedOutputModel, HttpServletRequest request) {
                // add session attribute
                Enumeration<String> names = request.getSession().getAttributeNames();
                while (names.hasMoreElements()) {
                    final String name = names.nextElement();
                    dm.put(name, request.getSession().getAttribute(name));
                }

                // add request attribute
                names = request.getAttributeNames();
                while (names.hasMoreElements()) {
                    final String name = names.nextElement();
                    dm.put(name, request.getAttribute(name));
                }
            }
        });
        final HashMap<String, Object> model = new HashMap<String, Object>();
        final MockHttpServletRequest req = new MockHttpServletRequest();

        final DustModel dm = new DustModel();
        dm.put("model1", "value");
        dm.put("model2", 1234);
        model.put(DustModel.MODEL_KEY, dm);

        req.addParameter("param1", "value");
        req.setAttribute("req-attr", "value");

        req.getSession().setAttribute("session-attr", "value");

        final DustModel resultModel = v.createDustModel(model, req);

        assertThat(resultModel, notNullValue());
        assertThat(resultModel.toMap().size(), is(5));

        assertThat((String) resultModel.get("model1"), is("value"));
        assertThat((Integer) resultModel.get("model2"), is(1234));
        assertThat(((Map<String, String[]>) resultModel.get("param")).get("param1")[0], is("value"));
        assertThat((String) resultModel.get("req-attr"), is("value"));
        assertThat((String) resultModel.get("session-attr"), is("value"));

    }
}
