package gal.usc.etse.sharecloud.repository;

import gal.usc.etse.sharecloud.model.ActivityType;
import gal.usc.etse.sharecloud.model.entity.UserActivity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends MongoRepository<UserActivity, String> {
    Optional<UserActivity> findByUserIdAndType(String userId, ActivityType type);
}
