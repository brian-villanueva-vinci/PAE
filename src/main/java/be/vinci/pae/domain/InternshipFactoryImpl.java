package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.InternshipDTO;

/**
 * Implements InternshipFactory interface.
 */
public class InternshipFactoryImpl implements InternshipFactory {

  /**
   * Creates a InternshipDTO object.
   *
   * @return a new InternshipDTO object.
   */
  @Override
  public InternshipDTO getInternshipDTO() {
    return new InternshipImpl();
  }

}

