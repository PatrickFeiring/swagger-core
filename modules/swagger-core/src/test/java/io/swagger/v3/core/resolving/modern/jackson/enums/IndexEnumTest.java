package io.swagger.v3.core.resolving.modern.jackson.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.core.resolving.modern.ModernResolverTest;
import org.testng.annotations.Test;

public class IndexEnumTest extends ModernResolverTest {
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum Status {
        PENDING,
        SUCCESS,
        ERROR
    }

    @Test
    public void testSchema() {
        // language=yaml
        var expected = """
            type: int
            enum:
            - 0
            - 1
            - 2
            """;
        verifyInline(resolve(Status.class), expected);
    }

    @Test
    public void testSerialization() {
        var value = Status.PENDING;
        // language=json
        var expected = """
            0
            """;
        verifyInline(writeValueAsString(value), expected);
        assertSchemaValidatesValue(Status.class, value);
    }
}
