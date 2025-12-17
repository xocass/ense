package gal.usc.etse.sharecloud.repository;

import gal.usc.etse.sharecloud.model.entity.FeedCard;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedRepository extends MongoRepository<FeedCard, String> {
    Optional<FeedCard>findById(String id);
}
