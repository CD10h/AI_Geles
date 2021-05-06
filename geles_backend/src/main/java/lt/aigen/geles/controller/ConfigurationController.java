package lt.aigen.geles.controller;

import lt.aigen.geles.models.Configuration;
import lt.aigen.geles.models.dto.ConfigurationDTO;
import lt.aigen.geles.repositories.ConfigurationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/config")
public class ConfigurationController {
    private final ConfigurationRepository configRepository;
    private final ModelMapper modelMapper;

    public ConfigurationController(ConfigurationRepository configRepository, ModelMapper modelMapper) {
        this.configRepository = configRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/") // /config/?q=key
    public List<ConfigurationDTO> getConfiguration(@RequestParam Optional<String> q) {
        return configRepository.findConfigsByKey(q.orElse(""))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ConfigurationDTO convertToDTO(Configuration config) {
        return modelMapper.map(config, ConfigurationDTO.class);
    }

    private Configuration convertFromDTO(ConfigurationDTO configDTO) {
        return modelMapper.map(configDTO, Configuration.class);
    }
}
