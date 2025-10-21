package gal.usc.etse.sharecloud.repository;
import com.mongodb.lang.NonNull;
import gal.usc.etse.sharecloud.model.User;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.jspecify.annotations.NonNull;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<@NonNull User, @NonNull String> {
    @NonNull
    List<User> findAll(Example<@NonNull User> example);
}
