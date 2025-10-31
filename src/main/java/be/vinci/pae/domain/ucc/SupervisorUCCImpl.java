package be.vinci.pae.domain.ucc;

import be.vinci.pae.domain.dto.SupervisorDTO;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.SupervisorDAO;
import be.vinci.pae.utils.Logs;
import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.ResourceNotFoundException;
import jakarta.inject.Inject;
import java.util.List;
import org.apache.logging.log4j.Level;

/**
 * Implementation of SupervisorUCC.
 */
public class SupervisorUCCImpl implements SupervisorUCC {

  @Inject
  private SupervisorDAO supervisorDAO;
  @Inject
  private DalServices dalServices;

  @Override
  public SupervisorDTO getOneById(int id) {
    Logs.log(Level.INFO, "SupervisorUCCImpl (getOneById) : entrance");
    SupervisorDTO supervisorDTO = supervisorDAO.getOneById(id);
    if (supervisorDTO == null) {
      throw new ResourceNotFoundException();
    }
    Logs.log(Level.DEBUG, "SupervisorUCCImpl (getOneById) : success!");
    return supervisorDTO;
  }

  @Override
  public List<SupervisorDTO> getAllByCompany(int companyId) {
    Logs.log(Level.INFO, "SupervisorUCCImpl (getAll) : entrance");
    return supervisorDAO.getAllByCompany(companyId);
  }

  @Override
  public SupervisorDTO addSupervisor(SupervisorDTO supervisorDTO) {
    Logs.log(Level.INFO, "SupervisorUCCImpl (addSupervisor) : entrance");
    try {
      dalServices.startTransaction();
      SupervisorDTO supervisorFound = supervisorDAO
          .getOneByPhoneNumber(supervisorDTO.getPhoneNumber());
      if (supervisorFound != null) {
        throw new DuplicateException();
      }
      SupervisorDTO addedSupervisor = supervisorDAO.addSupervisor(supervisorDTO);
      dalServices.commitTransaction();
      return addedSupervisor;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      Logs.log(Level.ERROR, "SupervisorUCCImpl (addSupervisor) : internal error " + e);
      throw e;
    }
  }
}
