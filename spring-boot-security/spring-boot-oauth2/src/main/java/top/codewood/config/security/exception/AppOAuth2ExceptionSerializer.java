package top.codewood.config.security.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class AppOAuth2ExceptionSerializer extends StdSerializer<AppOAuth2Exception> {

    public AppOAuth2ExceptionSerializer() {
        super(AppOAuth2Exception.class);
    }

    @Override
    public void serialize(AppOAuth2Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("code", value.getHttpErrorCode());
        gen.writeStringField("message", value.getMessage());
        gen.writeEndObject();
    }
}
