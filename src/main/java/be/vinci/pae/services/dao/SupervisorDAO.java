package be.vinci.pae.services.dao;

import be.vinci.pae.domain.dto.SupervisorDTO;
import java.util.List;

/**
 * SupervisorDAO interface.
 */
public interface SupervisorDAO {

  /**
   * Get one supervisor by his id.
   *
   * @param id the id.
   * @return the supervisorDTO.
   */
  SupervisorDTO getOneById(int id);

  /**
   * Get one supervisor by his phone number.
   *
   * @param phoneNumber the phone number.
   * @return the supervisorDTO.
   */
  SupervisorDTO getOneByPhoneNumber(String phoneNumber);

  /**
   * Get all the supervisors of a company.
   *
   * @param companyId the company's id.
   * @return all the supervisors from a company.
   */
  List<SupervisorDTO> getAllByCompany(int companyId);

  /**
   * Add a supervisor.
   *
   * @param supervisorDTO the supervisor to add.
   * @return the added supervisor.
   */
  SupervisorDTO addSupervisor(SupervisorDTO supervisorDTO);

}
