package io.swagger.v3.core.resolving.modern.basic;

import io.swagger.v3.core.resolving.modern.ModernResolverTest;
import org.testng.annotations.Test;

public class EnumTest extends ModernResolverTest {
    enum Status {
        PENDING,
        SUCCESS,
        ERROR
    }

   @Test
   public void testStringEnum() {
       // language=yaml
       var expected = """
            type: object
            properties:
              first:
                type: string
              second:
                type: number
            """;
       verifyInline(resolve(Status.class), expected);
   }
}
