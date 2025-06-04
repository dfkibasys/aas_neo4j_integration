import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
  GetAssetAdministrationShellsResult.JSON_PROPERTY_PAGING_METADATA,
  GetAssetAdministrationShellsResult.JSON_PROPERTY_RESULT
})

public class GetAssetAdministrationShellsResult {
  public static final String JSON_PROPERTY_PAGING_METADATA = "paging_metadata";
  private PagedResultPagingMetadata pagingMetadata;

  public static final String JSON_PROPERTY_RESULT = "result";
  private List<AssetAdministrationShell> result;

  public GetAssetAdministrationShellsResult() { 
  }

  public GetAssetAdministrationShellsResult pagingMetadata(PagedResultPagingMetadata pagingMetadata) {
    this.pagingMetadata = pagingMetadata;
    return this;
  }

   /**
   * Get pagingMetadata
   * @return pagingMetadata
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_PAGING_METADATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public PagedResultPagingMetadata getPagingMetadata() {
    return pagingMetadata;
  }


  @JsonProperty(JSON_PROPERTY_PAGING_METADATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPagingMetadata(PagedResultPagingMetadata pagingMetadata) {
    this.pagingMetadata = pagingMetadata;
  }


  public GetAssetAdministrationShellsResult result(List<AssetAdministrationShell> result) {
    this.result = result;
    return this;
  }

  public GetAssetAdministrationShellsResult addResultItem(AssetAdministrationShell resultItem) {
    if (this.result == null) {
      this.result = new ArrayList<>();
    }
    this.result.add(resultItem);
    return this;
  }

   /**
   * Get result
   * @return result
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_RESULT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<AssetAdministrationShell> getResult() {
    return result;
  }


  @JsonProperty(JSON_PROPERTY_RESULT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setResult(List<AssetAdministrationShell> result) {
    this.result = result;
  }


  /**
   * Return true if this GetAssetAdministrationShellsResult object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetAssetAdministrationShellsResult getAssetAdministrationShellsResult = (GetAssetAdministrationShellsResult) o;
    return Objects.equals(this.pagingMetadata, getAssetAdministrationShellsResult.pagingMetadata) &&
        Objects.equals(this.result, getAssetAdministrationShellsResult.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pagingMetadata, result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetAssetAdministrationShellsResult {\n");
    sb.append("    pagingMetadata: ").append(toIndentedString(pagingMetadata)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
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

