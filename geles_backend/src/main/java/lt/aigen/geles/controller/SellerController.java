package lt.aigen.geles.controller;

import lt.aigen.geles.models.Seller;
import lt.aigen.geles.models.dto.SellerDTO;
import lt.aigen.geles.repositories.SellerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sellers")
public class SellerController {
    SellerRepository sellerRepository;
    ModelMapper modelMapper;

    public SellerController(SellerRepository sellerRepository, ModelMapper modelMapper) {
        this.sellerRepository = sellerRepository;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/")  // /sellers/10
    public List<SellerDTO> getSellers(@RequestParam Optional<String> q) {
        return sellerRepository.
                findAllByName(q.orElse("")).
                stream().
                map(this::convertToDTO).
                collect(Collectors.toList());
    }

    @GetMapping("/{id}") // /sellers/10
    public ResponseEntity<SellerDTO> getSeller(@PathVariable Long id) {
        var seller = sellerRepository.findById(id);
        if (seller.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(convertToDTO(seller.get()));
        }
    }

    @PutMapping("/{id}")
    ResponseEntity<SellerDTO> updateSeller(@RequestBody @Validated SellerDTO sellerDTO, @PathVariable Long id) {
        if (sellerRepository.findById(id).equals(Optional.empty())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var newSeller = convertFromDTO(sellerDTO);
        newSeller.setId(id);
        return new ResponseEntity<>(convertToDTO(sellerRepository.save(newSeller)), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<SellerDTO> postSeller(@RequestBody @Validated SellerDTO sellerDTO) {
        var seller = convertFromDTO(sellerDTO);
        sellerRepository.save(seller);
        return ResponseEntity.ok(convertToDTO(seller));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeller(@PathVariable Long id)
    {
        if (!sellerRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        sellerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    private SellerDTO convertToDTO(Seller seller) {
        return modelMapper.map(seller, SellerDTO.class);
    }

    private Seller convertFromDTO(SellerDTO sellerDTO) {
        return modelMapper.map(sellerDTO, Seller.class);
    }

}
