package be.vinci.pae.utils.exceptions;

/**
 * Bad request exception class.
 */
public class InvalidRequestException extends RuntimeException {

  /**
   * Invalid request exception.
   */
  public InvalidRequestException() {
    super();
  }

  /**
   * Invalid request exception with a parameter.
   *
   * @param e message
   */
  public InvalidRequestException(String e) {
    super(e);
  }
}
