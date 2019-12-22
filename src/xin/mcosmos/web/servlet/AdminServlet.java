package xin.mcosmos.web.servlet;

import com.google.gson.Gson;
import xin.mcosmos.domain.Category;
import xin.mcosmos.domain.Order;
import xin.mcosmos.domain.Product;
import xin.mcosmos.service.AdminService;
import xin.mcosmos.service.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminServlet",
        value = "/admin")
public class AdminServlet extends BaseServlet {

    public void findAllCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AdminService service = new AdminService();
        List<Category> category = service.findAllCategory();
        /*Gson gson = new Gson();
        String s = gson.toJson(category);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(s);*/
        request.setAttribute("category", category);

        request.getRequestDispatcher("/admin/category/list.jsp").forward(request, response);

    }
    public void findAllCategoryShowList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AdminService service = new AdminService();
        List<Category> category = service.findAllCategory();
       Gson gson = new Gson();
        String s = gson.toJson(category);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(s);

    }

    public void showProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AdminService service = new AdminService();
        List<Product> productList = service.findProduct();
        request.setAttribute("productList", productList);

        request.getRequestDispatcher("/admin/product/list.jsp").forward(request, response);
    }

    public void findAllOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AdminService service = new AdminService();
        List<Order> orderList = service.findAllOrder();


        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);

    }

    public void findOrderByOid(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String oid = request.getParameter("oid");

        AdminService service = new AdminService();
        List<Map<String, Object>> list = service.findOrderByOid(oid);

        Gson gson = new Gson();
        String s = gson.toJson(list);
        System.out.println(s);
        response.setContentType("text/html;charset=UTF-8");
        
        response.getWriter().write(s);
    }

}