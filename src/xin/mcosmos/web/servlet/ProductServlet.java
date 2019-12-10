package xin.mcosmos.web.servlet;

import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;
import redis.clients.jedis.Jedis;
import xin.mcosmos.domain.*;
import xin.mcosmos.service.ProductService;
import xin.mcosmos.utils.CommonsUtils;
import xin.mcosmos.utils.JedisPoolUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "ProductServlet",
        value = "/product")
public class ProductServlet extends BaseServlet {


    //获取商品列表
    public void categoryList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductService service = new ProductService();

        Jedis jedis = JedisPoolUtils.getJedis();
        String categoryListJson = jedis.get("categoryListJson");

        //判断是否为空
        if (categoryListJson == null) {
            System.out.println("缓存没有数据，查询数据库");
            List<Category> categoryList = service.findAllCetegoryList();
            Gson gson = new Gson();
            categoryListJson = gson.toJson(categoryList);

            jedis.set("categoryListJson", categoryListJson);
        }
       /* List<Category> categoryList = service.findAllCetegoryList();
        Gson gson = new Gson();
        String categoryListJson = gson.toJson(categoryList);*/
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(categoryListJson);

    }


    //显示首页商品
    public void index(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductService service = new ProductService();
        List<Product> hotProductList = service.findHotProductList();
        List<Product> newProductList = service.findNewProductList();

        request.setAttribute("hotProductList", hotProductList);
        request.setAttribute("newProductList", newProductList);

        request.getRequestDispatcher("/index.jsp").forward(request, response);

    }


    //显示商品的详细功能
    public void productInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //获得当前页
        String currentPage = request.getParameter("currentPage");
        //获得商品类别
        String cid = request.getParameter("cid");

        //获得商品PID
        String pid = request.getParameter("pid");

        ProductService service = new ProductService();
        Product product = service.findProductByPid(pid);

        /*   List<Product> history*/

        request.setAttribute("product", product);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("cid", cid);

        //获得客户端携带的cookie
        String pids = pid;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    pids = cookie.getValue();

                    String[] split = pids.split("-");
                    List<String> strings = Arrays.asList(split);
                    LinkedList<String> list = new LinkedList(strings);
                    //是否存在当前pid
                    if (list.contains(pid)) {
                        //包含当前商品
                        list.remove(pid);
                    }
                    list.addFirst(pid);

                    StringBuffer buffer = new StringBuffer();

                    for (int i = 0; i < list.size(); i++) {
                        buffer.append(list.get(i));
                        buffer.append("-");
                    }
                    pids = buffer.substring(0, buffer.length() - 1);

                }
            }
        }
        Cookie cookie_pids = new Cookie("pids", pids);
        response.addCookie(cookie_pids);


        //转发之前 创建cookie存储pid


        request.getRequestDispatcher("/product_info.jsp").forward(request, response);


    }

    //根据商品的类别获取商品列表
    public void productListByCid(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String cid = request.getParameter("cid");
        String currentPageStr = request.getParameter("currentPage");

        if (currentPageStr == null) currentPageStr = "1";

        int currentPage = Integer.parseInt(currentPageStr);
        int currentCount = 12;

        ProductService service = new ProductService();
        PageBean pageBean = service.findProductListByCid(cid, currentPage, currentCount);

        request.setAttribute("pageBean", pageBean);
        request.setAttribute("cid", cid);

        //定义一个集合记录历史商品信息
        List<Product> historyProductList = new ArrayList<>();


        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    String pids = cookie.getValue();
                    String[] split = pids.split("-");
                    for (String pid : split) {
                        Product product = service.findProductByPid(pid);
                        historyProductList.add(product);
                    }

                }
            }
        }

        request.setAttribute("historyProductList", historyProductList);

        request.getRequestDispatcher("/product_list.jsp").forward(request, response);
    }

    //将商品添加到购物车
    public void addProductToCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String pid = request.getParameter("pid");
        int buyNum = Integer.parseInt(request.getParameter("buyNum"));

        ProductService service = new ProductService();
        Product product = service.findProductByPid(pid);

        double subtotal = product.getShop_price() * buyNum;
        //封装

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setBuyNum(buyNum);
        item.setSubtotal(subtotal);

        //获得购物车---是否在session已经存在
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null) {
            cart = new Cart();

        }
        Map<String, CartItem> cartItems = cart.getCartItems();
        double newSubtotal = 0.0;
        if (cartItems.containsKey(pid)) {
            CartItem cartItem = cartItems.get(pid);
            int oldBuyNum = cartItem.getBuyNum();
            oldBuyNum += buyNum;
            cartItem.setBuyNum(oldBuyNum);
            cart.setCartItems(cartItems);
            //修改小计
            //原先商品的小计
            double oldSubTotal = cartItem.getSubtotal();
            //新买的小计
            newSubtotal = buyNum * product.getShop_price();
            cartItem.setSubtotal(oldSubTotal + newSubtotal);
        } else {
            cart.getCartItems().put(product.getPid(), item);
            newSubtotal = buyNum * product.getShop_price();
        }

        double total = cart.getTotal() + newSubtotal;
        cart.setTotal(total);

        session.setAttribute("cart", cart);

        //跳转到购物车页面
        // request.getRequestDispatcher("/cart.jsp").forward(request, response);

        response.sendRedirect(request.getContextPath() + "/cart.jsp");


    }

    //删除单一商品
    public void delProFromCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pid = request.getParameter("pid");
        //删除session的商品
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            Map<String, CartItem> cartItems = cart.getCartItems();
            //需要修改总价
            cart.setTotal(cart.getTotal() - cartItems.get(pid).getSubtotal());

            cartItems.remove(pid);
            cart.setCartItems(cartItems);


        }

        session.setAttribute("cart", cart);

        response.sendRedirect(request.getContextPath() + "/cart.jsp");

    }

    //清空购物车
    public void clean(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("cart");

        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }

    //提交订单
    public void submitOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        //判断是否登陆
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Order order = new Order();
//      private String oid;
        String oid = CommonsUtils.getUUid();
        order.setOid(oid);
//      private Date ordertime;
        order.setOrdertime(new Date());
//      private double total;
        Cart cart = (Cart) session.getAttribute("cart");
        double total = cart.getTotal();
        order.setTotal(total);
//      private int state;//支付状态
        order.setState(0);
//      private String address;
        order.setAddress(null);
//      private String name;
        order.setName(null);
//      private String telephone;
        order.setTelephone(null);
//      private User user;
        order.setUser(user);
//      List<OrderItem> orderItems = new ArrayList<OrderItem>();        
        Map<String, CartItem> cartItems = cart.getCartItems();
        for (Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
            CartItem cartItem = entry.getValue();
            OrderItem orderItem = new OrderItem();
//          String itemid;
            orderItem.setItemid(CommonsUtils.getUUid());
//          int count;
            orderItem.setCount(cartItem.getBuyNum());
//          double subtotal;
            orderItem.setSubtotal(cartItem.getSubtotal());
//          Product product;
            orderItem.setProduct(cartItem.getProduct());
//          Order order;
            orderItem.setOrder(order);

            order.getOrderItems().add(orderItem);
        }

        ProductService service = new ProductService();
        service.submitOrder(order);


        session.setAttribute("order", order);

        response.sendRedirect(request.getContextPath() + "/order_info.jsp");
    }

    //确认订单 更新收货人信息+在线支付
    public void confirmOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //更新
        HttpSession session = request.getSession();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Order order = new Order();
        try {
            BeanUtils.populate(order, parameterMap);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        ProductService service = new ProductService();
        service.updateOrder(order);

        //支付

        try {
            Thread.sleep(3 * 1000);
            boolean b = service.setPayment();
            if (b) {

                session.removeAttribute("cart");
                response.getWriter().write(" <h1>付款成功！！！</h1></br>仅为测试请勿当真");

                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //获得当前用户订单
    public void myOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        //判断是否登陆
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        ProductService service = new ProductService();

        List<Order> orderList = service.findAllOrders(user.getUid());
        //循环遍历所有订单
        if (orderList != null) {
            for (Order order : orderList) {

                String oid = order.getOid();

                List<Map<String, Object>> mapList = service.findOrderItemByOid(oid);

                for (Map<String, Object> map : mapList) {
                    try {

                        OrderItem orderItem = new OrderItem();

                        //orderItem.setCount(Integer.parseInt(map.get("count").toString()));
                        BeanUtils.populate(orderItem, map);

                        Product product = new Product();

                        BeanUtils.populate(product, map);

                        orderItem.setProduct(product);

                        order.getOrderItems().add(orderItem);


                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }


                }

            }
        }


//        orderList

        request.setAttribute("orderList", orderList);

        request.getRequestDispatcher("/order_list.jsp").forward(request, response);
    }


}