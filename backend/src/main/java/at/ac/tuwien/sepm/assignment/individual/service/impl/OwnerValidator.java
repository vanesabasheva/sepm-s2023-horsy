package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OwnerValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final Pattern EMAIL_REGEX = Pattern.compile(
      "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
  );

  public void validateForCreate(OwnerCreateDto newOwner, Collection<Owner> allOwners) throws ValidationException {
    LOG.trace("validateForCreate({}), OwnerValidator", newOwner);
    List<String> validationErrors = new ArrayList<>();
    if (newOwner.firstName() == null) {
      validationErrors.add("Owner first name cannot be empty");
    } else {
      if (newOwner.firstName().length() > 100) {
        validationErrors.add("Owner first name must be shorter than 100 characters");
      }
    }

    if (newOwner.lastName() == null) {
      validationErrors.add("Owner last name cannot be empty");
    } else {
      if (newOwner.lastName().length() > 100) {
        validationErrors.add("Owner last name must be shorter than 100 characters");
      }
    }

    if (newOwner.email() != null) {
      if (newOwner.email().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (newOwner.email().length() > 255) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
      Matcher matcher = EMAIL_REGEX.matcher(newOwner.email());
      if (!matcher.matches()) {
        validationErrors.add("Owner email is invalid. Please provide a valid email");
      } else {
        for (Owner owner : allOwners) {
          if (owner.getEmail() != null && newOwner.email().equals(owner.getEmail())) {
            validationErrors.add("Email is already used. Please provide another email");
          }
        }
      }
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of owner to create failed", validationErrors);
    }
  }
}
