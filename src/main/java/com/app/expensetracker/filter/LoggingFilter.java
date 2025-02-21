package com.app.expensetracker.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

@Aspect
@Component
public class LoggingFilter {

    private static Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Around("execution(* com.app.expensetracker.controller..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // get http request and the respons
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed(); // if attributes are null, just proceed
        }

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        // pre-request logging
        String clientIP = request.getRemoteAddr();
        logger.info("Client IP: {}, Request URL: {}, Method: {}", clientIP, request.getRequestURL(), request.getMethod());


        // Proceed with the method execution
        Object result = joinPoint.proceed();

        // post-response logging
        if (response != null) {
            logger.info("Response status: {}", response.getStatus());
        }

        return result;
    }

}
