package r2.dustjs.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chanwook
 */
public class MultiPathResolveTemplateFileLoader implements TemplateFileLoader {

    private final Logger logger = LoggerFactory.getLogger(MultiPathResolveTemplateFileLoader.class);

    private static final String[] RESOURCE_LOCATION = new String[]{
            "src/main/resources", "src/test/resources", "target/classes"
    };

    public String getTemplate(String templateKey) {
        List<String> paths = new ArrayList<String>(RESOURCE_LOCATION.length);
        for (String location : RESOURCE_LOCATION) {
            paths.add(location + templateKey);
        }
        return resolveTemplate(paths.toArray(new String[paths.size()]));
    }

    private String resolveTemplate(String[] paths) {
        for (String path : paths) {
            if (logger.isDebugEnabled()) {
                logger.debug("Template file resolve try: " + path);
            }

            final File file = new FileSystemResource(path).getFile();
            if (file != null) {
                try {
                    String template = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                    return template;
                } catch (IOException e) {
                    // template key에 해당하는 파일이 없을 경우에는 HTML 파일 조회이던지 redirect와 같은 네비게이션으로 동작할 수 있도록 정상 처리
                    if (logger.isDebugEnabled()) {
                        logger.debug(path + "에 해당하는 Dust 템플릿 파일이 존재하지 않습니다.", e);
                    }
                    continue;
                }
            }
        }
        // template key에 해당하는 파일이 없을 경우에는 HTML 파일 조회이던지 redirect와 같은 네비게이션으로 동작할 수 있도록 정상 처리
        return "";
    }
}
