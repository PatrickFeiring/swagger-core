package io.swagger.v3.core.resolving.modern.jackson.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.core.resolving.modern.ModernResolverTest;
import org.testng.annotations.Test;

public class JsonFormatTest extends ModernResolverTest {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum User {
        Admin(1, "Admin"),
        Regular(2, "Regular");

        User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @JsonProperty("id")
        public int id;

        @JsonProperty("name")
        public String name;
    }

    @Test
    public void testSchema() {
        // language=yaml
        var expected = """
            type: object
            properties:
              name:
                type: string
              id:
                type: integer
                format: int32
            """;
        verifyInline(resolve(User.class), expected);
    }
    
    @Test
    public void testSerialization() {
        var value = User.Admin;
        // language=json
        var expected = """
            {
              "id" : 1,
              "name" : "Foo"
            }
            """;
        verifyInline(writeValueAsString(value), expected);
        assertSchemaValidatesValue(User.class, value);
    }
}
