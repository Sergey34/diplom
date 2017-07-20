package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Characteristics;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class DaoCharacteristics {
    private MongoOperations mongoOperations;

    @Autowired
    public DaoCharacteristics(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Set<ObjectId> findCharacteristicsByTemplate(Query query) {
        List<Characteristics> characteristicsList = mongoOperations.find(query, Characteristics.class);
        Set<ObjectId> ids = new HashSet<>();
        for (Characteristics characteristics : characteristicsList) {
            ids.add(characteristics.getId());
        }
        return ids;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(Collection<Characteristics> characteristics) {
        if (characteristics == null) {return;}
        for (Characteristics characteristic : characteristics) {
            mongoOperations.save(characteristic);
        }
    }

}
