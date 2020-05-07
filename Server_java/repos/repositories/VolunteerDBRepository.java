package repos.repositories;

import model.entities.Volunteer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repos.utils.IVolunteerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class VolunteerDBRepository implements IVolunteerRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public VolunteerDBRepository(Properties properties) {
        logger.info("Initializing VolunteerRepository with properties: {} ", properties);
        dbUtils = new JdbcUtils(properties);
    }

    public Volunteer findOne(String username, String password) {
        logger.traceEntry("finding volunteers with username {} and password {} ", username, password);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from Volunteers where Username = ? and Password = ?")){
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try(ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    int vid = result.getInt("Vid");
                    Volunteer volunteer = new Volunteer(vid, username, password);
                    logger.traceExit(volunteer);
                    return volunteer;
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No volunteer found with username {} and password {} ", username);
        return null;
    }

}
