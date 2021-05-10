package lt.aigen.geles.controller;

import lt.aigen.geles.models.dto.FavoriteDTO;
import lt.aigen.geles.models.dto.FlowerDTO;
import lt.aigen.geles.models.Flower;
import lt.aigen.geles.models.dto.FiltersDTO;
import lt.aigen.geles.models.dto.FlowerFilterDTO;
import lt.aigen.geles.repositories.FlowerRepository;
import lt.aigen.geles.repositories.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flowers")
public class FlowerController {
    FlowerRepository flowerRepository;
    UserRepository userRepository;
    ModelMapper modelMapper;

    public FlowerController(FlowerRepository flowerRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.flowerRepository = flowerRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/") // /flowers/?q=gele
    public ResponseEntity<List<FlowerDTO>> getFlowers(@RequestParam Optional<String> q,
                                                      @RequestParam Optional<String> favorite,
                                                      @CookieValue("user") Optional<String> username) {
        if (favorite.isPresent() && favorite.get().equals("true")) {
            if (username.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            System.out.println(flowerRepository.findAllFavoriteFlowers(username.get()));
            return new ResponseEntity<>(flowerRepository.findAllFavoriteFlowers(username.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(flowerRepository.findAllByNameContainsIgnoreCase(q.orElse(""))
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/favorite")
    public ResponseEntity<List<Long>> getFavoriteFlowers(@PathVariable Optional<String> q,
                                                         @CookieValue("user") Optional<String> username) {
        if (username.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(flowerRepository.findAllFavoriteFlowerIds(username.get()), HttpStatus.OK);
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

    @PostMapping("/")
    public ResponseEntity<FlowerDTO> createFlower(@RequestBody @Validated FlowerDTO flowerDTO) {
        var flower = convertFromDTO(flowerDTO);
        flowerRepository.save(flower);
        return ResponseEntity.ok(convertToDTO(flower));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFlower(@PathVariable Long id) {
        if (!flowerRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        flowerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<FlowerDTO> setFlowerAsFavorite(
        @PathVariable Long id,
        @CookieValue(name="user") String username,
        @RequestBody FavoriteDTO favorite
    ) {
        var flowerOptional = flowerRepository.findById(id);
        if (flowerOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var flower = flowerOptional.get();

        var userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        var user = userOptional.get();

        if (favorite.isFavorite()) {
            flower.getUserFavorites().add(user);
        } else {
            flower.getUserFavorites().remove(user);
        }

        flowerRepository.save(flower);
        return new ResponseEntity<>(convertToDTO(flower), HttpStatus.OK);
    }

    @PostMapping("/filter/")
    public ResponseEntity<List<FlowerDTO>> filterFlowers(
            @RequestParam String q,
            @RequestBody @Validated FlowerFilterDTO filters) {
        String sort = filters.getSort();
        String sortType = filters.getSortType();
        Pageable paging = PageRequest.of(0, Integer.MAX_VALUE);

        if (sortType.equals("asc")){
            paging = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(sort).ascending());
        } else if (sortType.equals("desc")) {
            paging = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(sort).descending());
        }

        double minPrice = 0.0;
        double maxPrice = Double.MAX_VALUE;
        int daysToExpire = 0;

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
                flowerRepository.findAllByPriceBetweenAndNameContainingIgnoreCaseAndDaysToExpireGreaterThanEqual(
                    paging, minPrice, maxPrice, q, daysToExpire
                ).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()), HttpStatus.OK);
    }

    private FlowerDTO convertToDTO(Flower flower) {
        return modelMapper.map(flower, FlowerDTO.class);
    }

    private Flower convertFromDTO(FlowerDTO flowerDTO) {
        return modelMapper.map(flowerDTO, Flower.class);
    }

}
