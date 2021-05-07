package lt.aigen.geles.repositories;

import lt.aigen.geles.models.Flower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

@Repository
public interface FlowerRepository extends JpaRepository<Flower, Long> {
    List<Flower> findAllByNameContainsIgnoreCase(String name);

    @Query("from Flower f where current_date + f.daysToExpire <= :searchDate")
    List<Flower> findExpiredBeforeDate(@Param("searchDate") Date searchDate);

    List<Flower> findAllByPriceBetweenAndNameContainingIgnoreCaseAndDaysToExpireLessThanEqual (
            Pageable pageable, Double minPrice, Double maxPrice, String name, Integer daysToExpire);
}