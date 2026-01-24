package io.swagger.v3.core.resolving.modern.jackson;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.core.resolving.modern.ModernResolverTest;
import org.testng.annotations.Test;

/**
 * Test JsonValue annotations
 * </p>
 * A method annotated with JsonValue will have it's JSON value generated
 * not from the fields themselves, but rather from calling the annotated
 * method. This means the schema need to use the return value of the method
 * rather than the class itself.
 */
public class JsonValueTest extends ModernResolverTest {

    public class Value {
        public Integer component;

        @JsonValue
        public String getValue() {
            return "custom";
        }
    }

    @Test
    public void testSchema() {
        // language=yaml
        var expected = """
            type: string
            """;
        verifyInline(resolve(Value.class), expected);
    }

    @Test
    public void testSerialization() {
        var value = new Value();
        // language=yaml
        var expected = """
            "custom"
            """;
        verifyInline(writeValueAsString(value), expected);
        assertSchemaValidatesValue(Value.class, value);
    }
}
