package be.vinci.pae.services.dao;

import be.vinci.pae.domain.dto.CompanyDTO;
import be.vinci.pae.domain.dto.ContactDTO;
import be.vinci.pae.domain.dto.InternshipDTO;
import be.vinci.pae.domain.dto.SupervisorDTO;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.utils.exceptions.FatalException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DTO attribute setter class.
 */
class DTOSetServices {

  /**
   * Set all companyDTO attribute.
   *
   * @param companyDTO CompanyDTO to set attribute on.
   * @param rs         data to retrieves from.
   * @return CompanyDTO with its attribute set.
   */
  public static CompanyDTO setCompanyDTO(CompanyDTO companyDTO, ResultSet rs) {
    try {
      companyDTO.setId(rs.getInt("company_id"));
      companyDTO.setName(rs.getString("name"));
      companyDTO.setDesignation(rs.getString("designation"));
      companyDTO.setAddress(rs.getString("address"));
      companyDTO.setPhoneNumber(rs.getString("cm_phone_number"));
      companyDTO.setEmail(rs.getString("cm_email"));
      companyDTO.setIsBlacklisted(rs.getBoolean("is_blacklisted"));
      companyDTO.setBlacklistMotivation(rs.getString("blacklist_motivation"));
      companyDTO.setVersion(rs.getInt("cm_version"));
      return companyDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Set all contactDTO attribute.
   *
   * @param contactDTO ContactDTO to set attribute on.
   * @param rs         data to retrieves from.
   * @return ContactDTO with its attribute set.
   */
  public static ContactDTO setContactDTO(ContactDTO contactDTO, ResultSet rs,
      CompanyDTO companyDTO,
      UserDTO studentDTO) {
    try {
      contactDTO.setId(rs.getInt("contact_id"));
      contactDTO.setCompany(companyDTO);
      contactDTO.setStudent(studentDTO);
      contactDTO.setMeeting(rs.getString("meeting"));
      contactDTO.setState(rs.getString("contact_state"));
      contactDTO.setReasonRefusal(rs.getString("reason_for_refusal"));
      contactDTO.setSchoolYear(rs.getString("ct_school_year"));
      contactDTO.setVersion(rs.getInt("ct_version"));
      return contactDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Set all internshipDTO attribute.
   *
   * @param internship internshipDTO to set attribute on.
   * @param rs         data to retrieves from.
   * @return InternshipDTO with its attribute set.
   */
  public static InternshipDTO setInternshipDTO(InternshipDTO internship, ResultSet rs,
      ContactDTO contactDTO,
      SupervisorDTO supervisorDTO) {
    try {
      internship.setId(rs.getInt("internship_id"));
      internship.setContact(contactDTO);
      internship.setSupervisor(supervisorDTO);
      internship.setSignatureDate(rs.getDate("signature_date"));
      internship.setProject(rs.getString("project"));
      internship.setSchoolYear(rs.getString("school_year"));
      internship.setVersion(rs.getInt("version"));
      return internship;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Set all companyDTO attribute.
   *
   * @param supervisorDTO SupervisorDTO to set attribute on.
   * @param rs            data to retrieves from.
   * @return SupervisorDTO with its attribute set.
   */
  public static SupervisorDTO setSupervisorDTO(SupervisorDTO supervisorDTO, ResultSet rs,
      CompanyDTO companyDTO) {
    try {
      supervisorDTO.setId(rs.getInt("supervisor_id"));
      supervisorDTO.setCompany(companyDTO);
      supervisorDTO.setLastname(rs.getString("su_lastname"));
      supervisorDTO.setFirstname(rs.getString("su_firstname"));
      supervisorDTO.setPhoneNumber(rs.getString("su_phone_number"));
      supervisorDTO.setEmail(rs.getString("su_email"));
      return supervisorDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Set all companyDTO attribute.
   *
   * @param studentDTO UserDTO to set attribute on.
   * @param rs         data to retrieves from.
   * @return UserDTO with its attribute set.
   */
  public static UserDTO setUserDTO(UserDTO studentDTO, ResultSet rs) {
    try {
      studentDTO.setId(rs.getInt("user_id"));
      studentDTO.setEmail(rs.getString("us_email"));
      studentDTO.setLastname(rs.getString("us_lastname"));
      studentDTO.setFirstname(rs.getString("us_firstname"));
      studentDTO.setPhoneNumber(rs.getString("us_phone_number"));
      studentDTO.setPassword(rs.getString("password"));
      studentDTO.setRegistrationDate(rs.getDate("registration_date"));
      studentDTO.setSchoolYear(rs.getString("us_school_year"));
      studentDTO.setRole(rs.getString("role"));
      studentDTO.setVersion(rs.getInt("us_version"));
      return studentDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

}
