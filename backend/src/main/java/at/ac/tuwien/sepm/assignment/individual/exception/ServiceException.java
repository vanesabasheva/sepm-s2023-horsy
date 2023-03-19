package at.ac.tuwien.sepm.assignment.individual.exception;

/**
 * Exception that is thrown in the service layer.
 */
public class ServiceException extends Exception {
  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
