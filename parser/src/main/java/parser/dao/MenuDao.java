package parser.dao;

import base.domain.airfoil.Airfoil;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface MenuDao extends CrudRepository<Airfoil, Long> {
}
