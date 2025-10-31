package be.vinci.pae.domain.dto;

import be.vinci.pae.domain.CompanyImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * CompanyDTO interface.
 */
@JsonDeserialize(as = CompanyImpl.class)
public interface CompanyDTO {

  /**
   * Get the company's id.
   *
   * @return the company's id.
   */
  int getId();

  /**
   * Set the company's id.
   *
   * @param id id to set.
   */
  void setId(int id);

  /**
   * Get the company's name.
   *
   * @return the company's name.
   */
  String getName();

  /**
   * Set the company's name.
   *
   * @param name name to set.
   */
  void setName(String name);

  /**
   * Get the company's designation.
   *
   * @return the company's designation.
   */
  String getDesignation();

  /**
   * Set the company's designation.
   *
   * @param designation designation to set.
   */
  void setDesignation(String designation);

  /**
   * Get the company's address.
   *
   * @return the company's address.
   */
  String getAddress();

  /**
   * Set the company's address.
   *
   * @param address address to set.
   */
  void setAddress(String address);

  /**
   * Get the company's phone number.
   *
   * @return the company's phone number.
   */
  String getPhoneNumber();

  /**
   * Set the company's phone number.
   *
   * @param phoneNumber phone number to set.
   */
  void setPhoneNumber(String phoneNumber);

  /**
   * Get the company's email.
   *
   * @return the company's email.
   */
  String getEmail();

  /**
   * Set the company's email.
   *
   * @param email email to set.
   */
  void setEmail(String email);

  /**
   * Get if the company is blacklisted.
   *
   * @return true if it is, false otherwise.
   */
  boolean isBlacklisted();

  /**
   * Set if the company is blacklisted.
   *
   * @param blacklisted blacklisted to set.
   */
  void setIsBlacklisted(boolean blacklisted);

  /**
   * Get the company's blacklist motivation.
   *
   * @return the company's blacklist motivation.
   */
  String getBlacklistMotivation();

  /**
   * Set the company's blacklist motivation.
   *
   * @param blacklistMotivation blacklist motivation to set.
   */
  void setBlacklistMotivation(String blacklistMotivation);

  /**
   * Get the company's version.
   *
   * @return the company's version.
   */
  int getVersion();

  /**
   * Set the company's version.
   *
   * @param version school version.
   */
  void setVersion(int version);

}
