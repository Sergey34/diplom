package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Airfoil;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface DaoAirfoil extends CrudRepository<Airfoil, ObjectId>, PagingAndSortingRepository<Airfoil, ObjectId> {
    @RestResource(path = "/prefix")
    List<Airfoil> findByPrefixOrderByShortName(@Param("prefix") char prefix, Pageable pageable);

    @RestResource(path = "/shortName")
    Airfoil findOneByShortName(@Param("shortName") String shortName);

    @RestResource(path = "/byCharacteristicsIdAndTemplate")
    List<Airfoil> findDistinctAirfoilByCharacteristics_idInAndShortNameRegex(@Param("Characteristics_id") Set<ObjectId> characteristics, @Param("template") String shortName, Pageable pageRequest);

    @RestResource(path = "/template")
    List<Airfoil> findByShortNameRegex(@Param("template") String shortName, Pageable pageRequest);

    @RestResource(exported = false)
    int countByPrefix(@Param("prefix") char prefix);

    @RestResource(exported = false)
    int countByShortNameRegex(String shortNameTemplate);

    @RestResource(exported = false)
    int countDistinctAirfoilByCharacteristics_idInAndShortNameRegex(Set<ObjectId> characteristics, String shortNameTemplate);

    @RestResource(exported = false)
    List<Airfoil> findDistinctAirfoilByCharacteristics_idInAndShortNameRegex(@Param("Characteristics_id") Set<ObjectId> ids, @Param("template") String shortNameTemplate);
}
