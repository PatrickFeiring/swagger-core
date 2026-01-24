package io.swagger.v3.core.resolving.modern.containers;

import io.swagger.v3.core.resolving.modern.ModernResolverTest;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

public class ContainerTest extends ModernResolverTest {
    static class ArrayBean {
        public int[] a;
    }

    @Test
    public void testArray() {
        // language=yaml
        var expected = """
            type: object
            properties:
              a:
                type: array
                items:
                  type: number
            """;
        verifyInline(resolve(ArrayBean.class), expected);
    }

    static class MultidimensionalArrayBean {
        public int[][] a;
    }

    @Test
    public void testMultidimensionalArray() {
        // language=yaml
        var expected = """
            type: object
            properties:
              a:
                type: array
                items:
                  type: array
                  items:
                    type: number
            """;
        verifyInline(resolve(MultidimensionalArrayBean.class), expected);
    }

    static class SetBean {
        public Set<String> a;
    }

    @Test
    public void testSet() {
        // language=yaml
        var expected = """
            type: object
            properties:
              a:
                type: array
                items:
                  type: string
                uniqueItems: true
            """;
        verifyInline(resolve(SetBean.class), expected);
    }

    static class MapBean {
        public Map<String, Integer> a;
    }

    @Test
    public void testMap() {
        // language=yaml
        var expected = """
            type: object
            properties:
              a:
                type: object
                additionalProperties:
                  type: number
            """;
        verifyInline(resolve(MapBean.class), expected);
    }
}