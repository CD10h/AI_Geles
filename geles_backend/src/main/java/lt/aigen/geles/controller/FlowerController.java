package lt.aigen.geles.controller;

import lt.aigen.geles.models.dto.FlowerDTO;
import lt.aigen.geles.models.Flower;
import lt.aigen.geles.models.dto.FiltersDTO;
import lt.aigen.geles.models.dto.FlowerFilterDTO;
import lt.aigen.geles.repositories.FlowerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flowers")
public class FlowerController {
    FlowerRepository flowerRepository;
    ModelMapper modelMapper;

    public FlowerController(FlowerRepository flowerRepository, ModelMapper modelMapper) {
        this.flowerRepository = flowerRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/") // /flowers/?q=gele
    public List<FlowerDTO> getFlowers(@RequestParam Optional<String> q,
                                      @CookieValue(value = "user", required = false) String username) {
        return flowerRepository.findAllByNameContainsIgnoreCase(q.orElse(""))
            .stream()
            .map(flower -> convertToDTOWithFavorite(flower, username))
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}") // /flowers/10
    public ResponseEntity<FlowerDTO> getFlower(@PathVariable Long id) {
        var flower = flowerRepository.findById(id);
        if (flower.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(convertToDTO(flower.get()));
        }
    }

    @PutMapping("/{id}")
    ResponseEntity<FlowerDTO> updateFlower(@RequestBody @Validated FlowerDTO flowerDTO, @PathVariable Long id) {
        if (flowerRepository.findById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var newFlower = convertFromDTO(flowerDTO);
        newFlower.setId(id);
        return ResponseEntity.ok(convertToDTO(flowerRepository.save(newFlower)));
    }

    @PostMapping("/")
    public ResponseEntity<FlowerDTO> postFlower(@RequestBody @Validated FlowerDTO flowerDTO) {
        var flower = convertFromDTO(flowerDTO);
        flowerRepository.save(flower);
        return ResponseEntity.ok(convertToDTO(flower));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!flowerRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        flowerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private FlowerDTO convertToDTOWithFavorite(Flower flower, String username) {
        var flowerDto = modelMapper.map(flower, FlowerDTO.class);
        if (username == null) {
            flowerDto.setFavorite(false);
        } else {
            flowerDto.setFavorite(
                flower.getUserFavorites()
                    .stream()
                    .anyMatch(user -> user.getUsername().equals(username))
            );
        }
        return flowerDto;
    }

    private FlowerDTO convertToDTO(Flower flower) {
        return modelMapper.map(flower, FlowerDTO.class);
    }

    @PostMapping("/filter/")
    public ResponseEntity<List<Flower>> filterFlowers(
            @RequestParam String q,
            @RequestBody @Validated FlowerFilterDTO filters) throws ParseException {
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
        Integer daysToExpire = Integer.MAX_VALUE;

        if (!filters.getFilters().isEmpty()) {
            for (FiltersDTO filter : filters.getFilters()) {
                if (filter.getName().equals("minPrice")){
                    minPrice = Double.parseDouble(filter.getValue());
                }
                if (filter.getName().equals("maxPrice")){
                    maxPrice = Double.parseDouble(filter.getValue());
                }
                if (filter.getName().equals("minDate")){
                    daysToExpire = Period.between(LocalDate.now(), LocalDate.parse(filter.getValue())).getDays();
                }
            }
        }
        return new ResponseEntity<>(
                flowerRepository.findAllByPriceBetweenAndNameContainingIgnoreCaseAndDaysToExpireLessThanEqual(
                paging, minPrice, maxPrice, q, daysToExpire), HttpStatus.OK);
    }

    private Flower convertFromDTO(FlowerDTO flowerDTO) {
        return modelMapper.map(flowerDTO, Flower.class);
    }

}
