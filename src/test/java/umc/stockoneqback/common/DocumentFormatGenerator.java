package umc.stockoneqback.common;

import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.snippet.Attributes.key;

public interface DocumentFormatGenerator {
    static Attributes.Attribute getDateFormat() {
        return key("format").value("yyyy-MM-dd");
    }
    static Attributes.Attribute getInputImageFormat() {
        return key("format").value("multipart/form-data");
    }

    static Attributes.Attribute getInputDTOFormat() {
        return key("format").value("application/json");
    }
}
