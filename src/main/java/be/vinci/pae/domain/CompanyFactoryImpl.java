package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.CompanyDTO;

/**
 * Implementation of CompanyFactory.
 */
public class CompanyFactoryImpl implements CompanyFactory {

  @Override
  public CompanyDTO getCompanyDTO() {
    return new CompanyImpl();
  }
}
