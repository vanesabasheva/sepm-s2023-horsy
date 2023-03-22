package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


  public void validateForUpdate(HorseDetailDto horse, List<Horse> horses) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }

    if (horse.name() == null || horse.name().isBlank()) {
      validationErrors.add("Horse name cannot be empty");
    } else {
      if (horse.name().length() > 255) {
        validationErrors.add("Horse name must be shorter than 255 characters");
      }
    }

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (horse.dateOfBirth() == null) {
      validationErrors.add("Horse date of birth cannot be empty");
    } else {
      if (horse.dateOfBirth().isAfter(LocalDate.now())) {
        validationErrors.add("Horse date of birth cannot be in the future");
      }
      if (horse.dateOfBirth().isBefore(LocalDate.of(1923, 1, 1))) {
        validationErrors.add("Horse cannot be older than 100 years");
      }
    }

    if (horse.sex() == null) {
      validationErrors.add("Sex cannot be empty");
    } else {
      for (Horse child : horses) {
        if ((Objects.equals(child.getMotherId(), horse.id()) && horse.sex() != Sex.FEMALE)
            || (Objects.equals(child.getFatherId(), horse.id()) && horse.sex() != Sex.MALE)) {
          conflictErrors.add("Horse is already a parent. Cannot change sex");
        }
      }
    }

    if (horse.mother() != null) {
      if (horse.mother().dateOfBirth().isAfter(horse.dateOfBirth()) || horse.mother().dateOfBirth().isEqual(horse.dateOfBirth())) {
        conflictErrors.add("Horse mother cannot be younger than the horse itself");
      }
      if (!horse.mother().sex().equals(Sex.FEMALE)) {
        conflictErrors.add("Mother has to be female");
      }
    }
    if (horse.father() != null) {
      if (horse.father().dateOfBirth().isAfter(horse.dateOfBirth()) || horse.father().dateOfBirth().isEqual(horse.dateOfBirth())) {
        conflictErrors.add("Horse father cannot be younger than the horse itself");
      }
      if (!horse.father().sex().equals(Sex.MALE)) {
        conflictErrors.add("Father has to be male");
      }
    }


    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Validation of horse for update failed", conflictErrors);
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  public void validateForCreate(HorseDetailDto newHorse) throws ValidationException, ConflictException {
    LOG.trace("validateForCreate({})", newHorse);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();
    if (newHorse.name() == null) {
      validationErrors.add("Horse name cannot be empty");
    } else {
      if (newHorse.name().length() > 255) {
        validationErrors.add("Horse name must be shorter than 255 characters");
      }
    }

    if (newHorse.sex() == null) {
      validationErrors.add("Horse sex cannot be empty");
    }
    if (newHorse.description() != null) {
      if (newHorse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (newHorse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (newHorse.dateOfBirth() == null) {
      validationErrors.add("Horse date of birth cannot be empty");
    } else {
      if (newHorse.dateOfBirth().isAfter(LocalDate.now())) {
        validationErrors.add("Horse date of birth cannot be in the future");
      } else if (newHorse.dateOfBirth().isBefore(LocalDate.of(1923, 1, 1))) {
        validationErrors.add("Horse cannot be older than 100 years");
      }

      if (newHorse.mother() != null) {
        if (newHorse.mother().dateOfBirth().isAfter(newHorse.dateOfBirth()) || newHorse.mother().dateOfBirth().isEqual(newHorse.dateOfBirth())) {
          conflictErrors.add("Horse mother cannot be younger than the horse itself");
        }
        if (!newHorse.mother().sex().equals(Sex.FEMALE)) {
          conflictErrors.add("Mother has to be female");
        }
      }

      if (newHorse.father() != null) {
        if (newHorse.father().dateOfBirth().isAfter(newHorse.dateOfBirth()) || newHorse.father().dateOfBirth().isEqual(newHorse.dateOfBirth())) {
          conflictErrors.add("Horse father cannot be younger than the horse itself");
        }
        if (!newHorse.father().sex().equals(Sex.MALE)) {
          conflictErrors.add("Father has to be male");
        }
      }
    }

    if (newHorse.sex() == null) {
      validationErrors.add("Sex cannot be empty");
    } else if (newHorse.sex() != Sex.FEMALE) {
      if (newHorse.sex() != Sex.MALE) {
        validationErrors.add("Horse sex should be either MALE or FEMALE");
      }
    }


    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Validation of horse for update failed", conflictErrors);
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse to create failed", validationErrors);
    }

  }
}
