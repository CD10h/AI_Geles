package lt.aigen.geles.repositories;

import lt.aigen.geles.models.Flower;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FlowerRepository extends CrudRepository<Flower, Long> {
    @Query("from Flower f where lower(f.name) like concat('%', lower(:name), '%')")
    List<Flower> findAllByName(String name);

    @Query("from Flower f where current_date + f.daysToExpire <= :searchDate")
    List<Flower> findExpiredBeforeDate(@Param("searchDate") Date searchDate);
}