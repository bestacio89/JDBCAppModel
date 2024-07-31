package DAOs.Bon;

import Entities.Bon;

import java.sql.SQLException;
import java.util.List;

public interface IBonDAO {
    void addBon(Bon bon) throws SQLException;

    void updateBon(Bon bon) throws SQLException;

    void deleteBon(int id) throws SQLException;

    Bon getBon(int id) throws SQLException;

    List<Bon> getAllBons() throws SQLException;
}
