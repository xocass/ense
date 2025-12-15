package gal.usc.etse.sharecloud.repository;

import gal.usc.etse.sharecloud.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findById(String userId);
    Optional<User> findBySpotifyProfile_SpotifyID(String spotifyID);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findTop10BySpotifyProfile_DisplayNameContainingIgnoreCase(String username);
}
