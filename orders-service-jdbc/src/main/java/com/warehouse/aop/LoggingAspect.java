package com.warehouse.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;


//@Aspect
//@Component
public class LoggingAspect {
//    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
//
//    @Pointcut("execution(* com.warehouse.service..*(..))")
//    public void logPointCut() {}
//
//    @Before("logPointCut()")
//    public void logBefore(JoinPoint joinPoint){
//        logger.info("Before Called: {} with args: {}", joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));
//    }
//
//    @After("logPointCut()")
//    public void logAfter(JoinPoint joinPoint) {
//        logger.info("After Called: {} with args: {}", joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));
//    }
//
//    @AfterReturning(pointcut = "logPointCut()", returning = "result")
//    public void logAfter(JoinPoint joinPoint, Object result) {
//        logger.info("Returned from: {} with result: {}", joinPoint.getSignature(), result);
//    }
//
//    @AfterThrowing(pointcut = "logPointCut()", throwing = "ex")
//    public void logException(JoinPoint joinPoint, Throwable ex) {
//        logger.error("Exception in: {} - Message: {}", joinPoint.getSignature(), ex.getMessage());
//    }
//
//    @Around("logPointCut()")
//    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
//        long start = System.currentTimeMillis();
//        Object result = joinPoint.proceed();
//        long duration = System.currentTimeMillis() - start;
//        logger.info("Execution time of {}: {} ms", joinPoint.getSignature(), duration);
//        return result;
//    }
}
