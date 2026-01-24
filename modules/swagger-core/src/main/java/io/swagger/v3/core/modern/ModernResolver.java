package io.swagger.v3.core.modern;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BasicSerializerFactory;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.util.EnumValues;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.jackson.AbstractModelConverter;
import io.swagger.v3.oas.models.media.Schema;

import java.util.*;

public class ModernResolver extends AbstractModelConverter implements ModelConverter {
    public ModernResolver(ObjectMapper mapper) {
        super(mapper);
    }

    class InternalSerializerFactory extends BasicSerializerFactory {

        protected InternalSerializerFactory() {
            super(null);
        }

        @Override
        public SerializerFactory withConfig(SerializerFactoryConfig config) {
            return null;
        }

        @Override
        public JsonSerializer<Object> createSerializer(SerializerProvider prov, JavaType type) throws JsonMappingException {
            return null;
        }

        @Override
        protected Iterable<Serializers> customSerializers() {
            return null;
        }

        protected JsonSerializer<?> buildEnumSerializer(SerializationConfig config,
                                                        JavaType type, BeanDescription beanDesc) {
            try {
                return super.buildEnumSerializer(config, type, beanDesc);
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Model resolveModel(AnnotatedType annotatedType) {
        JavaType type;
        if (annotatedType.getType() instanceof JavaType) {
            type = (JavaType) annotatedType.getType();
        } else {
            type = _mapper.constructType(annotatedType.getType());
        }

        BeanDescription beanDesc = _mapper.getSerializationConfig().introspect(type);

        var accessor = beanDesc.findJsonValueAccessor();
        if (accessor != null) {
            type = accessor.getType();
            beanDesc = _mapper.getSerializationConfig().introspect(type);
        }

        // TODO: keep track of this in model
        if (type.getRawClass().getCanonicalName().equals("java.util.Optional") || type.isReferenceType()) {
            return resolveModel(new AnnotatedType(type.containedType(0)));
        }

        if (type.getRawClass().equals(Integer.class)) {
            return new Model.IntegerModel(IntegerFormat.Int32);
        } else if (type.getRawClass().equals(int.class)) {
            return new Model.IntegerModel(IntegerFormat.Int32);
        } else if (type.getRawClass().equals(Long.class)) {
            return new Model.IntegerModel(IntegerFormat.Int64);
        } else if (type.getRawClass().equals(long.class)) {
            return new Model.IntegerModel(IntegerFormat.Int64);
        } else if (type.getRawClass().equals(String.class)) {
            return new Model.StringModel();
        } else if (type.getRawClass().equals(OptionalInt.class)) {
            return new Model.IntegerModel(IntegerFormat.Int32);
        } else if (type.getRawClass().equals(OptionalLong.class)) {
            return new Model.IntegerModel(IntegerFormat.Int64);
        } else if (type.getRawClass().equals(OptionalDouble.class)) {
            return new Model.NumberModel();
        }

        // We need to use isContainerType instead of isArrayType to also cover
        // set types etc.
        if (type.isContainerType()) {
            var key = type.getKeyType();
            var content = type.getContentType();
            if (key == null) {
                return new Model.Array(
                        resolveModel(new AnnotatedType().type(content)),
                        AnnotationUtils.isSetType(type.getRawClass())
                );
            } else {
                var model = new Model.Object();
                model.setAdditionalProperties(resolveModel(new AnnotatedType().type(content)));
                return model;
            }
        }

        var format = beanDesc.findExpectedFormat();

        // This is modelled on Jacksons BasicSerializerFactory.buildEnumSerializer
        if (type.isEnumType()) {
            if (format != null && format.getShape().equals(JsonFormat.Shape.OBJECT)) {
                EnumResolver.removeSelfReferences(beanDesc);
            } else {
                // Model of buildEnumSerializer
                // the part which is EnumSerializer.construct
                var enumClass = (Class<Enum<?>>) type.getRawClass();
                var enumValues = EnumValues.constructFromName(_mapper.getSerializationConfig(), beanDesc.getClassInfo());
                var varlues = enumValues.values().stream().map(value -> value.getValue()).toList();

                var asIndex = EnumResolver.asIndex(format);

                var strategyValues = EnumResolver.constructEnumNamingStrategyValues(_mapper.getSerializationConfig(), enumClass, beanDesc.getClassInfo());

                if (strategyValues != null) {
                    Model.StringEnum model = new Model.StringEnum(strategyValues.values().stream().map(value -> value.getValue()).toList());
                    return model;
                } else if (asIndex) {
                    Model.IntegerEnum model = new Model.IntegerEnum(enumValues.enums().stream().map(value -> value.ordinal()).toList());
                    return model;
                } else {
                    Model.StringEnum model = new Model.StringEnum(varlues);
                    return model;
                }
            }
        }

        Model.Object model = new Model.Object();
        List<BeanPropertyDefinition> properties = beanDesc.findProperties();

        AnnotationIntrospector introspector = _mapper.getSerializationConfig().getAnnotationIntrospector();
        JsonIgnoreProperties.Value v = introspector.findPropertyIgnoralByName(_mapper.getSerializationConfig(), beanDesc.getClassInfo());
        var b = v != null ? v.findIgnoredForSerialization() : null;

        for (BeanPropertyDefinition propDef : properties) {
            String propName = propDef.getName();
            AnnotatedMember member = propDef.getPrimaryMember();
            PropertyMetadata md = propDef.getMetadata();

            JavaType propType = member.getType();
            String name = member.getName();

            // member is equal to type?

            if (b.contains(name)) {
                continue;
            }

            Model child = resolveModel(new AnnotatedType().type(propType));
            model.addProperty(member.getName(), child);
        }

        return model;
    }

    @Override
    public Schema resolve(AnnotatedType annotatedType, ModelConverterContext context, Iterator<ModelConverter> next) {
        return resolveModel(annotatedType).toSchema();
    }
}
