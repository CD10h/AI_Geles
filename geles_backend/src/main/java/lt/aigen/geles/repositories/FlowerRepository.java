package lt.aigen.geles.repositories;

import lt.aigen.geles.models.Flower;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowerRepository extends CrudRepository<Flower, Long> {
}