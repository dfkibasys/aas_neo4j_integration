

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * PagedResultPagingMetadata
 */
@JsonPropertyOrder({
  PagedResultPagingMetadata.JSON_PROPERTY_CURSOR
})
public class PagedResultPagingMetadata {
  public static final String JSON_PROPERTY_CURSOR = "cursor";
  private String cursor;

  public PagedResultPagingMetadata() { 
  }

  public PagedResultPagingMetadata cursor(String cursor) {
    this.cursor = cursor;
    return this;
  }

   /**
   * Get cursor
   * @return cursor
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_CURSOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getCursor() {
    return cursor;
  }


  @JsonProperty(JSON_PROPERTY_CURSOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCursor(String cursor) {
    this.cursor = cursor;
  }


  /**
   * Return true if this PagedResultPagingMetadata object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PagedResultPagingMetadata pagedResultPagingMetadata = (PagedResultPagingMetadata) o;
    return Objects.equals(this.cursor, pagedResultPagingMetadata.cursor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cursor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PagedResultPagingMetadata {\n");
    sb.append("    cursor: ").append(toIndentedString(cursor)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

