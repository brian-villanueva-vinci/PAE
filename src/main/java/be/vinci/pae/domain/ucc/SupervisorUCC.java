package be.vinci.pae.domain.ucc;

import be.vinci.pae.domain.dto.SupervisorDTO;
import java.util.List;

/**
 * SupervisorUCC interface.
 */
public interface SupervisorUCC {

  /**
   * Get a supervisor by his id.
   *
   * @param id the id.
   * @return the supervisorDTO found.
   */
  SupervisorDTO getOneById(int id);

  /**
   * Get all supervisors from a company.
   *
   * @param companyId the company's id.
   * @return all the supervisors from the company.
   */
  List<SupervisorDTO> getAllByCompany(int companyId);

  /**
   * Add a new supervisor.
   *
   * @param supervisorDTO the supervisor to add.
   * @return the added supervisor.
   */
  SupervisorDTO addSupervisor(SupervisorDTO supervisorDTO);

}
