package repos.utils;

import model.entities.Donation;

import java.sql.SQLException;

public interface IDonationRepository {
    void add(Donation donation) throws SQLException;
    Iterable<Donation> findAll();
}
