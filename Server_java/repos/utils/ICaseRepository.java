package repos.utils;

import model.entities.Case;

import java.sql.SQLException;

public interface ICaseRepository {
    Case findOne(Integer id);
    Iterable<Case> findAll();
    void update(Integer caseId, Double sum) throws SQLException;
}
