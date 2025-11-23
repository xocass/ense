package gal.usc.etse.sharecloud.repository;

import gal.usc.etse.sharecloud.model.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@SuppressWarnings("UnusedReturnValue")
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Collection<RefreshToken> deleteAllByUser(String user);
    Optional<RefreshToken> findByToken(String token);
}

