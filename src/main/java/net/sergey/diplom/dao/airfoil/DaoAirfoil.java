package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Airfoil;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface DaoAirfoil extends CrudRepository<Airfoil, ObjectId>, QueryByExampleExecutor<Airfoil> {//Example.of(new Airfoil())

    List<Airfoil> findByPrefixOrderByShortName(char prefix, Pageable pageable);

    Airfoil findOneByShortName(String shortName);

    int countByPrefix(char prefix);

    @Override
    @RestResource(path = "/airfoils")
    Iterable<Airfoil> findAll();

    @RestResource(path = "/airfoilsByCharacteristics_idPage")
    List<Airfoil> findDistinctAirfoilByCharacteristics_idInAndShortNameRegex(Set<ObjectId> characteristics, String shortName, Pageable pageRequest);

    List<Airfoil> findByShortNameRegex(String shortName, Pageable pageRequest);

    int countByShortNameRegex(String shortNameTemplate);

    int countDistinctAirfoilByCharacteristics_idInAndShortNameRegex(Set<ObjectId> characteristics, String shortNameTemplate);

    @RestResource(path = "/airfoilsByCharacteristics_id")
    List<Airfoil> findDistinctAirfoilByCharacteristics_idInAndShortNameRegex(Set<ObjectId> ids, String shortNameTemplate);
}
