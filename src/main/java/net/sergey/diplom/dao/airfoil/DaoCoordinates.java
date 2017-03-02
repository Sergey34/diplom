package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Coordinates;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface DaoCoordinates extends CrudRepository<Coordinates, String>, JpaSpecificationExecutor<Coordinates> {
}
