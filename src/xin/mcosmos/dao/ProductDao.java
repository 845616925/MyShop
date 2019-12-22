package xin.mcosmos.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import xin.mcosmos.domain.*;
import xin.mcosmos.utils.DataSourceUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductDao {

    public List<Product> findHotProductList() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

        String sql = "select * from product where is_hot=? limit ?,?";
        return runner.query(sql, new BeanListHandler<Product>(Product.class), 1, 0, 9);

    }

    public List<Product> findNewProductList() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

        String sql = "select * from product order by pdate desc limit ?,?";
        return runner.query(sql, new BeanListHandler<Product>(Product.class), 0, 9);


    }

    public List<Category> findAllCategoryList() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

        String sql = "select * from category ";
        return runner.query(sql, new BeanListHandler<Category>(Category.class));

    }

    public int getCount(String cid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

        String sql = "select count(*) from product where cid=? ";
        Long query = (long) runner.query(sql, new ScalarHandler(), cid);
        return query.intValue();
    }

    public List<Product> findProductByPage(String cid, int index, int currentcount) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

        String sql = "select * from product where cid=? limit ?,? ";

        List<Product> list = runner.query(sql, new BeanListHandler<Product>(Product.class), cid, index, currentcount);
        return list;
    }


    public Product findProductByPid(String pid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());

        String sql = "select * from product where pid=? ";

        Product product = runner.query(sql, new BeanHandler<Product>(Product.class), pid);
        return product;
    }

   

    //向orders插入数据
    public void addOrders(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "insert into orders values(?,?,?,?,?,?,?,?)";
        Connection conn = DataSourceUtils.getConnection();
        runner.update(conn, sql, order.getOid(), order.getOrdertime(), order.getTotal(), order.getState(),
                order.getAddress(), order.getName(), order.getTelephone(), order.getUser().getUid());
    }

    //向orderitem插入数据
    public void addOrderItem(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "insert into orderitem values(?,?,?,?,?)";
        Connection conn = DataSourceUtils.getConnection();
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            runner.update(conn, sql, orderItem.getItemid(), orderItem.getCount(), orderItem.getSubtotal(),
                    orderItem.getProduct().getPid(), orderItem.getOrder().getOid());

        }
    }

    public void updateOrder(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update orders set address=?,name=?,telephone=? where oid=?";
        runner.update(sql, order.getAddress(), order.getName(), order.getTelephone(), order.getOid());

    }

    public int setPayment() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update orders set state=?";
        return runner.update(sql, 1);

    }

    public List<Order> findAllOrders(String uid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from orders where uid=?";
        List<Order> query = runner.query(sql, new BeanListHandler<Order>(Order.class), uid);
        return query;
    }

    public List<Map<String, Object>> findOrderItemByOid(String oid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select i.itemid,i.count,i.subtotal,p.pimage,p.pname,p.shop_price from orderitem i,product p where i.pid=p.pid and i.oid=?";
        List<Map<String, Object>> mapList = runner.query(sql, new MapListHandler(), oid);
        return mapList;
    }
}
