package com.ledzion.bicycleservice.repository;

import com.ledzion.bicycleservice.model.Bicycle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BicycleDAO {

    Optional<Bicycle> getBicycleById(long id);

    List<Bicycle> getAllBicycles();

<<<<<<< Updated upstream
    List<Bicycle> getBicyclesByType(String type, String size);

    boolean bookBicycle(String userId, String type, String size, LocalDate startDate, LocalDate endDate);
=======
    List<Bicycle> getBicyclesByTypeSize(String type, String size);

    List<Bicycle> getBicyclesByTypeSize2(List<String> type, List<String> size);
>>>>>>> Stashed changes
}
