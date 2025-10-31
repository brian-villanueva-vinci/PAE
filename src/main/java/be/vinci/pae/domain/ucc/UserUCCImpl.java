package be.vinci.pae.domain.ucc;

import be.vinci.pae.domain.User;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.UserDAO;
import be.vinci.pae.utils.Logs;
import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.InvalidRequestException;
import be.vinci.pae.utils.exceptions.ResourceNotFoundException;
import be.vinci.pae.utils.exceptions.UnauthorizedAccessException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.apache.logging.log4j.Level;

/**
 * User UCC.
 */
public class UserUCCImpl implements UserUCC {

  @Inject
  private UserDAO userDAO;
  @Inject
  private DalServices dalServices;

  /**
   * Get a user associated with an email and check their password with the password entered.
   *
   * @param email    the user's email.
   * @param password the user's hashed password.
   * @return a UserDTO if existing user and correct password;.
   */
  @Override
  public UserDTO login(String email, String password) {
    User user;
    UserDTO userDTOFound;
    try {
      Logs.log(Level.INFO, "UserUCC (login) : entrance");
      dalServices.startTransaction();
      userDTOFound = userDAO.getOneUserByEmail(email.toLowerCase());
      user = (User) userDTOFound;
    } catch (FatalException e) {
      dalServices.rollbackTransaction();
      throw e;
    }
    if (user == null) {
      Logs.log(Level.ERROR, "UserUCC (login) : user not found");
      dalServices.rollbackTransaction();
      throw new ResourceNotFoundException("User not found.");
    }
    if (!user.checkPassword(password)) {
      Logs.log(Level.ERROR, "UserUCC (login) : wrong password");
      dalServices.rollbackTransaction();
      throw new UnauthorizedAccessException("The password is incorrect");
    }
    dalServices.commitTransaction();
    Logs.log(Level.DEBUG, "UserUCC (login) : success!");
    return userDTOFound;
  }

  /**
   * Get all users.
   *
   * @return a list containing all the users.
   */
  @Override
  public List<UserDTO> getAllUsers(UserDTO userDTO) {
    if (userDTO.getRole().equals("Etudiant")) {
      throw new UnauthorizedAccessException("Student can't access this.");
    }
    List<UserDTO> userList;
    try {
      Logs.log(Level.INFO, "UserUCC (getAllUsers) : entrance");
      dalServices.startTransaction();
      userList = userDAO.getAllUsers();
    } catch (FatalException e) {
      dalServices.rollbackTransaction();
      throw e;
    }
    dalServices.commitTransaction();
    Logs.log(Level.DEBUG, "UserUCC (getAllUsers) : success!");
    return userList;
  }

  /**
   * Get a user by his id.
   *
   * @param id the user id.
   * @return the user found.
   */
  public UserDTO getOneById(int id) {
    UserDTO user;
    try {
      Logs.log(Level.INFO, "UserUCC (getOneById) : entrance");
      dalServices.startTransaction();
      user = userDAO.getOneUserById(id);
    } catch (FatalException e) {
      dalServices.rollbackTransaction();
      throw e;
    }
    if (user == null) {
      Logs.log(Level.ERROR, "UserUCC (getOneById) : user is not in db");
      dalServices.rollbackTransaction();
      throw new ResourceNotFoundException();
    }
    dalServices.commitTransaction();
    Logs.log(Level.DEBUG, "UserUCC (getOneById) : success!");
    return user;
  }

  /**
   * Register a user.
   *
   * @param user user to register.
   * @return a UserDTO of registered user, null otherwise.
   */
  @Override
  public UserDTO register(UserDTO user) {

    Logs.log(Level.DEBUG, "UserUCC (register) : entrance");
    
    String lowerCaseEmail = user.getEmail().toLowerCase();
    user.setEmail(lowerCaseEmail);

    LocalDate localDate = LocalDate.now();
    Date registrationDate = Date.valueOf(localDate);
    user.setRegistrationDate(registrationDate);
    int monthValue = localDate.getMonthValue();
    String schoolYear;
    if (monthValue >= 9) {
      schoolYear = localDate.getYear() + "-" + localDate.plusYears(1).getYear();
    } else {
      schoolYear = localDate.minusYears(1).getYear() + "-" + localDate.getYear();
    }
    user.setSchoolYear(schoolYear);

    UserDTO registeredUser;

    try {
      dalServices.startTransaction();
      UserDTO existingUser = userDAO.getOneUserByEmail(user.getEmail());
      if (existingUser != null) {
        throw new DuplicateException("Cannot add existing user");
      }

      User userHashPwd = (User) user;
      userHashPwd.hashPassword();
      userHashPwd.setVersion(1);

      registeredUser = userDAO.addOneUser(userHashPwd);

      dalServices.commitTransaction();
      Logs.log(Level.DEBUG, "UserUCC (register) : success!");
      return registeredUser;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }

  }

  @Override
  public UserDTO editOneUser(UserDTO currentUser, UserDTO newUser) {
    UserDTO editedUser;
    if (currentUser.getVersion() != newUser.getVersion()) {
      Logs.log(Level.ERROR, "UserResource (editUser) : conflict version");
      throw new DuplicateException("Different version from front and back");
    }

    int currentVersion = currentUser.getVersion();
    newUser.setPassword(currentUser.getPassword());
    newUser.setVersion(currentVersion + 1);

    try {
      dalServices.startTransaction();

      editedUser = userDAO.editOneUser(newUser, currentVersion);
      if (editedUser == null) {
        throw new DuplicateException("Someone updated before us");
      }

      dalServices.commitTransaction();
      Logs.log(Level.DEBUG, "UserUCC (editOneUser) : success!");
      return editedUser;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  @Override
  public UserDTO editPassword(UserDTO userDTO, String oldPassword, String newPassword,
      String repeatedPassword) {
    UserDTO newPasswordUserDTO;

    User user = (User) userDTO;
    if (!user.checkPassword(oldPassword)) {
      throw new UnauthorizedAccessException("The password is incorrect");
    }
    if (!newPassword.equals(repeatedPassword)) {
      throw new InvalidRequestException(
          "The repeated password does not match with the new password.");
    }

    int currentVersion = userDTO.getVersion();
    user.setPassword(newPassword);
    user.hashPassword();
    user.setVersion(currentVersion + 1);

    try {
      dalServices.startTransaction();

      newPasswordUserDTO = userDAO.editOneUser(userDTO, currentVersion);
      if (newPasswordUserDTO == null) {
        throw new DuplicateException("Someone updated before us");
      }

      dalServices.commitTransaction();
      Logs.log(Level.DEBUG, "UserUCC (editPassword) : success!");
      return newPasswordUserDTO;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }
}
