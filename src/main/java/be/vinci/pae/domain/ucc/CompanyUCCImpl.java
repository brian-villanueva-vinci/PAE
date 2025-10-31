package be.vinci.pae.domain.ucc;

import be.vinci.pae.domain.dto.CompanyDTO;
import be.vinci.pae.domain.dto.ContactDTO;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.CompanyDAO;
import be.vinci.pae.services.dao.ContactDAO;
import be.vinci.pae.services.dao.UserDAO;
import be.vinci.pae.utils.Logs;
import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.InvalidRequestException;
import be.vinci.pae.utils.exceptions.ResourceNotFoundException;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;

/**
 * Company UCC.
 */
public class CompanyUCCImpl implements CompanyUCC {

  @Inject
  private CompanyDAO companyDAO;
  @Inject
  private UserDAO userDAO;
  @Inject
  private DalServices dalServices;
  @Inject
  private ContactDAO contactDAO;

  @Override
  public CompanyDTO findOneById(int id) {
    Logs.log(Level.INFO, "CompanyUCC (findOneById) : entrance");
    CompanyDTO company;
    try {
      dalServices.startTransaction();
      company = companyDAO.getOneCompanyById(id);
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
    Logs.log(Level.DEBUG, "CompanyUCC (findOneById) : success!");
    dalServices.commitTransaction();
    return company;
  }

  /**
   * Get all companies.
   *
   * @return a list containing all the companies.
   */
  @Override
  public Map<Integer, Map<CompanyDTO, Map<String, Integer>>> getAllCompanies() {
    Map<Integer, Map<CompanyDTO, Map<String, Integer>>> companyList;
    try {
      Logs.log(Level.DEBUG, "CompanyUCC (getAllCompanies) : entrance");
      dalServices.startTransaction();
      companyList = companyDAO.getAllCompanies();
    } catch (FatalException e) {
      dalServices.rollbackTransaction();
      throw e;
    }
    dalServices.commitTransaction();
    Logs.log(Level.DEBUG, "CompanyUCC (getAllCompanies) : success!");
    return companyList;
  }

  /**
   * Get all companies available for one user.
   *
   * @return a list containing all the companies.
   */
  @Override
  public List<CompanyDTO> getAllCompaniesByUser(int userId) {
    List<CompanyDTO> companyList;
    try {
      Logs.log(Level.DEBUG, "CompanyUCC (getAllCompaniesByUser) : entrance");
      dalServices.startTransaction();
      UserDTO user = userDAO.getOneUserById(userId);
      if (user == null) {
        Logs.log(Level.ERROR, "CompanyUCC (getAllCompaniesByUser) : user not found");
        throw new ResourceNotFoundException();
      }
      LocalDate date = LocalDate.now();
      String schoolYear;
      if (date.getMonthValue() < 9) {
        schoolYear = date.getYear() - 1 + "-" + date.getYear();
      } else {
        schoolYear = date.getYear() + "-" + date.getYear() + 1;
      }
      companyList = companyDAO.getAllCompaniesByUserIdSchoolYear(userId, schoolYear);
      dalServices.commitTransaction();
      Logs.log(Level.DEBUG, "CompanyUCC (getAllCompaniesByUser) : success!");
      return companyList;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  /**
   * Register a company. If existing company with same name, it has to have a new designation.
   *
   * @param company company to add.
   * @return CompanyDTO of added company, null otherwise.
   */
  @Override
  public CompanyDTO registerCompany(CompanyDTO company) {

    CompanyDTO registeredCompany;

    try {
      dalServices.startTransaction();
      CompanyDTO existingCompany;
      if (company.getDesignation().isBlank()) {
        company.setDesignation(null);
        List<CompanyDTO> existingCompaniesWithNullDesignation = companyDAO.getAllCompaniesByName(
            company.getName());
        if (existingCompaniesWithNullDesignation.size() > 0) {
          throw new DuplicateException(
              "Already exist company with same name and designation, add a different designation");
        }
      } else {
        existingCompany = companyDAO.getOneCompanyByNameDesignation(company.getName(),
            company.getDesignation());
        if (existingCompany != null) {
          throw new DuplicateException(
              "Already exist company with same name and designation, add a different designation");
        }
      }

      if (company.getEmail().isBlank()) {
        company.setEmail(null);
      }

      if (company.getPhoneNumber().isBlank()) {
        company.setPhoneNumber(null);
      }

      company.setIsBlacklisted(false);
      company.setVersion(1);

      registeredCompany = companyDAO.addOneCompany(company);
      dalServices.commitTransaction();
      return registeredCompany;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }

  }

  @Override
  public CompanyDTO blacklist(int companyId, String blacklistMotivation, int version) {
    CompanyDTO companyDTO;
    Logs.log(Level.DEBUG, "CompanyUCC (blacklist) : entrance");
    try {
      dalServices.startTransaction();
      companyDTO = companyDAO.getOneCompanyById(companyId);
      if (companyDTO.isBlacklisted()) {
        Logs.log(Level.ERROR, "CompanyUCC (blacklist) : the company is already blacklisted");
        dalServices.rollbackTransaction();
        throw new DuplicateException();
      }
      companyDTO = companyDAO.blacklist(companyId, blacklistMotivation, version);
      if (companyDTO.getVersion() != version + 1) {
        Logs.log(Level.ERROR, "CompanyUCC (blacklist) : the company's version isn't matching");
        throw new InvalidRequestException();
      }
      List<ContactDTO> contactDTOList = contactDAO.getAllContactsByCompanyStartedOrAdmitted(
          companyId);
      for (ContactDTO contactDTO : contactDTOList) {
        contactDAO.putContactOnHold(contactDTO);
      }
    } catch (FatalException e) {
      dalServices.rollbackTransaction();
      throw e;
    }
    dalServices.commitTransaction();
    return companyDTO;
  }
}
