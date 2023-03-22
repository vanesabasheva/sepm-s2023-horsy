package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.mapper.OwnerMapper;
import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OwnerServiceImpl implements OwnerService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final OwnerDao dao;
  private final OwnerMapper mapper;
  private final OwnerValidator validator;

  public OwnerServiceImpl(
      OwnerDao dao,
      OwnerMapper mapper,
      OwnerValidator validator) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
  }

  @Override
  public OwnerDto getById(long id) throws NotFoundException, ServiceException {
    LOG.trace("getById({})", id);
    try {
      return mapper.entityToDto(dao.getById(id));
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }

  @Override
  public Map<Long, OwnerDto> getAllById(Collection<Long> ids) throws NotFoundException, ServiceException {
    LOG.trace("getAllById({})", ids);
    try {
      Map<Long, OwnerDto> owners =
          dao.getAllById(ids).stream()
              .map(mapper::entityToDto)
              .collect(Collectors.toUnmodifiableMap(OwnerDto::id, Function.identity()));
      for (final var id : ids) {
        if (!owners.containsKey(id)) {
          throw new NotFoundException("Owner with ID %d not found".formatted(id));
        }
      }
      return owners;
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }

  @Override
  public Stream<OwnerDto> search(OwnerSearchDto searchParameters) throws ServiceException {
    try {
      LOG.trace("search({})", searchParameters);
      return dao.search(searchParameters).stream()
          .map(mapper::entityToDto);
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }

  @Override
  public OwnerDto create(OwnerCreateDto newOwner) throws ValidationException, ServiceException {
    LOG.trace("create({})", newOwner);
    try {
      validator.validateForCreate(newOwner);
      return mapper.entityToDto(dao.create(newOwner));
    } catch (PersistenceException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }
}
