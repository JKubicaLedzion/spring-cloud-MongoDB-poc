package com.ledzion.bicycleservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class BookingParameters {

    @NotBlank(message = "User Id should be provided.")
    private String userId;

    @NotBlank(message = "Bicycle Id should be provided.")
    private String bicycleId;

    @NotNull(message = "Start date should be provided.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End date should be provided.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
