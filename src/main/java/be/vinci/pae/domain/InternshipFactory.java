package be.vinci.pae.domain;


import be.vinci.pae.domain.dto.InternshipDTO;

/**
 * InternshipFactory Interface.
 */
public interface InternshipFactory {

  /**
   * Creates a InternshipDTO object.
   *
   * @return a new InternshipDTO object.
   */
  InternshipDTO getInternshipDTO();
}
