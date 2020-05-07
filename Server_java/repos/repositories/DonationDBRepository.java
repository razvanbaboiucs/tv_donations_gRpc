package repos.repositories;

import model.entities.Donation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repos.utils.IDonationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DonationDBRepository implements IDonationRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public DonationDBRepository(Properties properties) {
        logger.info("Initializing DonationRepository with properties: {} ", properties);
        dbUtils = new JdbcUtils(properties);
    }

    public void add(Donation donation) throws SQLException {
        logger.traceEntry("Saving donation {} ", donation);
        Connection connection = dbUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("insert into Donations(Did, Cid, SumDonated) values (?, ?, ?)");
        preparedStatement.setInt(1, donation.getDonorId());
        preparedStatement.setInt(2, donation.getCaseId());
        preparedStatement.setDouble(3, donation.getSumDonated());
        preparedStatement.executeUpdate();
        logger.traceExit();
    }

    public Iterable<Donation> findAll() {
        logger.traceEntry("Finding all donations");
        Connection connection = dbUtils.getConnection();
        List<Donation> donations = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Donations")) {
            try(ResultSet result=preparedStatement.executeQuery()) {
                while (result.next()) {
                    int did = result.getInt("Did");
                    int cid = result.getInt("Cid");
                    Double sum = result.getDouble("SumDonated");
                    Donation donation = new Donation(cid, did, sum);
                    donations.add(donation);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit(donations);
        return donations;
    }
}
