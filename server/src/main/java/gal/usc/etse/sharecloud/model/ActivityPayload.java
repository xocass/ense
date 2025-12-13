package gal.usc.etse.sharecloud.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ListenedTrackPayload.class, name = "LISTENED_TRACK"),
        @JsonSubTypes.Type(value = LinkedSpotifyPayload.class, name = "LINKED_SPOTIFY")
})
public interface ActivityPayload {}
