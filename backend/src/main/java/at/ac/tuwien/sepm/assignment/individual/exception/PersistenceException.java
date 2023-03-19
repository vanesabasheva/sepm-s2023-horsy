package at.ac.tuwien.sepm.assignment.individual.exception;

/**
 * Exception that is thrown in the persistence layer.
 */
public class PersistenceException extends Exception {
  public PersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
}
