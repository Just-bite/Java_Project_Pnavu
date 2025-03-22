package com.example.restservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Логирование перед выполнением метода
    @Before("execution(* com.example.restservice..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.atInfo()
                    .setMessage("Вызов метода: {} с аргументами: {}")
                    .addArgument(joinPoint.getSignature().toShortString())
                    .addArgument(joinPoint::getArgs)
                    .log();
        }
    }

    // Логирование после успешного выполнения метода
    @AfterReturning(pointcut = "execution(* com.example.restservice..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            logger.atInfo()
                    .setMessage("Метод {} успешно выполнен. Результат: {}")
                    .addArgument(joinPoint.getSignature().toShortString())
                    .addArgument(() -> result != null ? result.toString() : "null")
                    .log();
        }
    }

    // Логирование при возникновении ошибки
    @AfterThrowing(pointcut = "execution(* com.example.restservice..*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        if (logger.isErrorEnabled()) {
            logger.atError()
                    .setMessage("Ошибка в методе: {}, ошбика {}")
                    .addArgument(joinPoint.getSignature().toShortString())
                    .addArgument(error)
                    .log();
        }
    }
}