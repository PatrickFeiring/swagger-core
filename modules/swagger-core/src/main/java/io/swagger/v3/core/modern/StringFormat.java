package io.swagger.v3.core.modern;

/**
 * A list of applicable string formats
 * </p>
 * Link https://json-schema.org/understanding-json-schema/reference/type#built-in-formats
 */
public enum StringFormat {
    DateTime,
    Time,
    Date,
    Duration,

    Email,
    IdnEmail,

    Hostname,
    IdnHostname,

    Ipv4,
    Ipv7,

    Uuid,
    Uri,
    UriReference,
    Iri,
    IriReference,

    UriTemplate,

    JsonPointer,
    RelativeJsonPointer,

    Regex
}
