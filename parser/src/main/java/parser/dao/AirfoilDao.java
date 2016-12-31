package parser.dao;

import base.domain.airfoil.Airfoil;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface AirfoilDao extends CrudRepository<Airfoil, String> {
    @Override
    Iterable<Airfoil> findAll();

    @Override
    <S extends Airfoil> Iterable<S> save(Iterable<S> airfoils);

    @Override
    <S extends Airfoil> S save(S airfoil);

    @Override
    void delete(String id);
}
