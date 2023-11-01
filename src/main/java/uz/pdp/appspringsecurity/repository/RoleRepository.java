package uz.pdp.appspringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.appspringsecurity.entity.Role;
import uz.pdp.appspringsecurity.projection.role.RoleCustomProjection;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByIsUserTrue();

    Optional<Role> findByIsAdminTrue();
    @Query(value = "select r.name,count(u.id) from Role r left join User u ON r=u.role GROUP BY r.name")
    List<Object[]> getRolesWithCount();

    @Query(value = "select r.name as name," +
            "COUNT(u.id) as soni,"+
            "MAX (u.id) as kattaId from Role r LEFT JOIN User u on r=u.role GROUP BY r.name")
    List<RoleCustomProjection>getRolesWithCount2();

}
