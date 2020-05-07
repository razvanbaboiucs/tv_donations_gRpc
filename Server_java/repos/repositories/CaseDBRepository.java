package repos.repositories;

import model.entities.Case;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repos.utils.ICaseRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CaseDBRepository implements ICaseRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public CaseDBRepository(Properties properties) {
        logger.info("Initializing CaseRepository with properties: {} ", properties);
        dbUtils = new JdbcUtils(properties);
    }

    public Case findOne(Integer id) {
        logger.traceEntry("finding cases with id {}", id);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Cases where Cid = ?")){
            preparedStatement.setInt(1, id);
            try(ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    String name = result.getString("Name");
                    Double totalSumDonated = result.getDouble("TotalSumDonated");
                    Case wantedCase = new Case(id, name, totalSumDonated);
                    logger.traceExit(wantedCase);
                    return wantedCase;
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No case found with id {} ", id);
        return null;
    }

    public Iterable<Case> findAll() {
        logger.traceEntry("Finding all cases");
        Connection connection = dbUtils.getConnection();
        List<Case> cases = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Cases")) {
            try(ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    int cid = result.getInt("cid");
                    String name = result.getString("Name");
                    Double totalSumDonated = result.getDouble("TotalSumDonated");
                    Case givenCase = new Case(cid, name, totalSumDonated);
                    cases.add(givenCase);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit(cases);
        return cases;
    }

    @Override
    public void update(Integer caseId, Double sum) throws SQLException {
        logger.traceEntry("Finding all cases");
        Connection connection = dbUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("update Cases set TotalSumDonated = ? where Cid = ?");
        preparedStatement.setDouble(1, sum);
        preparedStatement.setInt(2, caseId);
        preparedStatement.executeUpdate();
        logger.traceExit();
    }
}
