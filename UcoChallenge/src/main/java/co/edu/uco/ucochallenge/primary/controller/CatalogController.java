package co.edu.uco.ucochallenge.primary.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.uco.ucochallenge.secondary.ports.repository.*;

@RestController
@RequestMapping("/uco-challenge/api/v1/catalog")
public class CatalogController {

    private final CountryRepository countryRepo;
    private final StateRepository stateRepo;
    private final CityRepository cityRepo;
    private final IdTypeRepository idTypeRepo;

    public CatalogController(
            CountryRepository countryRepo,
            StateRepository stateRepo,
            CityRepository cityRepo,
            IdTypeRepository idTypeRepo) {
        this.countryRepo = countryRepo;
        this.stateRepo = stateRepo;
        this.cityRepo = cityRepo;
        this.idTypeRepo = idTypeRepo;
    }

    @GetMapping("/countries")
    public ResponseEntity<List<Map<String, Object>>> getCountries() {
        List<Map<String, Object>> countries = countryRepo.findAll().stream()
                .map(country -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", country.getId());
                    map.put("name", country.getName());
                    return map;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/states")
    public ResponseEntity<?> getStates(@RequestParam(required = true) UUID countryId) {
        if (countryId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El parámetro countryId es requerido"));
        }
        
        // Validar que el país existe
        if (!countryRepo.existsById(countryId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El país especificado no existe"));
        }
        
        List<Map<String, Object>> states = stateRepo.findByCountryId(countryId).stream()
                .map(state -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", state.getId());
                    map.put("name", state.getName());
                    return map;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(states);
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getCities(@RequestParam(required = true) UUID stateId) {
        if (stateId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El parámetro stateId es requerido"));
        }
        
        // Validar que el departamento existe
        if (!stateRepo.existsById(stateId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El departamento especificado no existe"));
        }
        
        List<Map<String, Object>> cities = cityRepo.findByState_Id(stateId).stream()
                .map(city -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", city.getId());
                    map.put("name", city.getName());
                    return map;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/id-types")
    public ResponseEntity<List<Map<String, Object>>> getIdTypes() {
        List<Map<String, Object>> idTypes = idTypeRepo.findAll().stream()
                .map(idType -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", idType.getId());
                    map.put("name", idType.getName());
                    return map;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(idTypes);
    }
}


