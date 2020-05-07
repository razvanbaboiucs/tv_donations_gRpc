package repos.utils;

import model.entities.Volunteer;

public interface IVolunteerRepository {
    Volunteer findOne(String username, String password);
}
