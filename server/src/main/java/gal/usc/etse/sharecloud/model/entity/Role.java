package gal.usc.etse.sharecloud.model.entity;

import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;


@Document(collection = "roles")
public class Role {
    @Id
    private String rolename;


    private Set<Role> includes;
    private Set<Permission> permissions;

    public Role() { }

    public Role(String rolename, Set<Role> includes, Set<Permission> permissions) {
        this.rolename = rolename;
        this.includes = includes;
        this.permissions = permissions;
    }

    public String getRolename() {
        return rolename;
    }

    public Role setRolename(String rolename) {
        this.rolename = rolename;
        return this;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Role setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }

    public Set<Role> getIncludes() {
        return includes;
    }

    public Role setIncludes(Set<Role> includes) {
        this.includes = includes;
        return this;
    }

}
