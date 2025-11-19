package gal.usc.etse.sharecloud.server.repository;

import gal.usc.etse.sharecloud.server.model.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByRolename(String rolename);
}

