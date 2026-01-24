package io.swagger.v3.core.resolving.modern.basic;

import io.swagger.v3.core.resolving.modern.ModernResolverTest;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URL;
import java.util.*;

public class PrimitivesTest extends ModernResolverTest {

    public record Strings(
            String a,
            Byte b,
            byte c,
            byte[] d,
            URI uri,
            URL url,
            UUID uuid
    ) {
    }

    @Test
    public void testStrings() {
        // language=yaml
        var expected = """
            type: object
            properties:
              first:
                type: string
              second:
                type: number
            """;
        verifyInline(resolve(Strings.class), expected);
    }

    public record Numbers(
            Number a
//            Integer a,
//            int a,
//            BigInteger a,
//            Short a,
//            short a,
//            Long a,
//            long a,
//            Float a,
//            float a,
//            Double a,
//            double a,
//            BigDecimal a,
    ) {
    }

    public record Booleans(
            Boolean a,
            boolean b
    ) {
    }

    public record Optionals(
        OptionalInt a,
        OptionalLong b,
        OptionalDouble c,
        Optional<String> d
    ) {
    }

    @Test
    public void testOptionals() {
        // language=yaml
        var expected = """
            type: object
            properties:
              a:
                type: integer
                format: int32
              b:
                type: integer
                format: int64
              c:
                type: number
              d:
                type: string
            """;
        verifyInline(resolve(Optionals.class), expected);
    }
}
