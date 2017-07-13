package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Prefix;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface DaoAirfoil extends CrudRepository<Airfoil, String> {

    List<Airfoil> findByPrefixOrderByShortName(Prefix prefix, Pageable pageable);

    Airfoil findOneByShortName(String shortName);

    int countByPrefix(Prefix prefix);

    List<Airfoil> findAll(Pageable pageRequest);

    List<Airfoil> findDistinctAirfoilByCharacteristics_fileNameInAndShortNameRegex(Set<String> characteristics, String shortName, Pageable pageRequest);

    List<Airfoil> findByShortNameRegex(String shortName, Pageable pageRequest);

    int countByShortNameRegex(String shortNameTemplate);

    int countDistinctAirfoilByCharacteristics_fileNameInAndShortNameRegex(Set<String> characteristics, String shortNameTemplate);
}
