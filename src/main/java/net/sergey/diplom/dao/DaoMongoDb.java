package net.sergey.diplom.dao;

import net.sergey.diplom.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

//@Transactional
//@Repository
public class DaoMongoDb {
    private final MongoOperations mongoOperation;

    @Autowired
    public DaoMongoDb(MongoTemplate mongoTemplate) {
        this.mongoOperation = mongoTemplate;
    }

    public void foo() {
        Query searchUserQuery = new Query(Criteria.where("username").is("mkyong"));
        User savedUser = mongoOperation.findOne(searchUserQuery, User.class);
        System.out.println(savedUser);
    }
}
