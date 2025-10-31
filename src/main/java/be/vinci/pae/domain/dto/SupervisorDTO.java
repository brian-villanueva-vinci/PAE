package be.vinci.pae.domain.dto;

import be.vinci.pae.domain.SupervisorImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * SupervisorDTO interface.
 */
@JsonDeserialize(as = SupervisorImpl.class)
public interface SupervisorDTO {

  /**
   * Get the supervisor's id.
   *
   * @return the supervisor's id.
   */
  int getId();

  /**
   * Set the supervisor's id.
   *
   * @param id id to set.
   */
  void setId(int id);

  /**
   * Get the supervisor's company.
   *
   * @return the supervisor's company.
   */
  CompanyDTO getCompany();

  /**
   * Set the supervisor's company.
   *
   * @param company company to set.
   */
  void setCompany(CompanyDTO company);

  /**
   * Get the supervisor's lastname.
   *
   * @return the supervisor's lastname.
   */
  String getLastname();

  /**
   * Set the supervisor's lastname.
   *
   * @param lastname lastname to set.
   */
  void setLastname(String lastname);

  /**
   * Get the supervisor's firstname.
   *
   * @return the supervisor's firstname.
   */
  String getFirstname();

  /**
   * Set the supervisor's firstname.
   *
   * @param firstname firstname to set.
   */
  void setFirstname(String firstname);

  /**
   * Get the supervisor's phone number.
   *
   * @return the supervisor's phone number.
   */
  String getPhoneNumber();

  /**
   * Set the supervisor's phone number.
   *
   * @param phoneNumber phone number to set.
   */
  void setPhoneNumber(String phoneNumber);

  /**
   * Get the supervisor's email.
   *
   * @return the supervisor's email.
   */
  String getEmail();

  /**
   * Set the supervisor's email.
   *
   * @param email email to set.
   */
  void setEmail(String email);
}
