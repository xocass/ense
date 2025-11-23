package gal.usc.etse.sharecloud.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import gal.usc.etse.sharecloud.model.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String Email);
    List<User> findAllByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findBySpotifyState(String spotifyState);

}
