package de.dfki.cos.aas2graph.kafka.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY, 
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AasEvent.class, name = "AAS_CREATED"),
    @JsonSubTypes.Type(value = AasEvent.class, name = "AAS_UPDATED"),
    @JsonSubTypes.Type(value = AasEvent.class, name = "AAS_DELETED"),
    @JsonSubTypes.Type(value = AasEvent.class, name = "SM_REF_ADDED"),
    @JsonSubTypes.Type(value = AasEvent.class, name = "SM_REF_DELETED"),
    @JsonSubTypes.Type(value = AasEvent.class, name = "ASSET_INFORMATION_SET"),
    
    @JsonSubTypes.Type(value = SubmodelEvent.class, name = "SM_CREATED"),
    @JsonSubTypes.Type(value = SubmodelEvent.class, name = "SM_UPDATED"),
    @JsonSubTypes.Type(value = SubmodelEvent.class, name = "SM_DELETED"),
    @JsonSubTypes.Type(value = SubmodelEvent.class, name = "SME_UPDATED"),
    @JsonSubTypes.Type(value = SubmodelEvent.class, name = "SME_CREATED"),
    @JsonSubTypes.Type(value = SubmodelEvent.class, name = "SME_DELETED")
})
public interface Event {

}
