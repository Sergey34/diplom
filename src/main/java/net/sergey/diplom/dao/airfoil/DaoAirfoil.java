package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Airfoil;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface DaoAirfoil extends CrudRepository<Airfoil, ObjectId>, PagingAndSortingRepository<Airfoil, ObjectId> {
    @RestResource(path = "/prefix")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    List<Airfoil> findByPrefixOrderByShortName(@Param("prefix") char prefix, Pageable pageable);

    @RestResource(path = "/shortName")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    Airfoil findOneByShortName(@Param("shortName") String shortName);

    @RestResource(path = "/template")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    List<Airfoil> findByShortNameRegex(@Param("template") String shortName, Pageable pageRequest);

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    <S extends Airfoil> Iterable<S> save(Iterable<S> iterable);


    @RestResource(exported = false)
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    int countByPrefix(@Param("prefix") char prefix);

    @RestResource(exported = false)
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    int countByShortNameRegex(String shortNameTemplate);

    @RestResource(exported = false)
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    List<Airfoil> findDistinctAirfoilByCharacteristics_idInAndShortNameRegex(@Param("Characteristics_id") Set<ObjectId> ids,
                                                                             @Param("template") String shortNameTemplate);
}
