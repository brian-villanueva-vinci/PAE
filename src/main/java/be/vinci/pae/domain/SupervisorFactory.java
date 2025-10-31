package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.SupervisorDTO;

/**
 * SupervisorFactory interface.
 */
public interface SupervisorFactory {

  /**
   * Get a new SupervisorDTO.
   *
   * @return the new SupervisorDTO.
   */
  SupervisorDTO getSupervisorDTO();

}
