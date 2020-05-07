package services;

import model.entities.Case;
import repos.utils.ICaseRepository;

import java.sql.SQLException;

public class CaseService {
    ICaseRepository caseRepository;

    public CaseService(ICaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public Case findOne(Integer id) {
        return caseRepository.findOne(id);
    }

    public Iterable<Case> findAll() {
        return caseRepository.findAll();
    }

    public void updateCase(Integer caseId, Double sumToAdd) throws SQLException {
        Double sum = caseRepository.findOne(caseId).getTotalSumDonated();
        sum += sumToAdd;
        caseRepository.update(caseId, sum);
    }
}
