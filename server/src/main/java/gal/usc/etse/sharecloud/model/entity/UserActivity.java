package gal.usc.etse.sharecloud.model.entity;

import gal.usc.etse.sharecloud.model.ActivityPayload;
import gal.usc.etse.sharecloud.model.ActivityType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "user_activities")
public class UserActivity {
    @Id
    private String id;

    private String userId;
    private ActivityType type;
    private Instant updatedAt;
    private ActivityPayload payload;

    // getters / setters
    public String getId() {return id;}
    public String getUserId() {return userId;}
    public ActivityType getType() {return type;}
    public Instant getUpdatedAt() {return updatedAt;}
    public ActivityPayload getPayload() {return payload;}

    public void setId(String id) {this.id = id;}
    public void setUserId(String userId) {this.userId = userId;}
    public void setType(ActivityType type) {this.type = type;}
    public void setUpdatedAt(Instant createdAt) {this.updatedAt = createdAt;}
    public void setPayload(ActivityPayload payload) {this.payload = payload;}
}

