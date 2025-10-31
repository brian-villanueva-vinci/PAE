package be.vinci.pae.services.dao;

import be.vinci.pae.domain.CompanyFactory;
import be.vinci.pae.domain.SupervisorFactory;
import be.vinci.pae.domain.dto.CompanyDTO;
import be.vinci.pae.domain.dto.SupervisorDTO;
import be.vinci.pae.services.dal.DalBackendServices;
import be.vinci.pae.utils.Logs;
import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;

/**
 * Implementation of SupervisorDAO.
 */
public class SupervisorDAOImpl implements SupervisorDAO {

  @Inject
  private DalBackendServices dalServices;
  @Inject
  private SupervisorFactory supervisorFactory;
  @Inject
  private CompanyFactory companyFactory;

  @Override
  public SupervisorDTO getOneById(int id) {
    String request = """
        SELECT su.supervisor_id, su.company AS su_company, su.lastname AS su_lastname,
        su.firstname AS su_firstname, su.phone_number AS su_phone_number, su.email AS su_email,
        cm.company_id, cm.name, cm.designation, cm.address, cm.phone_number AS cm_phone_number,
        cm.email AS cm_email, cm.is_blacklisted, cm.blacklist_motivation, cm.version AS cm_version
        FROM prostage.supervisors su, prostage.companies cm
        WHERE su.supervisor_id = ?
        """;

    try (PreparedStatement ps = dalServices.getPreparedStatement(request)) {
      ps.setInt(1, id);
      Logs.log(Level.DEBUG, "SupervisorDAO (getOneById) : success!");
      return buildSupervisorDTO(ps);
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "SupervisorDAO (getOneById) : internal error");
      throw new FatalException(e);
    }

  }

  @Override
  public SupervisorDTO getOneByPhoneNumber(String phoneNumber) {
    String request = """
        SELECT su.supervisor_id, su.company AS su_company, su.lastname AS su_lastname,
        su.firstname AS su_firstname, su.phone_number AS su_phone_number, su.email AS su_email,
        cm.company_id, cm.name, cm.designation, cm.address, cm.phone_number AS cm_phone_number,
        cm.email AS cm_email, cm.is_blacklisted, cm.blacklist_motivation, cm.version AS cm_version
        FROM prostage.supervisors su, prostage.companies cm
        WHERE su.phone_number = ?
        """;

    try (PreparedStatement ps = dalServices.getPreparedStatement(request)) {
      ps.setString(1, phoneNumber);
      return buildSupervisorDTO(ps);
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "SupervisorDAO (getOneByPhoneNumber) : internal error");
      throw new FatalException(e);
    }
  }

  @Override
  public List<SupervisorDTO> getAllByCompany(int companyId) {
    String requestSql = """
        SELECT s.supervisor_id, s.lastname AS su_lastname, s.firstname AS su_firstname,
        s.phone_number AS su_phone_number, s.email AS su_email,
                
        cm.company_id, cm.name, cm.designation, cm.address, cm.phone_number AS cm_phone_number,
        cm.email AS cm_email, cm.is_blacklisted, cm.blacklist_motivation, cm.version AS cm_version
                
        FROM prostage.supervisors s, prostage.companies cm
        WHERE s.company = ? AND cm.company_id = ?
        """;

    try (PreparedStatement ps = dalServices.getPreparedStatement(requestSql)) {
      ps.setInt(1, companyId);
      ps.setInt(2, companyId);
      return buildSupervisorDTOList(ps);
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "SupervisorDAO (getAllByCompany) : internal error");
      throw new FatalException(e);
    }
  }

  @Override
  public SupervisorDTO addSupervisor(SupervisorDTO supervisorDTO) {
    String requestSql = """
        INSERT INTO prostage.supervisors VALUES (DEFAULT,?,?,?,?,?)
        """;

    try (PreparedStatement ps = dalServices.getPreparedStatement(requestSql)) {
      ps.setInt(1, supervisorDTO.getCompany().getId());
      ps.setString(2, supervisorDTO.getLastname());
      ps.setString(3, supervisorDTO.getFirstname());
      ps.setString(4, supervisorDTO.getPhoneNumber());
      ps.setString(5, supervisorDTO.getEmail());
      ps.execute();
      return getOneByPhoneNumber(supervisorDTO.getPhoneNumber());
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "SupervisorDAO (addSupervisor) : internal error");
      throw new FatalException(e);
    }
  }

  private List<SupervisorDTO> buildSupervisorDTOList(PreparedStatement ps) {
    List<SupervisorDTO> supervisorDTOList = new ArrayList<>();
    try (ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        CompanyDTO companyDTO = DTOSetServices.setCompanyDTO(companyFactory.getCompanyDTO(), rs);
        supervisorDTOList.add(DTOSetServices.setSupervisorDTO(supervisorFactory.getSupervisorDTO(),
            rs, companyDTO));
      }
      return supervisorDTOList;
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "SupervisorDAO (buildSupervisorDTOList) : internal error!");
      throw new DuplicateException();
    }
  }

  private SupervisorDTO buildSupervisorDTO(PreparedStatement ps) {
    try (ResultSet rs = ps.executeQuery()) {
      if (rs.next()) {
        CompanyDTO companyDTO = DTOSetServices.setCompanyDTO(companyFactory.getCompanyDTO(), rs);
        return DTOSetServices.setSupervisorDTO(supervisorFactory.getSupervisorDTO(), rs,
            companyDTO);
      }
      return null;
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "SupervisorDAO (buildSupervisorDTO) : internal error!");
      throw new DuplicateException();
    }
  }

}
