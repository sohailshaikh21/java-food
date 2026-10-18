package foodie_backend.foodie_backend.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* foodie_backend.foodie_backend.controller..*(..))")
    public Object logControllerCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            String controller = joinPoint.getSignature().getDeclaringType().getSimpleName();
            String method = joinPoint.getSignature().getName();
            log.info("{}.{} took {} ms", controller, method, System.currentTimeMillis() - start);
        }
    }
}
