package DAOs.Product;

import Entities.Product;

import java.util.List;

public interface IProductDAO {
    void addProduct(String name, String description, double price, int quantity);

    void updateProduct(int id, String name, String description, double price, int quantity);

    List<Product> getAllProducts();

    void deleteProduct(int id);
}
