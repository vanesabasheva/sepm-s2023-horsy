package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseFamilyTreeDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;

import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {
  /**
   * Get all horses stored in the persistent data store.
   *
   * @return a list of all stored horses
   */
  List<Horse> getAll();


  /**
   * Update the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse update(HorseDetailDto horse) throws NotFoundException;

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * @param newHorse the horse to add
   * @return the newly added horse
   */
  Horse create(HorseDetailDto newHorse);

  /**
   * Deletes a horse from the persistent data store.
   *
   * @param id the id of the horse to delete
   */
  void delete(long id);

  /**
   * Get all horses stored in the persistent data store matching the given parameters
   *
   * @param requestParameters the parameters that the horses need to have
   * @return a list of all stored horses matching the given parameters
   */
  List<Horse> search(HorseSearchDto requestParameters);

  /**
   * Get all horses stored in the persistent data store that match the given parameters.
   *
   * @param parameters search parameters
   * @return a list of all stored horses matching the parameters
   */
  List<Horse> getAll(HorseSearchDto parameters);

  /**
   * @param parameters the horse to get its ancestors from with the number of generations to get
   * @return a list of horses, containing all related horses
   */
  List<Horse> getFamilyTree(HorseFamilyTreeDto parameters);
}
