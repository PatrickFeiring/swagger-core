package io.swagger.v3.core.resolving.modern.jackson.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.core.resolving.modern.ModernResolverTest;
import org.testng.annotations.Test;

public class JsonPropertyEnumTest extends ModernResolverTest {

    enum Status {
        @JsonProperty("statusPending")
        PENDING,
        @JsonProperty("statusSuccess")
        SUCCESS,
        @JsonProperty("statusError")
        ERROR
    }

    @Test
    public void testSchema() {
        // language=yaml
        var expected = """
            type: string
            enum:
            - statusPending
            - statusSuccess
            - statusError
            """;
        verifyInline(resolve(Status.class), expected);
    }
}
