package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Characteristics;
import net.sergey.diplom.domain.airfoil.Prefix;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DaoAirfoil extends CrudRepository<Airfoil, String> {

    List<Airfoil> findByPrefixOrderByShortName(Prefix prefix, Pageable pageable);

    Airfoil findOneByShortName(String shortName);

    int countByPrefix(Prefix prefix);

    List<Airfoil> findAll(Pageable pageRequest);

    List<Airfoil> findDistinctAirfoilByCharacteristicsInAndShortNameRegex(List<Characteristics> characteristics, String shortName, Pageable pageRequest);

    List<Airfoil> findByShortNameRegex(String shortName, Pageable pageRequest);

    int countByShortNameRegex(String shortNameTemplate);

    int countDistinctAirfoilByCharacteristicsInAndShortNameRegex(List<Characteristics> characteristics, String shortNameTemplate);
}
