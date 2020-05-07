package services;

import model.entities.Donor;
import repos.utils.IDonorRepository;

public class DonorService {
    private IDonorRepository donorRepository;

    public DonorService(IDonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    public Integer addDonor(String name, String address, String phoneNumber) {
        Donor donor = new Donor(name, address, phoneNumber, 0);
        return donorRepository.add(donor);
    }

    public Donor findOne(Integer id) {
        return donorRepository.findOne(id);
    }

    public Iterable<Donor> findAll() {
        return donorRepository.findAll();
    }

    public Iterable<Donor> findDonorByName(String name) {
        return donorRepository.findByName(name);
    }
}
