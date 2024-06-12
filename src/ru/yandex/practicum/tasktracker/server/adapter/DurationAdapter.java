package ru.yandex.practicum.tasktracker.server.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Duration;

/**
 * A custom Gson {@link TypeAdapter} for serializing and deserializing {@link Duration} objects.
 * <p>
 * The duration is processed in minutes.
 */
public class DurationAdapter extends TypeAdapter<Duration> {

  /**
   * Serializes the specified {@link Duration} object into its JSON representation.
   * The duration is converted to its total minutes.
   * @param jsonWriter    the {@link JsonWriter} to write the JSON to
   * @param duration {@link Duration} object to be serialized
   * @throws IOException if an I/O error occurs while writing the JSON
   */
  @Override
  public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
    jsonWriter.value(duration.toMinutes());
  }

  /**
   * Deserializes the specified JSON into a {@link Duration} object.
   * The JSON value is expected to be the duration in minutes.
   * @param jsonReader the {@link JsonReader} to read the JSON from
   * @return the deserialized {@link Duration} object
   * @throws IOException if an I/O error occurs while reading the JSON
   */
  @Override
  public Duration read(JsonReader jsonReader) throws IOException {
    long minutes = jsonReader.nextLong();
    return Duration.ofMinutes(minutes);
  }
}
