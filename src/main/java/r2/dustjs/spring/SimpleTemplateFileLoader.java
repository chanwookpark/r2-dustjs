package r2.dustjs.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import r2.common.R2Exception;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author chanwook
 */
public class SimpleTemplateFileLoader implements TemplateFileLoader {

    private final Logger logger = LoggerFactory.getLogger(SimpleTemplateFileLoader.class);

    public String getTemplate(String templatePath) {
        if (logger.isDebugEnabled()) {
            logger.debug("Template file resolve try: " + templatePath);
        }

        try {
            final File file = new ClassPathResource(templatePath).getFile();
            String template = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            return template;
        } catch (IOException e) {
            throw new R2Exception(templatePath + "에 해당하는 Dust 템플릿 파일이 존재하지 않습니다.", e);
        }
    }
}
