package gal.usc.etse.sharecloud.repository;

import gal.usc.etse.sharecloud.model.entity.Reaccion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReactionRepository extends MongoRepository<Reaccion, String> {
    List<Reaccion> findByReceiverIdOrderByCreatedAtDesc(String receiverId);
}
