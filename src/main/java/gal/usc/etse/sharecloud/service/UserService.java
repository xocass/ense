package gal.usc.etse.sharecloud.service;
import gal.usc.etse.sharecloud.exception.UserAlreadyExistsException;
import gal.usc.etse.sharecloud.model.User;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mongodb.lang.NonNull;
import gal.usc.etse.sharecloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchOperation;

import java.util.List;

@Service
public class UserService {
    private final UserRepository users;
    private final JsonMapper mapper;
    private final MongoTemplate mongoTemplate;

    //@Autowired
    public UserService(UserRepository usuarios, JsonMapper mapper){
        this.users = usuarios;
        this.mapper = mapper;
    }

    // Inxecta MongoTemplate no constructor
    @Autowired
    public UserService(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }



    public User signUpUser(@NonNull User user) throws UserAlreadyExistsException {
        Example<User> filter = Example.of(user);
        if(users.findAll(filter).isEmpty()){
            users.save(user);
            return user;
        }else{
            throw new UserAlreadyExistsException();
        }
    }

    public User updateUser(String email, List<JsonPatchOperation> changes) {
        User userFilter= new User();
        userFilter.setEmail(email);
        Example<User> filter = Example.of(userFilter);
        User user = users.findAll(filter).getFirst();

        JsonNode patched = JsonPatch.apply(changes, mapper.convertValue(user, JsonNode.class));
        User updatedUser = mapper.convertValue(patched, User.class);
        return users.save(updatedUser);
    }

    //Mongo
    public List<User> findUserByCriteria(String email, Integer age){
        // Creamos un obxecto Criteria
        Criteria criteria = new Criteria();

        // Engadimos condicións de forma condicional
        if (email != null && !email.isEmpty()) {
            criteria.and("email").regex(".*" + email + ".*", "i"); // Busca parcial e insensible a maiúsculas
        }
        if (age != null) {
            criteria.and("age").is(age);
        }

        // Creamos un obxecto Query a partir do Criteria e do Pageable
        Query query = Query.query(criteria);

        // Executamos a consulta
        List<User> users = mongoTemplate.find(query, User.class);

        return users;
    }
}
