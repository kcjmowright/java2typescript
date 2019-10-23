package java2typescript.jaxrs.model;

import java.util.HashMap;
import java.util.Map;

public class MediaType {
  private String mime;
  private ResponseType responseType;

  public enum ResponseType {
    JSON, TEXT, ARRAYBUFFER, BLOB, DOCUMENT;

    public String value() {
      return name().toLowerCase();
    }
  }

  private static final Map<String, MediaType> TYPES = new HashMap<>();
  public static final MediaType JSON = MediaType.of("application/json", ResponseType.JSON);
  public static final MediaType FORM = MediaType.of("application/x-www-form-urlencoded", ResponseType.TEXT);
  public static final MediaType TEXT = MediaType.of("text/plain", ResponseType.TEXT);
  public static final MediaType XML = MediaType.of("application/xml", ResponseType.DOCUMENT);
  public static final MediaType OCTET_STREAM = MediaType.of("application/octet-stream", ResponseType.BLOB);
  public static final MediaType TEXT_CSV = MediaType.of("text/csv", ResponseType.BLOB);

  public static final MediaType of(String mime, ResponseType responseType) {
    return TYPES.computeIfAbsent(mime, $ -> new MediaType(mime, responseType));
  }

  public static final MediaType of(String mime) {
    return of(mime, ResponseType.BLOB);
  }

  public MediaType(String mime, ResponseType responseType) {
    this.mime = mime;
    this.responseType = responseType;
  }

  public String getMime() {
    return mime;
  }

  public ResponseType getResponseType() {
    return responseType;
  }

  public String getResponseTypeAsString() {
    return responseType.value();
  }
}
