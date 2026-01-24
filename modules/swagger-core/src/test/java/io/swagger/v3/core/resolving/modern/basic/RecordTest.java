package io.swagger.v3.core.resolving.modern.basic;

import io.swagger.v3.core.resolving.modern.ModernResolverTest;
import org.testng.annotations.Test;

public class RecordTest extends ModernResolverTest {

    record Data(
            String first,
            Integer second
    ) {
    }

    @Test
    public void testRecord() {
        // language=yaml
        var expected = """
            type: object
            properties:
              first:
                type: string
              second:
                type: number
            """;
        verifyInline(resolve(Data.class), expected);
    }
}
