package be.vinci.pae.domain.dto;

import be.vinci.pae.domain.UserImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.sql.Date;

/**
 * UserDTO interface containing only getters and setters of a User.
 */
@JsonDeserialize(as = UserImpl.class)
public interface UserDTO {

  /**
   * Get the user's id.
   *
   * @return the user's id.
   */
  int getId();

  /**
   * Set the user's id.
   *
   * @param id id to set.
   */
  void setId(int id);

  /**
   * Get the user's email.
   *
   * @return the user's email.
   */
  String getEmail();

  /**
   * Set the user's email.
   *
   * @param email email to set.
   */
  void setEmail(String email);

  /**
   * Get the user's lastname.
   *
   * @return the user's lastname.
   */
  String getLastname();

  /**
   * Set the user's lastname.
   *
   * @param lastname lastname to set.
   */
  void setLastname(String lastname);

  /**
   * Get the user's firstname.
   *
   * @return the user's firstname.
   */
  String getFirstname();

  /**
   * Set the user's firstname.
   *
   * @param firstname firstname to set.
   */
  void setFirstname(String firstname);

  /**
   * Get the user's phone number.
   *
   * @return the user's phone number.
   */
  String getPhoneNumber();

  /**
   * Set the user's phone number.
   *
   * @param phoneNumber phone number to set.
   */
  void setPhoneNumber(String phoneNumber);

  /**
   * Get the user's password.
   *
   * @return the user's password.
   */
  String getPassword();

  /**
   * Set the user's password.
   *
   * @param password password to set.
   */
  void setPassword(String password);

  /**
   * Get the user's registration date.
   *
   * @return the user's registration date.
   */
  Date getRegistrationDate();

  /**
   * Set the user's registration date.
   *
   * @param registrationDate registration date to set.
   */
  void setRegistrationDate(Date registrationDate);

  /**
   * Get the user's school year.
   *
   * @return the user's school year.
   */
  String getSchoolYear();

  /**
   * Set the user's school year.
   *
   * @param schoolYear school year to set.
   */
  void setSchoolYear(String schoolYear);

  /**
   * Get the user's role.
   *
   * @return the user's role.
   */
  String getRole();

  /**
   * Set the user's role.
   *
   * @param role role to set.
   */
  void setRole(String role);

  /**
   * Get the contact's version.
   *
   * @return the contact's version.
   */
  int getVersion();

  /**
   * Set the contact's version.
   *
   * @param version school version.
   */
  void setVersion(int version);
}