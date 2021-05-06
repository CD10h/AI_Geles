package lt.aigen.geles.repositories;

import lt.aigen.geles.models.Configuration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {

    @Query("from Configuration c where lower(c.key) like concat(lower(:key_param),'%')")
    public List<Configuration> findConfigsByKey(@Param("key_param") String Key);
}
