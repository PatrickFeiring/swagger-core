package io.swagger.v3.core.modern;

import io.swagger.v3.oas.models.media.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed class Model {
    static final class Object extends Model {
        public Map<String, Model> properties;
        public Model additionalProperties;

        public Object() {
            this.properties = new HashMap<>();
            this.additionalProperties = null;
        }

        public void addProperty(String name, Model model) {
            this.properties.put(name, model);
        }

        public void setAdditionalProperties(Model model) {
            this.additionalProperties = model;
        }
    }

    static final class Array extends Model {
        public Model items;
        public boolean uniqueItems;

        public Array(Model items, boolean uniqueItems) {
            this.items = items;
            this.uniqueItems = uniqueItems;
        }
    }

    static final class StringModel extends Model {
        public StringFormat format;

        public StringModel() {
            this.format = null;
        }

        public StringModel(StringFormat format) {
            this.format = format;
        }

        public void format(StringFormat format) {
            this.format = format;
        }
    }

    static final class IntegerModel extends Model {
        public IntegerFormat format;

        public IntegerModel() {
            this.format = format;
        }

        public IntegerModel(IntegerFormat format) {
            this.format = format;
        }

        public void format(IntegerFormat format) {
            this.format = format;
        }
    }

    static final class NumberModel extends Model {
    }

    static final class StringEnum extends Model {
        List<String> names;

        public StringEnum(List<String> names) {
            this.names = names;
        }
    }

    static final class IntegerEnum extends Model {
        List<Integer> values;

        public IntegerEnum(List<Integer> values) {
            this.values = values;
        }
    }

    public Schema toSchema() {
        Schema schema = new Schema();

        switch (this) {
            case Object obj -> {
                schema.addType("object");

                if (!obj.properties.isEmpty()) {
                    var properties = new HashMap<String, Schema>();
                    for (var entry : obj.properties.entrySet()) {
                        properties.put(entry.getKey(), entry.getValue().toSchema());
                    }
                    schema.properties(properties);
                }

                if (obj.additionalProperties != null) {
                    schema.additionalProperties(obj.additionalProperties.toSchema());
                }
            }
            case Array array -> {
                schema.addType("array");
                schema.items(array.items.toSchema());
                if (array.uniqueItems) {
                    schema.uniqueItems(array.uniqueItems);
                }
            }
            case StringModel value -> {
                schema.addType("string");
            }
            case IntegerModel value -> {
                schema.addType("integer");
                if (value.format != null) {
                    schema.format(switch (value.format) {
                        case Int32 -> "int32";
                        case Int64 -> "int64";
                    });
                }
            }
            case NumberModel value -> {
                schema.addType("number");
            }
            case StringEnum value -> {
                schema.addType("string");
                schema.setEnum(value.names);
            }
            case IntegerEnum value -> {
                schema.addType("integer");
                schema.setEnum(value.values);
            }
            default -> throw new IllegalStateException("Unexpected value: " + this);
        }
        return schema;
    }
}
