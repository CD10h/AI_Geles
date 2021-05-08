package lt.aigen.geles.interceptors;

import lt.aigen.geles.models.RequestLog;
import lt.aigen.geles.repositories.ConfigurationRepository;
import lt.aigen.geles.repositories.RequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class RequestLoggerInterceptor implements HandlerInterceptor {
    Date requestDate;

    @Autowired
    RequestLogRepository requestLogRepository;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        requestDate = new Date();
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        Boolean loggingEnabled = configurationRepository.findConfigByKey("requestLoggingEnabled").map(configuration -> {
            if (configuration.getValue().equals("true")){
                return true;
            }
            return false;
        }).orElse(false);

        if (loggingEnabled) {
            String username = null;
            try {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("user")){
                        username = cookie.getValue();
                        break;
                    }
                }
            } catch (NullPointerException e) {
            }

            if (username != null) {
                requestLogRepository.save(new RequestLog(username, requestDate,
                    request.getRequestURL().toString(), request.getMethod()));
            }
        }
    }
}
