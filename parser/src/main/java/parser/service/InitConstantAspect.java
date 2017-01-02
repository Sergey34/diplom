package parser.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import parser.service.constants.Constant;

@Component
@Aspect
@Slf4j
public class InitConstantAspect {
    @Autowired
    private Constant constants;

    @Before("execution(* parser.service.ParserServiceInt.*(..))")
    public void initConsts(JoinPoint joinPoint) {
        log.debug("initConsts");
        constants.initConst("parser/src/main/resources/WEB-INF/config.properties");
    }
}
