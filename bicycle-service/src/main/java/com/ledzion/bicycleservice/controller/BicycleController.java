package com.ledzion.bicycleservice.controller;

import com.ledzion.bicycleservice.model.Bicycle;
import com.ledzion.bicycleservice.service.BicycleService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/bicycles")
public class BicycleController {

    private static final String SERVICE_UNAVAILABLE_ERROR_MESSAGE =
            "No Response From Bicycle Service at this moment. " + " Service will be back shortly.";
    private final Logger LOGGER = LoggerFactory.getLogger(BicycleController.class);

    private static final String BICYCLE_NOT_FOUND = "Bicycle not found.";

    private BicycleService bicycleService;

    @Autowired
    public BicycleController(BicycleService bicycleService) {
        this.bicycleService = bicycleService;
    }

    @HystrixCommand(fallbackMethod = "getBicycleByIdFallback")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getBicycleById(@PathVariable("id") long id) {
        LOGGER.debug("Getting bicycles with id {}.", id);
        Optional<Bicycle> bicycle = bicycleService.getBicycleById(id);
        return bicycle.isPresent()
                ? ResponseEntity.status(HttpStatus.OK).body(bicycle.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(BICYCLE_NOT_FOUND);
    }

    @HystrixCommand(fallbackMethod = "getAllBicyclesFallback")
    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getAllBicycles() {
        LOGGER.debug("Getting all bicycles.");
        List<Bicycle> bicycles = bicycleService.getAllBicycles();
        return bicycles.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(BICYCLE_NOT_FOUND)
                : ResponseEntity.status(HttpStatus.OK).body(bicycles);
    }

    @HystrixCommand(fallbackMethod = "getBicyclesByTypeFallback")
    @GetMapping(value = "filter", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getBicyclesByType(@RequestParam(name = "type") String type,
                                            @RequestParam(name = "size", required = false) String size) {
        LOGGER.debug("Getting bicycles of type {} and size {}.", type, size);
        List<Bicycle> bicycles = bicycleService.getBicyclesByType(type, size);
        return bicycles.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(BICYCLE_NOT_FOUND)
                : ResponseEntity.status(HttpStatus.OK).body(bicycles);
    }

    @SuppressWarnings("unused")
    public ResponseEntity getBicycleByIdFallback(@PathVariable("id") long id) {
        return ResponseEntity.ok().body( SERVICE_UNAVAILABLE_ERROR_MESSAGE );
    }

    @SuppressWarnings("unused")
    public ResponseEntity getAllBicyclesFallback() {
        return ResponseEntity.ok().body( SERVICE_UNAVAILABLE_ERROR_MESSAGE );
    }

    @SuppressWarnings("unused")
    public ResponseEntity getBicyclesByTypeFallback(@RequestParam(name = "type") String type,
            @RequestParam(name = "size", required = false) String size) {
        return ResponseEntity.ok().body( SERVICE_UNAVAILABLE_ERROR_MESSAGE );
    }
}
