package DAOs.User;

import Entities.User;

import java.util.List;

public interface IUserDAO {
    void addUser(String name, String email, String discordName, String linkedinUrl);

    void updateUser(int id, String name, String email, String discordName, String linkedinUrl);

    List<User> getAllUsers();

    void deleteUser(int id);
}
