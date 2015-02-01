package org.iteventviewer.common;

/**
 * Created by yuki_yoshida on 15/01/31.
 */

import android.text.TextUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalDateTimeConverter
    implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

  public static final Type TYPE = new TypeToken<LocalDateTime>() {}.getType();

  private final String format;

  public LocalDateTimeConverter(String format) {
    this.format = format;
  }

  public LocalDateTimeConverter() {
    format = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  }

  @Override public JsonElement serialize(LocalDateTime src, Type typeOfSrc,
      JsonSerializationContext context) {
    if (src == null) {
      return new JsonPrimitive("");
    }
    final DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
    return new JsonPrimitive(fmt.print(src));
  }

  @Override public LocalDateTime deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    String jsonString = json.getAsString();
    if (TextUtils.isEmpty(jsonString)) {
      return null;
    }
    final DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
    return fmt.parseLocalDateTime(jsonString);
  }
}
