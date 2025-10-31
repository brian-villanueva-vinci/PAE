package be.vinci.pae.services.dao;

import be.vinci.pae.domain.dto.UserDTO;
import java.util.List;

/**
 * UserDAO Interface.
 */
public interface UserDAO {

  /**
   * Get one user by email then set the userDTO if user exist.
   *
   * @param email user' email.
   * @return userDTO with setter corresponding to the email, null otherwise.
   */
  UserDTO getOneUserByEmail(String email);

  /**
   * Add one user.
   *
   * @param user user to add.
   * @return added user.
   */
  UserDTO addOneUser(UserDTO user);

  /**
   * Get one user by id then set the userDTO if user exist.
   *
   * @param id user' id.
   * @return userDTO with setter corresponding to the id, null otherwise.
   */
  UserDTO getOneUserById(int id);

  /**
   * Get all users.
   *
   * @return a list of all users.
   */
  List<UserDTO> getAllUsers();

  /**
   * Edit one user.
   *
   * @param user    the user to edit.
   * @param version the current version of the user.
   * @return a UserDTO of the edited user.
   */
  UserDTO editOneUser(UserDTO user, int version);
}
