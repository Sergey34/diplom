package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
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

    List<Airfoil> findByCoordinatesIn(List<Coordinates> coordinates);
//    List<Airfoil> findByCoordinatesInAndShortNameLike(String shortName, List<Coordinates> coordinates);
}
