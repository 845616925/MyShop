package xin.mcosmos.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import xin.mcosmos.domain.Category;
import xin.mcosmos.domain.Order;
import xin.mcosmos.domain.Product;
import xin.mcosmos.utils.DataSourceUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminDao {
    public List<Category> findAllCategory() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from category";
        List<Category> query = runner.query(sql, new BeanListHandler<Category>(Category.class));
        return query;
    }

    public void saveProduct(Product product) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into product values(?,?,?,?,?,?,?,?,?,?)";
        runner.update(sql, product.getPid(), product.getPname(), product.getMarket_price(),
                product.getShop_price(), product.getPimage(), product.getPdate(),
                product.getIs_hot(), product.getPdesc(), product.getPflag(),
                product.getCategory().getCid());
    }

    public List<Product> findProduct() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product";
        List<Product> query = runner.query(sql, new BeanListHandler<Product>(Product.class));
        return query;
    }

    public List<Order> findAllOrder() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from orders";
        List<Order> query = runner.query(sql, new BeanListHandler<Order>(Order.class));
        return query;
    }

    public List<Map<String, Object>> findOrderByOid(String oid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select p.pimage,p.pname,p.shop_price,i.count,i.subtotal " +
                "from orderitem i,product p " +
                "where i.pid=p.pid and i.oid=?";
        return runner.query(sql, new MapListHandler(), oid);
    }
}
