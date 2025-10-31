package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.UserDTO;

/**
 * UserFactory Interface.
 */
public interface UserFactory {

  /**
   * Creates a UserDTO object.
   *
   * @return a new UserDTO object.
   */
  UserDTO getUserDTO();
}
