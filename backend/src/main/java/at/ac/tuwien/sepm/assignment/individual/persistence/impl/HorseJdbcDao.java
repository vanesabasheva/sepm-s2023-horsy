package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , description = ?"
      + "  , date_of_birth = ?"
      + "  , sex = ?"
      + "  , owner_id = ?"
      + "  , mother_id = ?"
      + "  , father_id = ?"
      + " WHERE id = ?";
  private static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME
      + " (name, description, date_of_birth, sex, owner_id, mother_id, father_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_SELECT_SEARCH_PARENTS = "SELECT * FROM " + TABLE_NAME
      + " WHERE UPPER(name) like UPPER('%'||COALESCE(?, '')||'%') AND sex = ? LIMIT ?";

  private final JdbcTemplate jdbcTemplate;

  public HorseJdbcDao(
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcTemplate.query(SQL_SELECT_ALL, this::mapRow);
  }

  @Override
  public List<Horse> search(HorseSearchDto requestParameters) {
    LOG.trace("search({})", requestParameters);
    var query = SQL_SELECT_SEARCH_PARENTS;
    var params = new ArrayList<>();
    params.add(requestParameters.name());
    params.add(requestParameters.sex().toString());
    params.add(requestParameters.limit());
    return jdbcTemplate.query(query, this::mapRow, params.toArray());
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.get(0);
  }


  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    int updated = jdbcTemplate.update(connection -> {
      PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE,
          Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, horse.name());
      stmt.setString(2, horse.description());
      stmt.setDate(3, Date.valueOf(horse.dateOfBirth()));
      stmt.setString(4, horse.sex().toString());
      if (horse.ownerId() != null) {
        stmt.setLong(5, horse.ownerId());
      } else {
        stmt.setNull(5, java.sql.Types.NULL);
      }
      if (horse.mother() != null) {
        stmt.setLong(6, horse.motherId());
      } else {
        stmt.setNull(6, java.sql.Types.NULL);
      }
      if (horse.father() != null) {
        stmt.setLong(7, horse.fatherId());
      } else {
        stmt.setNull(7, java.sql.Types.NULL);
      }
      stmt.setLong(8, horse.id());
      return stmt;
    }, keyHolder);

    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }
    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setDescription(horse.description())
        .setDateOfBirth(horse.dateOfBirth())
        .setSex(horse.sex())
        .setOwnerId(horse.ownerId())
        .setMotherId(horse.motherId())
        .setFatherId(horse.fatherId());
  }


  @Override
  public Horse create(HorseDetailDto newHorse) {
    LOG.trace("create({}), persistence", newHorse);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement stmt = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, newHorse.name());
      stmt.setString(2, newHorse.description());
      stmt.setDate(3, Date.valueOf(newHorse.dateOfBirth()));
      stmt.setString(4, newHorse.sex().toString());
      if (newHorse.ownerId() != null) {
        stmt.setLong(5, newHorse.ownerId());
      } else {
        stmt.setNull(5, java.sql.Types.NULL);
      }
      if (newHorse.motherId() != null) {
        stmt.setLong(6, newHorse.motherId());
      } else {
        stmt.setNull(6, java.sql.Types.NULL);
      }
      if (newHorse.fatherId() != null) {
        stmt.setLong(7, newHorse.fatherId());
      } else {
        stmt.setNull(7, java.sql.Types.NULL);
      }
      return stmt;
    }, keyHolder);

    return new Horse()
        .setName(newHorse.name())
        .setDescription(newHorse.description())
        .setDateOfBirth(newHorse.dateOfBirth())
        .setSex(newHorse.sex())
        .setOwnerId(newHorse.ownerId())
        .setMotherId(newHorse.motherId())
        .setFatherId(newHorse.fatherId())
        ;
  }

  @Override
  public void delete(long id) {
    LOG.trace("delete({}), persistence", id);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement stmt = connection.prepareStatement(SQL_DELETE, Statement.RETURN_GENERATED_KEYS);
      stmt.setLong(1, id);
      return stmt;
    }, keyHolder);
  }


  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setDescription(result.getString("description"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setSex(Sex.valueOf(result.getString("sex")))
        .setOwnerId(result.getObject("owner_id", Long.class))
        ;
  }
}
