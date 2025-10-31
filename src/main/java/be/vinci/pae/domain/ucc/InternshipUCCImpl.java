package be.vinci.pae.domain.ucc;

import be.vinci.pae.domain.Contact;
import be.vinci.pae.domain.dto.InternshipDTO;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.InternshipDAO;
import be.vinci.pae.utils.Logs;
import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.InvalidRequestException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.ResourceNotFoundException;
import be.vinci.pae.utils.exceptions.UnauthorizedAccessException;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;

/**
 * Implementation of InternshipUCC.
 */
public class InternshipUCCImpl implements InternshipUCC {

  @Inject
  private InternshipDAO internshipDAO;
  @Inject
  private DalServices dalServices;


  @Override
  public InternshipDTO getOneByStudentCurrentSchoolYear(int student) {
    InternshipDTO internship;
    try {
      dalServices.startTransaction();
      String schoolYear = getCurrentSchoolYear();
      internship = internshipDAO.getOneInternshipByIdUserSchoolYear(student, schoolYear);
      if (internship == null) {
        throw new ResourceNotFoundException();
      }
      dalServices.commitTransaction();
      return internship;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }


  @Override
  public InternshipDTO getOneById(int id, int actualStudent) {
    InternshipDTO internship;
    try {
      dalServices.startTransaction();
      internship = internshipDAO.getOneInternshipById(id);
      if (internship == null) {
        throw new ResourceNotFoundException();
      } else if (internship.getContact().getStudent().getId()
          != actualStudent) {
        throw new NotAllowedException();
      }
      dalServices.commitTransaction();
      return internship;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  @Override
  public List<InternshipDTO> getAllInternships() {
    try {
      dalServices.startTransaction();
      List<InternshipDTO> internshipDTOList = internshipDAO.getAllInternships();
      dalServices.commitTransaction();
      return internshipDTOList;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  @Override
  public InternshipDTO createInternship(InternshipDTO internshipDTO, int studentId) {
    Logs.log(Level.INFO, "InternshipUCC (createInternship) : entrance");
    if (studentId != internshipDTO.getContact().getStudent().getId()) {
      Logs.log(Level.ERROR, "InternshipUCC (createInternship) : wrong student");
      throw new UnauthorizedAccessException("This user can't create this internship");
    }
    internshipDTO.setSchoolYear(internshipDTO.getContact().getSchoolYear());
    if (!((Contact) internshipDTO.getContact()).isAdmitted()) {
      Logs.log(Level.ERROR, "InternshipUCC (createInternship) : contact is not admitted");
      throw new InvalidRequestException("Contact is not admitted");
    }
    dalServices.startTransaction();
    String schoolYear = getCurrentSchoolYear();
    InternshipDTO existingInternship = internshipDAO.getOneInternshipByIdUserSchoolYear(
        internshipDTO.getContact().getStudent().getId(), schoolYear);
    if (existingInternship != null) {
      Logs.log(Level.ERROR, "InternshipUCC (createInternship) : internship already created");
      throw new DuplicateException("Cannot add existing internship");
    }

    return internshipDAO.createInternship(internshipDTO);
  }

  /**
   * Get the current school year.
   *
   * @return the current school year with format xxxx-xxxx.
   */
  private String getCurrentSchoolYear() {
    LocalDate date = LocalDate.now();
    String schoolYear;
    if (date.getMonthValue() < 9) {
      schoolYear = date.getYear() - 1 + "-" + date.getYear();
    } else {
      schoolYear = date.getYear() + "-" + date.getYear() + 1;
    }
    return schoolYear;
  }

  @Override
  public Map<String, Integer[]> getInternshipCountByYear() {
    try {
      dalServices.startTransaction();
      Map<String, Integer[]> returnedMap = internshipDAO.getInternshipCountByYear();
      dalServices.commitTransaction();
      return returnedMap;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  @Override
  public InternshipDTO editProject(String project, int version, int internshipId) {
    try {
      dalServices.startTransaction();
      InternshipDTO internship = internshipDAO.editProject(project, version, internshipId);
      if (internship.getVersion() != version + 1) {
        Logs.log(Level.ERROR,
            "InternshipUCC (editProject) : the internship's version isn't matching");
        throw new InvalidRequestException();
      }
      dalServices.commitTransaction();
      return internship;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }
}
