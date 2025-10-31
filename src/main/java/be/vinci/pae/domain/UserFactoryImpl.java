package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.UserDTO;

/**
 * Implements UserFactory interface.
 */
public class UserFactoryImpl implements UserFactory {

  /**
   * Creates a UserDTO object.
   *
   * @return a new UserDTO object.
   */
  @Override
  public UserDTO getUserDTO() {
    return new UserImpl();
  }

}
