package com.example.recipeapp.config;

import com.example.recipeapp.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class UserSimpleSerializer extends StdSerializer<User> {

    public UserSimpleSerializer() {
        this(null);
    }

    public UserSimpleSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("alias", user.getAlias());
        gen.writeStringField("email", user.getEmail());
        gen.writeEndObject();
    }
}
