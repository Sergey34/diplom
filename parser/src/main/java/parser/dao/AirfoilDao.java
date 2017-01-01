package parser.dao;

import base.domain.airfoil.Airfoil;
import base.domain.airfoil.Prefix;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface AirfoilDao extends CrudRepository<Airfoil, String> {
    @Override
    Iterable<Airfoil> findAll();

    @Override
    <S extends Airfoil> Iterable<S> save(@NonNull Iterable<S> airfoils);

    @Override
    <S extends Airfoil> S save(@NonNull S airfoil);

    @Override
    void delete(@NonNull String id);

    List<Airfoil> findAllByPrefix(Prefix prefix);
}
