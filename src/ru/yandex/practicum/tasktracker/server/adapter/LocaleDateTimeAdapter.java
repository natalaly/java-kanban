package ru.yandex.practicum.tasktracker.server.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A custom Gson {@link TypeAdapter} for serializing and deserializing {@link LocalDateTime} objects.
 * <p>
 * The date-time format used is "dd.MM.yyyy HH:mm".
 */
public class LocaleDateTimeAdapter extends TypeAdapter<LocalDateTime> {
//  private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm");
  private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  /**
   * Serializes the specified {@link LocalDateTime} object into its JSON representation.
   * @param jsonWriter    the {@link JsonWriter} to write the JSON to
   * @param localDateTime the {@link LocalDateTime} object to be serialized
   * @throws IOException if an I/O error occurs while writing the JSON
   */
  @Override
  public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
    if (localDateTime == null) {
      jsonWriter.nullValue();
    } else {
      jsonWriter.value(localDateTime.format(DTF));
    }
  }

  /**
   * Deserializes the specified JSON into a {@link LocalDateTime} object.
   * @param jsonReader the {@link JsonReader} to read the JSON from
   * @return the deserialized {@link LocalDateTime} object
   * @throws IOException if an I/O error occurs while reading the JSON
   */
  @Override
  public LocalDateTime read(JsonReader jsonReader) throws IOException {
    if (jsonReader.peek() == JsonToken.NULL) {
      jsonReader.nextNull();
      return null;
    }
    if (jsonReader.peek() == JsonToken.STRING) {
      return LocalDateTime.parse(jsonReader.nextString(), DTF);
    }
    throw new UnsupportedOperationException("Unsupported time format");
  }
}
