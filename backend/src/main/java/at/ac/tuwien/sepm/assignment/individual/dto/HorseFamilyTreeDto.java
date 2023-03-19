package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO to bundle the query parameters used in searching horses' family tree.
 */
public record HorseFamilyTreeDto(
    Long id,
    String name,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate bornBefore,
    Sex sex,
    Long motherId,
    Long fatherId,
    Long generations
) {
}
