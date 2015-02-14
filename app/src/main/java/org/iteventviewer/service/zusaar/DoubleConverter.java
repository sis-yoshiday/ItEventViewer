package org.iteventviewer.service.zusaar;

/**
 * Created by yuki_yoshida on 15/01/31.
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class DoubleConverter implements JsonSerializer<Double>, JsonDeserializer<Double> {

  public static final Type TYPE = new TypeToken<Double>() {
  }.getType();

  public DoubleConverter() {
  }

  @Override
  public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
    if (src == null) {
      return JsonNull.INSTANCE;
    }
    try {
      return new JsonPrimitive(src);
    } catch (NumberFormatException e) {
      return JsonNull.INSTANCE;
    }
  }

  @Override
  public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    String jsonString = json.getAsString();
    try {
      return Double.valueOf(jsonString);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
