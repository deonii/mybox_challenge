package deonii.mybox.interceptor;

import deonii.mybox.data.dao.SessionDAO;
import deonii.mybox.data.entity.SessionEntity;
import deonii.mybox.functions.UserFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserFunctions userFunctions;

    @Autowired
    private SessionDAO sessionDAO;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        Cookie cookie = userFunctions.findCookie(request);
        if(cookie == null) {
            request.setAttribute("userUuid", null);
            return true;
        }

        String stringSessionId = cookie.getValue();
        UUID sessionId = UUID.fromString(stringSessionId);
        SessionEntity sessionEntity = sessionDAO.findBySessionId(sessionId);
        if(sessionEntity == null) {
            request.setAttribute("userUuid", null);
            return true;
        }

        UUID userUuid = sessionEntity.getUserUuid();
        request.setAttribute("userUuid", userUuid);
        return true;
    }
}
