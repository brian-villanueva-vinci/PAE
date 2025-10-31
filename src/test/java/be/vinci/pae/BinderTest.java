package be.vinci.pae;

import be.vinci.pae.domain.CompanyFactory;
import be.vinci.pae.domain.CompanyFactoryImpl;
import be.vinci.pae.domain.ContactFactory;
import be.vinci.pae.domain.ContactFactoryImpl;
import be.vinci.pae.domain.InternshipFactory;
import be.vinci.pae.domain.InternshipFactoryImpl;
import be.vinci.pae.domain.SupervisorFactory;
import be.vinci.pae.domain.SupervisorFactoryImpl;
import be.vinci.pae.domain.UserFactory;
import be.vinci.pae.domain.UserFactoryImpl;
import be.vinci.pae.domain.ucc.CompanyUCC;
import be.vinci.pae.domain.ucc.CompanyUCCImpl;
import be.vinci.pae.domain.ucc.ContactUCC;
import be.vinci.pae.domain.ucc.ContactUCCImpl;
import be.vinci.pae.domain.ucc.InternshipUCC;
import be.vinci.pae.domain.ucc.InternshipUCCImpl;
import be.vinci.pae.domain.ucc.SupervisorUCC;
import be.vinci.pae.domain.ucc.SupervisorUCCImpl;
import be.vinci.pae.domain.ucc.UserUCC;
import be.vinci.pae.domain.ucc.UserUCCImpl;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.CompanyDAO;
import be.vinci.pae.services.dao.ContactDAO;
import be.vinci.pae.services.dao.InternshipDAO;
import be.vinci.pae.services.dao.SupervisorDAO;
import be.vinci.pae.services.dao.UserDAO;
import jakarta.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.mockito.Mockito;

/**
 * BinderTest class.
 */
public class BinderTest extends AbstractBinder {

  /**
   * Binds mocks and class to interfaces.
   */
  @Override
  protected void configure() {
    // Business
    bind(UserUCCImpl.class).to(UserUCC.class).in(Singleton.class);
    bind(UserFactoryImpl.class).to(UserFactory.class).in(Singleton.class);
    bind(ContactUCCImpl.class).to(ContactUCC.class).in(Singleton.class);
    bind(ContactFactoryImpl.class).to(ContactFactory.class).in(Singleton.class);
    bind(CompanyUCCImpl.class).to(CompanyUCC.class).in(Singleton.class);
    bind(CompanyFactoryImpl.class).to(CompanyFactory.class).in(Singleton.class);
    bind(InternshipUCCImpl.class).to(InternshipUCC.class).in(Singleton.class);
    bind(InternshipFactoryImpl.class).to(InternshipFactory.class).in(Singleton.class);
    bind(SupervisorUCCImpl.class).to(SupervisorUCC.class).in(Singleton.class);
    bind(SupervisorFactoryImpl.class).to(SupervisorFactory.class).in(Singleton.class);

    // Data
    bind(Mockito.mock(UserDAO.class)).to(UserDAO.class);
    bind(Mockito.mock(ContactDAO.class)).to(ContactDAO.class);
    bind(Mockito.mock(CompanyDAO.class)).to(CompanyDAO.class);
    bind(Mockito.mock(InternshipDAO.class)).to(InternshipDAO.class);
    bind(Mockito.mock(SupervisorDAO.class)).to(SupervisorDAO.class);
    bind(Mockito.mock(DalServices.class)).to(DalServices.class);
  }
}
