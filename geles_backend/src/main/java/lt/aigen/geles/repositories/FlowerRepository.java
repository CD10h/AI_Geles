package lt.aigen.geles.repositories;

import lt.aigen.geles.models.Flower;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowerRepository extends CrudRepository<Flower, Long> {
    @Query("select fow from Flower fow where fow.name like %:name%")
    List<Flower> findAllByName(@Param("name") String name);
}