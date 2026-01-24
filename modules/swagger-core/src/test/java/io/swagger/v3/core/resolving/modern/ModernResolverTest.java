package io.swagger.v3.core.resolving.modern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.InputFormat;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.dialect.Dialects;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.modern.ModernResolver;
import io.swagger.v3.core.resolving.modern.jackson.JsonValueTest;
import io.swagger.v3.core.util.Json31;
import io.swagger.v3.core.util.Yaml31;
import io.swagger.v3.oas.models.media.Schema;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;

public class ModernResolverTest {
    static ObjectMapper mapper;
    static ObjectWriter pretty;

    static SchemaRegistry registry;

    public static SchemaRegistry registry() {
        if (registry == null) {
            registry = SchemaRegistry.withDefaultDialect(Dialects.getOpenApi31());
        }
        return registry;
    }

    public static ObjectWriter pretty() {
        if (pretty == null) {
            pretty = new ObjectMapper().writerWithDefaultPrettyPrinter();
        }
        return pretty;
    }

    public static ObjectMapper mapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return mapper;
    }

    public String writeValueAsString(Object value) {
        try {
            return pretty().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Schema resolve(Class<?> clazz) {
        final var model = new ModernResolver(mapper()).resolve(new AnnotatedType(clazz), null, null);
        return model;
    }

    public static void verifyInline(Schema model, String expected) {
        Approvals.verify(Yaml31.pretty(Json31.mapper().convertValue(model, ObjectNode.class)), new Options().inline(expected));
    }

    public static void verifyInline(String value, String expected) {
        Approvals.verify(value, new Options().inline(expected));
    }

    /**
     * Assert that
     */
    public void assertSchemaValidatesValue(Class<?> clazz, Object value) {
        com.networknt.schema.Schema validate = registry().getSchema(
                Json31.mapper().convertValue(resolve(clazz), JsonNode.class)
        );
        var errors = validate.validate(writeValueAsString(value), InputFormat.JSON);
        if (!errors.isEmpty()) {
            throw new RuntimeException(errors.toString());
        }
    }
}
