package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseFamilyTreeDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";

  private final HorseService service;

  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    if (searchParameters.name() == null) {
      return service.allHorses();
    } else {
      return service.allHorses(searchParameters);
    }

  }

  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public HorseDetailDto getById(@PathVariable long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }


  @PutMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public HorseDetailDto update(@PathVariable long id, @RequestBody HorseDetailDto toUpdate) throws ValidationException, ConflictException {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      return service.update(toUpdate.withId(id));
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }


  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public HorseDetailDto create(@RequestBody HorseDetailDto toAdd) throws ValidationException, ConflictException {
    LOG.info("PUT " + BASE_PATH + "/{}", toAdd);
    try {
      return service.create(toAdd);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, e.getMessage(), e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    try {
      service.delete(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, e.getMessage(), e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @GetMapping("{id}/familytree")
  @ResponseStatus(HttpStatus.OK)
  public Stream<HorseFamilyTreeDto> getFamilyTree(HorseFamilyTreeDto fromHorse) throws ValidationException {
    LOG.info("GET FAMILY TREE " + BASE_PATH + "/{}", fromHorse);
    try {
      return service.getFamilyTree(fromHorse);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, e.getMessage(), e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
