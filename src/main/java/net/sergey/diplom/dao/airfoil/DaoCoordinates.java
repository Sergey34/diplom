package net.sergey.diplom.dao.airfoil;

import net.sergey.diplom.domain.airfoil.Characteristics;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface DaoCoordinates extends CrudRepository<Characteristics, String>, JpaSpecificationExecutor<Characteristics> {
}
