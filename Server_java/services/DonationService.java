package services;

import model.entities.Donation;
import repos.utils.IDonationRepository;

import java.sql.SQLException;

public class DonationService {
    IDonationRepository donationRepository;

    public DonationService(IDonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public void add(Integer donorId, Integer caseId, Double sumDonated) throws SQLException {
        Donation donation = new Donation(caseId, donorId, sumDonated);
        donationRepository.add(donation);
    }
}
