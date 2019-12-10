package xin.mcosmos.service;

import xin.mcosmos.dao.AdminDao;
import xin.mcosmos.domain.Category;
import xin.mcosmos.domain.Order;
import xin.mcosmos.domain.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminService {


    public List<Category> findAllCategory() {
        AdminDao dao = new AdminDao();
        try {
            return dao.findAllCategory();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void saveProduct(Product product) {
        try {
            AdminDao dao = new AdminDao();
            dao.saveProduct(product);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> findProduct() {
        List<Product> product = null;
        AdminDao dao = new AdminDao();
        try {
            product = dao.findProduct();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    public List<Order> findAllOrder() {
        AdminDao dao = new AdminDao();
        List<Order> orderList = null;
        try {
            orderList = dao.findAllOrder();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    public List<Map<String, Object>> findOrderByOid(String oid) {
        AdminDao dao = new AdminDao();
        List<Map<String, Object>> list = null;
        try {
            list = dao.findOrderByOid(oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
