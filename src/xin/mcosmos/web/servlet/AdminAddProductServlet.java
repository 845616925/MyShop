package xin.mcosmos.web.servlet;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import sun.nio.ch.IOUtil;
import xin.mcosmos.domain.Category;
import xin.mcosmos.domain.Product;
import xin.mcosmos.service.AdminService;
import xin.mcosmos.utils.CommonsUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminAddProductServlet",
        value = "/adminAddProduct")
public class AdminAddProductServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Product product = new Product();
        Map<String, Object> map = new HashMap<>();
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();

            ServletFileUpload upload = new ServletFileUpload(factory);


            List<FileItem> parseRequest = upload.parseRequest(request);
            for (FileItem item : parseRequest) {
                boolean formField = item.isFormField();
                if (formField) {
                    //封装到Product
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString("UTF-8");
                    map.put(fieldName, fieldValue);
                } else {
                    String name = item.getName();
                    String path = this.getServletContext().getRealPath("upload");
                    InputStream in = item.getInputStream();
                    OutputStream out = new FileOutputStream(path + "/" + name);
                    IOUtils.copy(in, out);
                    out.close();
                    in.close();
                    item.delete();


                    map.put("pimage", "upload/" + name);
                }

            }

            BeanUtils.populate(product, map);

            //  private String pid;
            product.setPid(CommonsUtils.getUUid());
            //  private Date pdate;
            product.setPdate(new Date());
            //  private int pflag;
            product.setPflag(1);
            //  private Category category;
            Category category = new Category();
            category.setCid(map.get("cid").toString());
            product.setCategory(category);


            AdminService service = new AdminService();
            service.saveProduct(product);


            request.getRequestDispatcher("/admin?method=showProduct").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}