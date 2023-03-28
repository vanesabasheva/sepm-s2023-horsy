package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.persistence.DataGeneratorBean;
import at.ac.tuwien.sepm.assignment.individual.service.impl.HorseValidator;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseServiceTest {

  @Autowired
  HorseService horseService;

  @Autowired
  DataGeneratorBean dataGeneratorBean;

  @BeforeEach
  void setupData() {
    try {
      dataGeneratorBean.createSchema();
      dataGeneratorBean.generateData();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @AfterEach
  void cleanupData() {
    try {
      dataGeneratorBean.deleteSchema();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  @Test
  @DisplayName("getAll for horses returns all stored horses")
  public void getAllReturnsAllStoredHorses() {
    List<HorseListDto> horses = horseService.allHorses()
        .toList();
    assertThat(horses.size()).isGreaterThanOrEqualTo(10);
    assertThat(horses)
        .map(HorseListDto::id, HorseListDto::sex)
        .contains(tuple(-1L, Sex.FEMALE));
  }

  @Test
  @DisplayName("Delete on a non - existing horse throws NotFoundException")
  public void deleteNotExistingHorseThrowsNotFoundException() {
    assertThrows(NotFoundException.class,
        () -> horseService.delete(42L));
  }

  @Test
  @DisplayName("Create a new horse with a younger mother throws ConflictException")
  public void createWithYoungerMotherThrowsConflictException() {
    HorseDetailDto mother = new HorseDetailDto(1L, "Mother", "", LocalDate.now(), Sex.FEMALE, null, null, null);
    assertThrows(ConflictException.class,
        () -> horseService.create(new HorseDetailDto(2L, "Child", "", LocalDate.of(1999, 2, 2), Sex.MALE, null, mother, null)));
  }

  @Test
  @DisplayName("ValidatorForCreate throws no exceptions if horse is valid")
  public void validationOfCorrectParametersThrowsNoExceptions() {
    HorseValidator validator = new HorseValidator();
    HorseDetailDto horse = new HorseDetailDto(null, "Gwendy", "The guinea pig", LocalDate.now(), Sex.FEMALE, null, null, null);
    assertAll(() -> validator.validateForCreate(horse));
  }

  @Test
  @DisplayName("Creating a horse with valid data throws no exception")
  public void createValidHorse() throws ValidationException, ConflictException, NotFoundException {
    HorseDetailDto horse = new HorseDetailDto(null, "TestCreate", "description", LocalDate.now(), Sex.MALE, null, null, null);
    horseService.create(horse);
    List<HorseListDto> horses = horseService.allHorses().toList();
    assertThat(horses.size()).isGreaterThanOrEqualTo(10);
    assertThat(horses)
        .map(HorseListDto::name, HorseListDto::sex)
        .contains(tuple("TestCreate", Sex.MALE));
  }


}
