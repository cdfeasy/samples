package facebookbot.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by d.asadullin on 14.07.2016.
 */
@Scope("singleton")
@Component
public class SessionService {
    Cache<String, Session> cache = CacheBuilder.newBuilder()
            .maximumSize(10000).expireAfterAccess(3, TimeUnit.DAYS).build();

    public SessionService() {
    }

    public Session get(String id) {
        Session session = cache.getIfPresent(id);
        if (session == null) {
            session = new Session(id);
            session.setIsNew(true);
            cache.put(id, session);
        }
        return session;
    }
}
