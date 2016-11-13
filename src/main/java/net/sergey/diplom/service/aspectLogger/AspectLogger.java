package net.sergey.diplom.service.aspectLogger;

import net.sergey.diplom.service.utils.UtilsLogger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class AspectLogger {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(UtilsLogger.getStaticClassName() + "MethodLoggerServiceImpl");

    @After("execution(* net.sergey.diplom.service.ServiceInt.*(..))")
    public void log(JoinPoint joinPoint) {
        if (UtilsLogger.getAuthentication() != null) {
            String name = UtilsLogger.getAuthentication().getName();
            LOGGER.trace("User '{}' run method '{}'", name, joinPoint.getSignature());
        }

    }
}
