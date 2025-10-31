package be.vinci.pae.domain.ucc;

import be.vinci.pae.domain.dto.UserDTO;
import java.util.List;

/**
 * UserUCC Interface.
 */
public interface UserUCC {

  /**
   * Get a user associated with an email and check their password with the password entered.
   *
   * @param email    the user's email.
   * @param password the user's hashed password.
   * @return a UserDTO if existing user and correct password;.
   */
  UserDTO login(String email, String password);

  /**
   * Get all users.
   *
   * @param user the user.
   * @return a list containing all the users.
   */
  List<UserDTO> getAllUsers(UserDTO user);

  /**
   * Get a user by his id.
   *
   * @param id the user id.
   * @return the user if found.
   */
  UserDTO getOneById(int id);

  /**
   * Register a user.
   *
   * @param user user to register.
   * @return a UserDTO of registered user.
   */
  UserDTO register(UserDTO user);

  /**
   * Edit one user.
   *
   * @param currentUser user containing current info.
   * @param newUser     user containing new info.
   * @return a UserDTO of the edited user.
   */
  UserDTO editOneUser(UserDTO currentUser, UserDTO newUser);

  /**
   * Edit a password.
   *
   * @param userDTO          the user to edit.
   * @param oldPassword      the old password of the user
   * @param newPassword      a new password of the user.
   * @param repeatedPassword a repeated password of the user.
   * @return a UserDTO of an edited password.
   */
  UserDTO editPassword(UserDTO userDTO, String oldPassword, String newPassword,
      String repeatedPassword);
}
