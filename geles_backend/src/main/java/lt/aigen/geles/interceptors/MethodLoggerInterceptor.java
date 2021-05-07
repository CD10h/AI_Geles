package lt.aigen.geles.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class MethodLoggerInterceptor implements HandlerInterceptor {
    Date requestDate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        requestDate = new Date();
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        System.out.println(request.getMethod());
        System.out.println(request.getRequestURL());
        System.out.println(requestDate);
        try {
            //what to do if more than one cookie exists
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("name"))
                    System.out.println(cookie.getValue());
            }
        } catch (NullPointerException e){
        }
    }
}
