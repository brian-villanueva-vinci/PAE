package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.CompanyDTO;

/**
 * CompanyFactory interface.
 */
public interface CompanyFactory {

  /**
   * Creates a CompanyDTO object.
   *
   * @return a new CompanyDTO object.
   */
  CompanyDTO getCompanyDTO();

}
