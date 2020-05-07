package repos.repositories;

import model.entities.Donor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repos.utils.IDonorRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DonorDBRepository implements IDonorRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public DonorDBRepository(Properties properties) {
        logger.info("Initializing DonorRepository with properties: {} ", properties);
        dbUtils = new JdbcUtils(properties);
    }

    public Integer add(Donor donor) {
        logger.traceEntry("saving donor {} ", donor);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into Donors(Name, Address, PhoneNumber) values (?, ?, ?)")) {
            preparedStatement.setString(1, donor.getName());
            preparedStatement.setString(2, donor.getAddress());
            preparedStatement.setString(3, donor.getPhoneNumber());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Donors order by Did desc")) {
            try (ResultSet result = preparedStatement.executeQuery()) {
                if(result.next()) {
                    int did = result.getInt("Did");
                    logger.traceExit();
                    return did;
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
        return null;
    }

    public void delete(Integer id) {
        logger.traceEntry("Deleting donor with {}", id);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from Donors where Did = ?")) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    public void update(Donor donor) {
        //To do
        logger.traceEntry("Updating donor {} ", donor);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("update Donors set Name = ?, Address = ?, PhoneNumber = ? where Did = ?")) {
            preparedStatement.setString(1, donor.getName());
            preparedStatement.setString(2, donor.getAddress());
            preparedStatement.setString(3, donor.getPhoneNumber());
            preparedStatement.setInt(4, donor.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    public Donor findOne(Integer id) {
        logger.traceEntry("Finding donors with id {} ", id);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from Donors where Did=?")) {
            preparedStatement.setInt(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    int did = result.getInt("Did");
                    String name = result.getString("Name");
                    String address = result.getString("Address");
                    String phoneNumber = result.getString("PhoneNumber");
                    Donor donor = new Donor(name, address, phoneNumber, did);
                    logger.traceExit(donor);
                    return donor;
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No donor found with id {}", id);
        return null;
    }

    public Iterable<Donor> findAll() {
        logger.traceEntry("Finding all donors");
        Connection connection = dbUtils.getConnection();
        List<Donor> donors = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from Donors")) {
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    int did = result.getInt("Did");
                    String name = result.getString("Name");
                    String address = result.getString("Address");
                    String phoneNumber = result.getString("PhoneNumber");
                    Donor donor = new Donor(name, address, phoneNumber, did);
                    donors.add(donor);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit(donors);
        return donors;
    }

    @Override
    public Iterable<Donor> findByName(String name) {
        logger.traceEntry("Finding donors with name {} ", name);
        Connection connection = dbUtils.getConnection();
        List<Donor> donors = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from Donors where Name like '%" + name + "%'")) {
            try (ResultSet result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    int did = result.getInt("Did");
                    String new_name = result.getString("Name");
                    String address = result.getString("Address");
                    String phoneNumber = result.getString("PhoneNumber");
                    Donor donor = new Donor(new_name, address, phoneNumber, did);
                    donors.add(donor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.traceExit("Finished with {} donors", donors.size());
        return donors;
    }
}
