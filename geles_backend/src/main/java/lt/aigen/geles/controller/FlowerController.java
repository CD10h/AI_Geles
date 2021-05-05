package lt.aigen.geles.controller;

import lt.aigen.geles.models.Flower;
import lt.aigen.geles.models.dto.FiltersDTO;
import lt.aigen.geles.models.dto.FlowerFilterDTO;
import lt.aigen.geles.repositories.FlowerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/flowers")
public class FlowerController {
    FlowerRepository flowerRepository;

    public FlowerController(FlowerRepository flowerRepository) {
        this.flowerRepository = flowerRepository;
    }

    @GetMapping("/") // /flowers/?q=gele
    public List<Flower> getFlowers(@RequestParam Optional<String> q) {
        return flowerRepository.findAllByName(q.orElse(""));
    }

    @GetMapping("/{id}") // /flowers/10
    public ResponseEntity<Flower> getFlower(@PathVariable Long id) {
        return flowerRepository.findById(id).map(ResponseEntity::ok).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    ResponseEntity<Flower> updateFlower(@RequestBody @Validated Flower newFlower, @PathVariable Long id) {
        if (flowerRepository.findById(id).equals(Optional.empty())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        newFlower.setId(id);
        return new ResponseEntity<>(flowerRepository.save(newFlower), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Flower> postFlower(@RequestBody @Valid Flower flower)
    {
        var createdFlower = flowerRepository.save(flower);
        return ResponseEntity.ok(createdFlower);
    }

    /*
    {
    "sort": "price",
    "sortType": "asc",
    "filters": [
        {
            "name": "minPrice",
            "value": "0"
        },
        {
            "name": "maxPrice",
            "value": "15"
        }
    ]
    }*/
    @PostMapping("/filter/")
    public ResponseEntity<List<Flower>> filterFlowers(@RequestParam String q, @RequestBody @Validated FlowerFilterDTO filters)
    {
        String sort = filters.getSort();
        String sortType = filters.getSortType();
        Pageable paging = PageRequest.of(0, Integer.MAX_VALUE);;

        if (sortType.equals("asc")){
            paging = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(sort).ascending());
        } else if (sortType.equals("desc")) {
            paging = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(sort).descending());
        }

        Double minPrice = 0.0;
        Double maxPrice = Double.MAX_VALUE;

        if (!filters.getFilters().isEmpty()) {
            for (FiltersDTO filter : filters.getFilters()) {
                if (filter.getName().equals("minPrice")){
                    minPrice = Double.parseDouble(filter.getValue());
                }
                if (filter.getName().equals("maxPrice")){
                    maxPrice = Double.parseDouble(filter.getValue());
                }
            }
        }
        return new ResponseEntity<>(flowerRepository.findAllByPriceBetweenAndNameContainingIgnoreCase(paging, minPrice, maxPrice, q), HttpStatus.OK);
    }
}
