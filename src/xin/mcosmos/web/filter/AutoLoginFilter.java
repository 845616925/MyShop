package xin.mcosmos.web.filter;

import xin.mcosmos.domain.User;
import xin.mcosmos.service.UserService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AutoLoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String cookie_username = null;
        String cookie_password = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("cookie_username".equals(cookie.getName())) {
                    cookie_username = cookie.getValue();

                }
                if ("cookie_password".equals(cookie.getName())) {
                    cookie_password = cookie.getValue();
                }
            }

            if (cookie_username != null && cookie_password != null) {
                UserService service = new UserService();
                User user = service.login(cookie_username, cookie_password);
                request.getSession().setAttribute("user", user);
            }

        }

        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

    @Override
    public void destroy() {
    }

}
