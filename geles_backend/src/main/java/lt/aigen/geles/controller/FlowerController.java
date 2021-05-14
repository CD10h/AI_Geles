package lt.aigen.geles.controller;

import lt.aigen.geles.annotations.Authorized;
import lt.aigen.geles.components.CurrentUser;
import lt.aigen.geles.models.User;
import lt.aigen.geles.models.dto.*;
import lt.aigen.geles.models.Flower;
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
    ModelMapper modelMapper;
    CurrentUser currentUser;

    public FlowerController(FlowerRepository flowerRepository, ModelMapper modelMapper, CurrentUser currentUser) {
        this.flowerRepository = flowerRepository;
        this.modelMapper = modelMapper;
        this.currentUser = currentUser;
    }

    @Authorized(optional = true)
    @GetMapping("/") // /flowers/?q=gele
    public ResponseEntity<List<FlowerDTO>> getFlowers(@RequestParam Optional<String> q,
                                                      @RequestParam Optional<String> favorite) {
        if (favorite.isPresent() && favorite.get().equals("true")) {
            var user = currentUser.get();
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(flowerRepository.findAllFavoriteFlowers(user.getUsername()), HttpStatus.OK);
        }
        return new ResponseEntity<>(flowerRepository.findAllByNameContainsIgnoreCase(q.orElse(""))
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()), HttpStatus.OK);
    }

    @Authorized
    @GetMapping("/favorite")
    public ResponseEntity<List<Long>> getFavoriteFlowers(@PathVariable Optional<String> q) {
        return new ResponseEntity<>(flowerRepository.findAllFavoriteFlowerIds(currentUser.get().getUsername()), HttpStatus.OK);
    }

    @Authorized
    @GetMapping("/favorite/all")
    public ResponseEntity<List<FlowerFavoriteAmountDTO>> getAllFavoriteFlowers() {
        return new ResponseEntity<>(flowerRepository.findAllUsersFavoriteFlowers(),HttpStatus.OK);
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

    @Authorized(admin = true)
    @PostMapping("/")
    public ResponseEntity<FlowerDTO> createFlower(@RequestBody @Validated FlowerDTO flowerDTO) {
        var flower = convertFromDTO(flowerDTO);
        flowerRepository.save(flower);
        return ResponseEntity.ok(convertToDTO(flower));
    }

    @Authorized(admin = true)
    @PutMapping("/{id}")
    ResponseEntity<FlowerDTO> updateFlower(@RequestBody @Validated FlowerDTO flowerDTO, @PathVariable Long id) {
        if (flowerRepository.findById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var newFlower = convertFromDTO(flowerDTO);
        newFlower.setId(id);
        return ResponseEntity.ok(convertToDTO(flowerRepository.save(newFlower)));
    }

    @Authorized(admin = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFlower(@PathVariable Long id) {
        if (!flowerRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        flowerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Authorized
    @PutMapping("/{id}/favorite")
    public ResponseEntity<FlowerDTO> setFlowerAsFavorite(
            @PathVariable Long id,
            @RequestBody FavoriteDTO favorite
    ) {
        var user = currentUser.get();
        var flowerOptional = flowerRepository.findById(id);
        if (flowerOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var flower = flowerOptional.get();

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

    private FlowerFavoriteAmountDTO convertToFav(Flower flower) {
        return modelMapper.map(flower, FlowerFavoriteAmountDTO.class);
    }

    private FlowerDTO convertToDTO(Flower flower) {
        return modelMapper.map(flower, FlowerDTO.class);
    }

    private Flower convertFromDTO(FlowerDTO flowerDTO) {
        return modelMapper.map(flowerDTO, Flower.class);
    }
}
