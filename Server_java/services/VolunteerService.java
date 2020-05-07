package services;

import model.entities.Volunteer;
import repos.utils.IVolunteerRepository;

public class VolunteerService {
    IVolunteerRepository volunteerRepository;

    public VolunteerService(IVolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    public Volunteer findVolunteer(String username, String password) {
        return volunteerRepository.findOne(username, password);
    }
}
