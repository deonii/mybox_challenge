package deonii.mybox.functions;

import deonii.mybox.data.dao.SessionDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.entity.SessionEntity;
import deonii.mybox.data.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;

@Component
public class UserFunctions {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SessionDAO sessionDAO;

    @Value("${session.cookie.name}")
    public String cookieName;

    public SessionEntity createSession(UserEntity user, HttpServletResponse response) {
        SessionEntity sessionEntity = new SessionEntity(user);
        SessionEntity saveSession = sessionDAO.saveSession(sessionEntity);

        Cookie mySessionCookie = new Cookie(cookieName, saveSession.getSessionId().toString());
        mySessionCookie.setMaxAge(1000 * 60 * 60 * 24 * 7);
        response.addCookie(mySessionCookie);
        return saveSession;
    }

    public void deleteSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie mySessionCookie = findCookie(request);
        if(mySessionCookie == null) return;
        String stringSessionId = mySessionCookie.getValue();
        UUID sessionId = UUID.fromString(stringSessionId);
        SessionEntity sessionEntity = sessionDAO.findBySessionId(sessionId);
        if(sessionEntity == null) return;
        sessionDAO.deleteSession(sessionEntity);
        expireCookie(response);
    }

    public Cookie findCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }

    public void expireCookie(HttpServletResponse response) {
        Cookie deleteCookie = new Cookie(cookieName, null);
        deleteCookie.setMaxAge(0);
        response.addCookie(deleteCookie);
    }
}
