package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.ContactDTO;

/**
 * Implementation of ContactFactory.
 */
public class ContactFactoryImpl implements ContactFactory {

  @Override
  public ContactDTO getContactDTO() {
    return new ContactImpl();
  }
}
