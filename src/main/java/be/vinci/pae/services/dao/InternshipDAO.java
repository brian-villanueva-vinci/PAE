package be.vinci.pae.services.dao;

import be.vinci.pae.domain.dto.InternshipDTO;
import java.util.List;
import java.util.Map;

/**
 * InternshipDAO interface.
 */
public interface InternshipDAO {


  /**
   * Get one internship by student id and school yearthen set the internshipDTO if intership exist.
   *
   * @param student    student's id.
   * @param schoolYear school year.
   * @return internshipDTO with setter corresponding to the id, null otherwise.
   */
  InternshipDTO getOneInternshipByIdUserSchoolYear(int student, String schoolYear);


  /**
   * Get one internship by id then set the internshipDTO if intership exist.
   *
   * @param id internship's id.
   * @return internshipDTO with setter corresponding to the id, null otherwise.
   */
  InternshipDTO getOneInternshipById(int id);

  /**
   * Get all internships.
   *
   * @return all internships.
   */
  List<InternshipDTO> getAllInternships();

  /**
   * Get one internship by a contact id.
   *
   * @param id the contact id.
   * @return the internship found.
   */
  InternshipDTO getOneByContact(int id);

  /**
   * Create one internship.
   *
   * @param internshipDTO the dto containing the internship datas.
   * @return the created internship.
   */
  InternshipDTO createInternship(InternshipDTO internshipDTO);

  /**
   * Get internship count by year.
   *
   * @return map with number of internship and total student by year.
   */
  Map<String, Integer[]> getInternshipCountByYear();

  /**
   * update the internship's subject.
   *
   * @param subject      the internship subject.
   * @param version      the version of the internship
   * @param internshipId the internship's id.
   * @return the internship edited.
   */
  InternshipDTO editProject(String subject, int version, int internshipId);
}
