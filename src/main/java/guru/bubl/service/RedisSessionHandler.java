/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lambdaworks.redis.api.sync.RedisStringCommands;
import guru.bubl.module.model.User;
import guru.bubl.module.repository.user.NonExistingUserException;
import guru.bubl.module.repository.user.UserRepository;

import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.NewCookie;
import java.util.StringTokenizer;
import java.util.UUID;

@Singleton
public class RedisSessionHandler implements SessionHandler {

    @Inject
    RedisStringCommands redisStringCommands;

    @Inject
    UserRepository userRepository;

    @Inject
    @Named("isTesting")
    Boolean isTesting;

    @Override
    public void removePersistentSession(String persistentSessionId) {
        if (null == persistentSessionId) {
            return;
        }
        redisStringCommands.set(
                persistentSessionId,
                null
        );
    }

    @Override
    public NewCookie persistSessionForUser(HttpSession session, User user, String xsrfToken) {
        String persistentSessionId = UUID.randomUUID().toString();
        NewCookie jerseyCookie = new NewCookie(
                PERSISTENT_SESSION,
                persistentSessionId,
                "/service",
                isTesting ? null : "mindrespect.com",
                1,
                null,
                Integer.MAX_VALUE,
                !isTesting
        );
        redisStringCommands.set(
                persistentSessionId,
                user.email().trim() + " " + xsrfToken
        );
        return jerseyCookie;
    }

    public Boolean isUserInSession(HttpSession session, String persistentSessionId) {
        if (session.getAttribute(SecurityInterceptor.AUTHENTICATED_USER_KEY) != null) {
            return true;
        }
        if (null == persistentSessionId) {
            return false;
        }
        String stringFromRedis = (String) redisStringCommands.get(persistentSessionId);
        if (null == stringFromRedis) {
            return false;
        }
        StringTokenizer tokensFromRedis = new StringTokenizer(stringFromRedis);
        if (tokensFromRedis.countTokens() != 2) {
            return false;
        }
        String emailFromRedis = tokensFromRedis.nextToken();
        try {
            session.setAttribute(
                    SecurityInterceptor.AUTHENTICATED_USER_KEY,
                    userRepository.findByEmail(emailFromRedis)
            );
            session.setAttribute(
                    SessionHandler.X_XSRF_TOKEN,
                    tokensFromRedis.nextToken()
            );
        } catch (NonExistingUserException exception) {
            return false;
        }
        return true;
    }
}
