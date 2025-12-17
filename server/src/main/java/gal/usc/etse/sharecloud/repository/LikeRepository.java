package gal.usc.etse.sharecloud.repository;

import gal.usc.etse.sharecloud.model.entity.Like;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LikeRepository extends MongoRepository<Like, String> {
    List<Like> findAllByReceiverId(String receiverId);
}
