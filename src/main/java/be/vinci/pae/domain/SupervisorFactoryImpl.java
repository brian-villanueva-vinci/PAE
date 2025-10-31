package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.SupervisorDTO;

/**
 * Implementation of SupervisorFactory.
 */
public class SupervisorFactoryImpl implements SupervisorFactory {

  @Override
  public SupervisorDTO getSupervisorDTO() {
    return new SupervisorImpl();
  }

}
