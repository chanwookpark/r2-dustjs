package r2.dustjs.spring;

import org.junit.Test;
import org.modelmapper.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chanwook
 */
public class DustModelTest {

    @Test
    public void bindList() throws Exception {

        DustModel dm = new DustModel();

        List<Entity> list = new ArrayList<Entity>();
        list.add(new Entity("entity1"));
        list.add(new Entity("entity2"));

        dm.bind("key", list, new TypeToken<List<DTO>>(){});

        final List result = (List) dm.get("key");

        assert result.get(0) instanceof DTO;
    }

    static class Entity {
        String name;

        public Entity() {
        }

        public Entity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class DTO {
        String name;

        public DTO() {
        }

        public DTO(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
