package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseFamilyTreeDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseDaoTest {

  @Autowired
  HorseDao horseDao;

  @Test
  @DisplayName("getAll for horses returns all stored horses")
  public void getAllReturnsAllStoredHorses() throws PersistenceException {
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(10);
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName)
        .contains(tuple(-1L, "Wendy"));
  }

  @Test
  @DisplayName("getAll({}) of horses returns all horses matching the parameters")
  public void getAllWithParametersReturnsRelevantHorses() throws PersistenceException {
    HorseSearchDto horseSearchDto = new HorseSearchDto("Gwe", "", null, Sex.FEMALE, "", 5);
    List<Horse> horses = horseDao.getAll(horseSearchDto);
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName)
        .contains(tuple(-7L, "Gwendy"));
  }

  @Test
  @DisplayName("getById(0) for horse returns a NotFoundException")
  public void getByIdWithZeroReturnsNotFoundException() {
    assertThrows(NotFoundException.class,
        () -> horseDao.getById(0L));
  }

  @Test
  @DisplayName("Delete on a existing horse deletes the horse permanently from the persistent data storage")
  @Transactional
  @Rollback
  public void deleteExistingHorseRemovesTheHorsePermanently() throws PersistenceException {
    long id = -2;
    boolean exists = false;
    boolean deletedExists = false;
    for (Horse horse : horseDao.getAll()) {
      if (horse.getId() == id) {
        exists = true;
        break;
      }
    }
    horseDao.delete(id);
    for (Horse horse : horseDao.getAll()) {
      if (horse.getId() == id) {
        deletedExists = true;
        break;
      }
    }
    assertNotEquals(exists, deletedExists, "Successful delete of horse");
  }

  @Test
  @DisplayName("getFamilyTree of horse with no mother and father returns the horse itself")
  public void getFamilyTreeOfHorseReturnsAllRelatedHorses() throws PersistenceException {
    List<Horse> horses = horseDao.getFamilyTree(
        new HorseFamilyTreeDto(-2L, "Candy", LocalDate.of(2020, 10, 10), Sex.MALE, null, null, 2L));
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName)
        .contains(tuple(-2L, "Candy"));
  }


}
