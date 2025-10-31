package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.CompanyDTO;

/**
 * Company interface inheriting the CompanyDTO interface and containing business methods.
 */
public interface Company extends CompanyDTO {

  /**
   * Get if a student can contact this company.
   *
   * @return true if he can, false otherwise.
   */
  boolean studentCanContact();

}
