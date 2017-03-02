package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
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

    List<AirfoilDTO> findAll(Criteria criteria);
}
