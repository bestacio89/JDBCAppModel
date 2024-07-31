package DAOs.Compo;

import Entities.Compo;

import java.sql.SQLException;
import java.util.List;

public interface ICompoDAO {
    void addCompo(Compo compo) throws SQLException;

    void updateCompo(Compo compo) throws SQLException;

    void deleteCompo(int id) throws SQLException;

    Compo getCompo(int id) throws SQLException;

    List<Compo> getAllCompos() throws SQLException;
}
