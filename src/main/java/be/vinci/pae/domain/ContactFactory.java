package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.ContactDTO;

/**
 * ContactFactory interface.
 */
public interface ContactFactory {

  /**
   * Creates a ContactDTO object.
   *
   * @return a new ContactDTO object.
   */
  ContactDTO getContactDTO();

}
