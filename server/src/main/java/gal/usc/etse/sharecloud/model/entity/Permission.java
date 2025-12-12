package gal.usc.etse.sharecloud.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.*;

@Document(collection = "permissions")
public class Permission {
    @Id
    private String id;
    private String resource;
    private String action;

    public Permission() {}

    public String getId() {
        return this.id;
    }

    public Permission setId(String id) {
        this.id = id;
        return this;
    }

    public String getResource() {
        return resource;
    }

    public Permission setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public String getAction() {
        return action;
    }

    public Permission setAction(String action) {
        this.action = action;
        return this;
    }

}
