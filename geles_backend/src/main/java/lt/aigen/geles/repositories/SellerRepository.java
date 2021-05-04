package lt.aigen.geles.repositories;

import lt.aigen.geles.models.Flower;
import lt.aigen.geles.models.Seller;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SellerRepository extends CrudRepository<Seller, Long> {
    @Query("from Seller s where lower(s.name) like concat('%', lower(:name), '%')")
    List<Seller> findAllByName(String name);

}