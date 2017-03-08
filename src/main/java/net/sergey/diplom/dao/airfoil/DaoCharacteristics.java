package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Characteristics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class DaoCharacteristics {
    @Autowired
    private MongoOperations mongoOperations;

    public List<Characteristics> findCharacteristicsByTemplate(Query query) {
        return mongoOperations.find(query, Characteristics.class);
    }

    public List<Airfoil> findCharacteristicsByTemplateA(Query query) {
        return mongoOperations.find(query, Airfoil.class);
    }

}
