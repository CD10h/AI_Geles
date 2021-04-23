package lt.aigen.geles.repositories;

import lt.aigen.geles.models.Flower;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowerRepository extends CrudRepository<Flower, Long> {
    @Query("from Flower f where lower(f.name) like concat('%', lower(:name), '%')")
    List<Flower> findAllByName(String name);
}