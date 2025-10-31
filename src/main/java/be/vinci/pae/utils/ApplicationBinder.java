package be.vinci.pae.utils;

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
import be.vinci.pae.services.dal.DalBackendServices;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dal.DalServicesImpl;
import be.vinci.pae.services.dao.CompanyDAO;
import be.vinci.pae.services.dao.CompanyDAOImpl;
import be.vinci.pae.services.dao.ContactDAO;
import be.vinci.pae.services.dao.ContactDAOImpl;
import be.vinci.pae.services.dao.InternshipDAO;
import be.vinci.pae.services.dao.InternshipDAOImpl;
import be.vinci.pae.services.dao.SupervisorDAO;
import be.vinci.pae.services.dao.SupervisorDAOImpl;
import be.vinci.pae.services.dao.UserDAO;
import be.vinci.pae.services.dao.UserDAOImpl;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * ApplicationBinder class.
 */
@Provider
public class ApplicationBinder extends AbstractBinder {

  /**
   * Binds implementations to their interface.
   */
  @Override
  protected void configure() {
    // DAL
    bind(DalServicesImpl.class).to(DalBackendServices.class)
        .to(DalServices.class).in(Singleton.class);

    // USER
    bind(UserDAOImpl.class).to(UserDAO.class).in(Singleton.class);
    bind(UserFactoryImpl.class).to(UserFactory.class).in(Singleton.class);
    bind(UserUCCImpl.class).to(UserUCC.class).in(Singleton.class);

    // CONTACT
    bind(ContactDAOImpl.class).to(ContactDAO.class).in(Singleton.class);
    bind(ContactFactoryImpl.class).to(ContactFactory.class).in(Singleton.class);
    bind(ContactUCCImpl.class).to(ContactUCC.class).in(Singleton.class);

    // INTERNSHIP
    bind(InternshipDAOImpl.class).to(InternshipDAO.class).in(Singleton.class);
    bind(InternshipFactoryImpl.class).to(InternshipFactory.class).in(Singleton.class);
    bind(InternshipUCCImpl.class).to(InternshipUCC.class).in(Singleton.class);

    // COMPANY
    bind(CompanyDAOImpl.class).to(CompanyDAO.class).in(Singleton.class);
    bind(CompanyFactoryImpl.class).to(CompanyFactory.class).in(Singleton.class);
    bind(CompanyUCCImpl.class).to(CompanyUCC.class).in(Singleton.class);

    // SUPERVISOR
    bind(SupervisorDAOImpl.class).to(SupervisorDAO.class).in(Singleton.class);
    bind(SupervisorFactoryImpl.class).to(SupervisorFactory.class).in(Singleton.class);
    bind(SupervisorUCCImpl.class).to(SupervisorUCC.class).in(Singleton.class);
  }
}
