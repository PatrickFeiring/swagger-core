package io.swagger.v3.core.modern;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.EnumNamingStrategy;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.EnumNamingStrategyFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.EnumValues;

public class EnumResolver {

    // This is a copy of BasicSerializerFactory._removeEnumSelfReferences used in buildEnumSerializer
    public static void removeSelfReferences(BeanDescription beanDesc) {
        Class<?> aClass = ClassUtil.findEnumType(beanDesc.getBeanClass());
        var it = beanDesc.findProperties().iterator();
        while (it.hasNext()) {
            BeanPropertyDefinition property = it.next();
            JavaType propType = property.getPrimaryType();
            // is the property a self-reference?
            if (propType.isEnumType() && propType.isTypeOrSubTypeOf(aClass)
                    // [databind#4564] Since 2.16.3, Enum's should allow self as field, so let's remove only if static.
                    && property.getAccessor().isStatic()) {
                it.remove();
            }
        }
    }

    // This is a variant of EnumSerializer._isShapeWrittenUsingIndex
    public static boolean asIndex(JsonFormat.Value format) {
        JsonFormat.Shape shape = (format == null) ? null : format.getShape();
        if (shape == null) {
            return false;
        }
        // i.e. "default", check dynamically
        if (shape == JsonFormat.Shape.ANY || shape == JsonFormat.Shape.SCALAR) {
            return false;
        }
        // 19-May-2016, tatu: also consider "natural" shape
        if (shape == JsonFormat.Shape.STRING || shape == JsonFormat.Shape.NATURAL) {
            return Boolean.FALSE;
        }
        // 01-Oct-2014, tatu: For convenience, consider "as-array" to also mean 'yes, use index')
        if (shape.isNumeric() || (shape == JsonFormat.Shape.ARRAY)) {
            return Boolean.TRUE;
        }
        // consider throw
        return false;
    }

    protected static EnumValues constructEnumNamingStrategyValues(SerializationConfig config, Class<Enum<?>> enumClass,
                                                                  AnnotatedClass annotatedClass) {
        Object namingDef = config.getAnnotationIntrospector().findEnumNamingStrategy(config, annotatedClass);
        EnumNamingStrategy enumNamingStrategy = EnumNamingStrategyFactory.createEnumNamingStrategyInstance(
                namingDef, config.canOverrideAccessModifiers(), config.getEnumNamingStrategy());
        return enumNamingStrategy == null ? null : EnumValues.constructUsingEnumNamingStrategy(
                config, annotatedClass, enumNamingStrategy);
    }
}
