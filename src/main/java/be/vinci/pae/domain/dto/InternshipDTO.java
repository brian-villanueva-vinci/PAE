package be.vinci.pae.domain.dto;

import be.vinci.pae.domain.InternshipImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.sql.Date;

/**
 * InternshipDTO interface.
 */
@JsonDeserialize(as = InternshipImpl.class)
public interface InternshipDTO {

  /**
   * Get the internship's id.
   *
   * @return the internship's.
   */
  int getId();

  /**
   * Set the internship's id.
   *
   * @param id id to set.
   */
  void setId(int id);

  /**
   * Get the internship's contact.
   *
   * @return the internship's contact.
   */
  ContactDTO getContact();

  /**
   * Set the internship's contact.
   *
   * @param contact contact to set.
   */
  void setContact(ContactDTO contact);

  /**
   * Get the internship's supervisor.
   *
   * @return the internship's supervisor.
   */
  SupervisorDTO getSupervisor();

  /**
   * Set the internship's supervisor.
   *
   * @param supervisor supervisor to set.
   */
  void setSupervisor(SupervisorDTO supervisor);

  /**
   * Get the internship's signature date.
   *
   * @return the internship's signature date.
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  Date getSignatureDate();

  /**
   * Set the internship's signature date.
   *
   * @param signatureDate signature date to set.
   */
  void setSignatureDate(Date signatureDate);

  /**
   * Get the internship's project.
   *
   * @return the internship's project.
   */
  String getProject();

  /**
   * Set the internship's project.
   *
   * @param project project to set.
   */
  void setProject(String project);

  /**
   * Get the internship's school year.
   *
   * @return the internship's school year.
   */
  String getSchoolYear();

  /**
   * Set the internship's school year.
   *
   * @param schoolYear school year to set.
   */
  void setSchoolYear(String schoolYear);

  /**
   * Get the internship's database version.
   *
   * @return the internship's database version.
   */
  int getVersion();

  /**
   * Set the internship's database version.
   *
   * @param version version to set.
   */
  void setVersion(int version);

}