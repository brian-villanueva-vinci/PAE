package be.vinci.pae.utils.exceptions;

/**
 * Unauthorized access exception.
 */
public class UnauthorizedAccessException extends RuntimeException {

  /**
   * Unauthorized acces exception.
   */
  public UnauthorizedAccessException() {
    super();
  }

  /**
   * Unauthorized acces exception with a parameter.
   *
   * @param e message.
   */
  public UnauthorizedAccessException(String e) {
    super(e);
  }

}
