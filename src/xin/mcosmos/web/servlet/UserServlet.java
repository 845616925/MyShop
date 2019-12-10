package xin.mcosmos.web.servlet;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import xin.mcosmos.domain.User;
import xin.mcosmos.service.UserService;
import xin.mcosmos.utils.CommonsUtils;
import xin.mcosmos.utils.MailUtils;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@WebServlet(name = "UserServlet", value = "/user")
public class UserServlet extends BaseServlet {

    //登陆
    public void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");


        System.out.println("username:" + username);
        System.out.println("password:" + password);


        UserService service = new UserService();
        User user = service.login(username, password);

        if (user != null) {
            System.out.println("getusername:" + user.getUsername());
            System.out.println("getpassword:" + user.getPassword());
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                if (1 == user.getState()) {
                    System.out.println("State" + user.getState());
                    String autoLogin = request.getParameter("autoLogin");
                    System.out.println(autoLogin);
                    if (autoLogin != null) {
                        Cookie cookie_username = new Cookie("cookie_username", user.getUsername());
                        Cookie cookie_password = new Cookie("cookie_password", user.getPassword());

                        cookie_username.setMaxAge(60 * 60);
                        cookie_password.setMaxAge(60 * 60);

                        cookie_username.setPath(request.getContextPath());
                        cookie_password.setPath(request.getContextPath());

                        response.addCookie(cookie_username);
                        response.addCookie(cookie_password);
                    }

                    session.setAttribute("user", user);
                    response.sendRedirect(request.getContextPath());
                } else {
                    request.setAttribute("loginInfo", "未激活，请先去邮箱激活");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("loginInfo", "账号或密码错误，请重试");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("loginInfo", "登陆失败");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }


    }

    //登出
    public void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("user");

        Cookie cookie_username = new Cookie("cookie_username", "");
        cookie_username.setMaxAge(0);

        Cookie cookie_password = new Cookie("cookie_password", "");
        cookie_password.setMaxAge(0);

        cookie_username.setPath(request.getContextPath());
        cookie_password.setPath(request.getContextPath());

        response.addCookie(cookie_username);
        response.addCookie(cookie_password);

        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;


    }

    //检查用户名
    public void checkUsername(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");

        UserService service = new UserService();
        boolean isExist = service.checkUsername(username);

        String json = "{\"isExist\":" + isExist + "}";
        response.getWriter().write(json);
    }

    public void register(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        InputStream in = UserServlet.class.getClassLoader().getResourceAsStream("mail.properties");
        Properties pro = new Properties();


        Map<String, String[]> parameterMap = request.getParameterMap();
        User user = new User();
        try {

            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class aClass, Object o) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date parse = null;

                    try {
                        parse = format.parse(o.toString());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return parse;
                }
            }, Date.class);

            BeanUtils.populate(user, parameterMap);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        //  private String uid;
        user.setUid(CommonsUtils.getUUid());
        // private String telephone;
        user.setTelephone(null);
        //  private int state;
        user.setState(0);
        //  private String code;
        String pin = CommonsUtils.getUUid();
        user.setCode(pin);

        UserService service = new UserService();
        boolean isRegisterSuccess = false;
        try {
            isRegisterSuccess = service.register(user);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (isRegisterSuccess) {
            //成功
            try {
                pro.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String property = pro.getProperty("mail.url");
            // http://localhost:8000/MyShop/user?method=active&pin=39f9946d-481d-41e2-badc-dc9c0d021118
            String emailMsg = "恭喜您,注册成功！请点击下面的连接激活账户。" +
                    "<br /><a href='http://" + property + "/MyShop/user?method=active&pin=" + pin + "'>" +
                    "http://" + property + "/MyShop/user?method=active&pin=" + pin + "</a>";
            try {
                MailUtils.sendMail(user.getEmail(), emailMsg);

            } catch (MessagingException e) {
                e.printStackTrace();
            }

            response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");

        } else {
            //失败
            response.sendRedirect(request.getContextPath() + "/registerFail.jsp");

        }
    }

    public void active(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String pin = request.getParameter("pin");
        System.out.println(pin);

        UserService service = new UserService();
        boolean isChange = false;
        try {
            isChange = service.changeState(pin);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (isChange) {
            response.sendRedirect(request.getContextPath());
        } else {
            response.getWriter().write("激活失败，请重试！");
        }
    }

}
