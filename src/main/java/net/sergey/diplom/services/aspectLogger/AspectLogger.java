package net.sergey.diplom.services.aspectLogger;

import net.sergey.diplom.services.utils.UtilsLogger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Aspect
public class AspectLogger {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    @After("execution(* net.sergey.diplom.services.ServiceInt.*(..))")
    public void log(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String name = authentication.getName();
            LOGGER.trace("User '{}' run method '{}'", name, joinPoint.getSignature());
        }

    }
}
