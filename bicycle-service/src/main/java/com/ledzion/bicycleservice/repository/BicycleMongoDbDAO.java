package com.ledzion.bicycleservice.repository;

import com.ledzion.bicycleservice.model.Bicycle;

import java.util.List;
import java.util.Optional;

//@Repository
public class BicycleMongoDbDAO implements BicycleDAO {

    @Override
    public Optional<Bicycle> getBicycleById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Bicycle> getAllBicycles() {
        return null;
    }

    @Override
    public List<Bicycle> getBicyclesByType(String type, String size) {
        return null;
    }
}
