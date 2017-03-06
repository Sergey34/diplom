package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class DaoCoordinates {
    @Autowired
    private MongoOperations mongoOperations;

    public List<Coordinates> findCoordsByTemplate(Query query) {
        return mongoOperations.find(query, Coordinates.class);
    }

}
