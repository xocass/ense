package gal.usc.etse.sharecloud.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import gal.usc.etse.sharecloud.model.User;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByUsername(String Username);
    List<User> findByEmail(String Email);

}
