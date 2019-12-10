package xin.mcosmos.service;

import xin.mcosmos.dao.ProductDao;
import xin.mcosmos.domain.*;
import xin.mcosmos.utils.DataSourceUtils;

import java.awt.geom.FlatteningPathIterator;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductService {

    //获得热门商品
    public List<Product> findHotProductList() {
        ProductDao dao = new ProductDao();
        List<Product> hotProductList = null;
        try {
            hotProductList = dao.findHotProductList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hotProductList;
    }

    //获得最新商品
    public List<Product> findNewProductList() {

        ProductDao dao = new ProductDao();
        List<Product> newProductList = null;
        try {
            newProductList = dao.findNewProductList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newProductList;
    }

    public List<Category> findAllCetegoryList() {
        ProductDao dao = new ProductDao();
        List<Category> categoryList = null;
        try {
            categoryList = dao.findAllCategoryList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryList;
    }

    public PageBean findProductListByCid(String cid, int currentPage, int currentCount) {

        ProductDao dao = new ProductDao();

        PageBean<Product> pageBean = new PageBean<>();
        //当前页
        pageBean.setCurrentPage(currentPage);
        //每页显示的条数
        pageBean.setCurrentCount(currentCount);
        //总条数
        int totalCount = 0;
        try {
            totalCount = dao.getCount(cid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setTotalCount(totalCount);
        //总数
        int totalPage = (int) Math.ceil(1.0 * totalCount / currentCount);
        pageBean.setTotalPage(totalPage);

        //当前页显示的数据 
        int index = (currentPage - 1) * currentCount;
        List<Product> list = null;
        try {
            list = dao.findProductByPage(cid, index, currentCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setList(list);

        return pageBean;
    }

    public Product findProductByPid(String pid) {
        ProductDao dao = new ProductDao();
        Product product = null;
        try {
            product = dao.findProductByPid(pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

   

    //提交订单，将订单存储到数据库
    public void submitOrder(Order order) {
        ProductDao dao = new ProductDao();
        try {
            //开事务
            DataSourceUtils.startTransaction();
            //调用dao存储order数据的方法
            dao.addOrders(order);
            //调用dao存储orderItem数据的方法
            dao.addOrderItem(order);

        } catch (SQLException e) {
            try {
                DataSourceUtils.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                DataSourceUtils.commitAndRelease();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public void updateOrder(Order order) {
        ProductDao dao = new ProductDao();
        try {
            dao.updateOrder(order);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean setPayment() {
        ProductDao dao = new ProductDao();
        int i = 0;
        try {
            i = dao.setPayment();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i > 0 ? true : false;

    }

    //获取指定用户订单
    public List<Order> findAllOrders(String uid) {
        ProductDao dao = new ProductDao();
        List<Order> allOrders = null;
        try {
            allOrders = dao.findAllOrders(uid);
           
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allOrders;
    }

    public List<Map<String, Object>> findOrderItemByOid(String oid) {
        ProductDao dao = new ProductDao();
        List<Map<String, Object>> mapList = null;
        try {
            mapList = dao.findOrderItemByOid(oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapList;

    }
}
