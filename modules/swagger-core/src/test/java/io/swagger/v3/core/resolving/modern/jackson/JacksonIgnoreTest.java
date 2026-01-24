package io.swagger.v3.core.resolving.modern.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.core.resolving.modern.ModernResolverTest;
import org.testng.annotations.Test;

public class JacksonIgnoreTest extends ModernResolverTest {

    public record Value(
            String first,
            @JsonIgnore
            String second
    ) {
    }

    @Test
    public void testJsonIgnore() {
        // language=yaml
        var expected = """
            type: object
            properties:
              first:
                type: string
            """;
        verifyInline(resolve(Value.class), expected);
    }

    @JsonIgnoreProperties({"first"})
    public record IgnoreTest(
            String first,
            String second
    ) {
    }

    @Test
    public void testJsonIgnoreProperties() {
        // language=yaml
        var expected = """
            type: object
            properties:
              second:
                type: string
            """;
        verifyInline(resolve(IgnoreTest.class), expected);
    }
}
