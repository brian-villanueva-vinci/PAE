package be.vinci.pae.utils.exceptions;

/**
 * Not found exception class.
 */
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Resource not found exception.
   */
  public ResourceNotFoundException() {
    super();
  }

  /**
   * Resource not found exception with a parameter.
   *
   * @param e message
   */
  public ResourceNotFoundException(String e) {
    super(e);
  }
}
