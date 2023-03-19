package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseFamilyTreeDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final OwnerService ownerService;

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.ownerService = ownerService;
  }

  @Override
  public Stream<HorseListDto> allHorses() throws ServiceException {
    LOG.trace("allHorses()");
    try {
      var horses = dao.getAll();
      var ownerIds = horses.stream()
          .map(Horse::getOwnerId)
          .filter(Objects::nonNull)
          .collect(Collectors.toUnmodifiableSet());
      Map<Long, OwnerDto> ownerMap;
      try {
        ownerMap = ownerService.getAllById(ownerIds);
      } catch (NotFoundException e) {
        throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
      }
      return horses.stream()
          .map(horse -> mapper.entityToListDto(horse, ownerMap));
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }

  public Stream<HorseListDto> allHorses(HorseSearchDto requestParameters) throws ServiceException {
    LOG.trace("allHorses({}}), service", requestParameters);
    try {
      Collection<Horse> horses;
      if (requestParameters.ownerName() == null) {
        horses = dao.search(requestParameters);
      } else {
        horses = dao.getAll(requestParameters);
      }
      var ownerIds = horses.stream()
          .map(Horse::getOwnerId)
          .filter(Objects::nonNull)
          .collect(Collectors.toUnmodifiableSet());
      Map<Long, OwnerDto> ownerMap;
      try {
        ownerMap = ownerService.getAllById(ownerIds);
      } catch (NotFoundException e) {
        throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
      }
      return horses.stream()
          .map(horse -> mapper.entityToListDto(horse, ownerMap));
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }

  public HorseDetailDto[] getParents(Horse child) throws NotFoundException, ServiceException {
    try {
      HorseDetailDto[] parents = new HorseDetailDto[2];
      Horse mother;
      Horse father;
      HorseDetailDto motherDTO;
      HorseDetailDto fatherDTO;

      if (child.getMotherId() != null) {
        mother = dao.getById(child.getMotherId());
        motherDTO = mapper.entityToDetailDto(mother, ownerMapForSingleId(mother.getOwnerId()), null, null);
      } else {
        motherDTO = null;
      }
      if (child.getFatherId() != null) {
        father = dao.getById(child.getFatherId());
        fatherDTO = mapper.entityToDetailDto(father, ownerMapForSingleId(father.getOwnerId()), null, null);
      } else {
        fatherDTO = null;
      }

      parents[0] = motherDTO;
      parents[1] = fatherDTO;
      return parents;
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws ServiceException, NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);
    try {
      validator.validateForUpdate(horse);
      var updatedHorse = dao.update(horse);
      var parents = getParents(updatedHorse);
      return mapper.entityToDetailDto(
          updatedHorse,
          ownerMapForSingleId(updatedHorse.getOwnerId()),
          parents[0],
          parents[1]);
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }


  @Override
  public HorseDetailDto create(HorseDetailDto newHorse) throws ServiceException, NotFoundException, ValidationException, ConflictException {
    LOG.trace("create({}), service", newHorse);
    try {
      validator.validateForCreate(newHorse);
      var createdHorse = dao.create(newHorse);
      var parents = getParents(createdHorse);
      return mapper.entityToDetailDto(
          createdHorse,
          ownerMapForSingleId(createdHorse.getOwnerId()),
          parents[0],
          parents[1]);
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }


  @Override
  public HorseDetailDto getById(long id) throws NotFoundException, ServiceException {
    LOG.trace("details({})", id);
    try {
      Horse horse = dao.getById(id);
      var parents = getParents(horse);
      return mapper.entityToDetailDto(
          horse,
          ownerMapForSingleId(horse.getOwnerId()),
          parents[0],
          parents[1]);
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }

  @Override
  public void delete(long id) throws NotFoundException, ServiceException, ValidationException, ConflictException {
    LOG.trace("delete({}), service", id);
    try {
      if (dao.getById(id) == null) {
        throw new NotFoundException("Horse with id " + id + " was not found");
      }
      dao.delete(id);
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }

  @Override
  public Stream<HorseFamilyTreeDto> getFamilyTree(HorseFamilyTreeDto parameters) {
    List<Horse> familyTree = dao.getFamilyTree(parameters);
    return familyTree.stream()
        .map(horse -> mapper.entityToFamilyTreeDto(horse, parameters.generations()));
  }

  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) throws ServiceException {
    try {
      return ownerId == null
          ? null
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
    }
  }

}
