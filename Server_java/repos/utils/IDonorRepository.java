package repos.utils;

import model.entities.Donor;

public interface IDonorRepository {
    Integer add(Donor donor);
    void delete(Integer id);
    void update(Donor donor);
    Donor findOne(Integer id);
    Iterable<Donor> findAll();
    Iterable<Donor> findByName(String name);
}
