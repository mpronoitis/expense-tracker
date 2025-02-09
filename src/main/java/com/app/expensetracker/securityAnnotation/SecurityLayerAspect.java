package com.app.expensetracker.securityAnnotation;

import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.UserClaimsDTO;
import com.app.expensetracker.error.exception.GenericBadRequestException;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import com.app.expensetracker.shared.rest.enumeration.SecurityQueryEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class SecurityLayerAspect {

    @PersistenceContext
    private EntityManager entityManager;
    //define pointcut. When we use @SecureLayer this pointcut will be activated
    @Pointcut("@annotation(securityLayer)")
    public void executeSecurityLayer(SecurityLayer securityLayer) {}

    @Around("executeSecurityLayer(securityLayer)")
    public Object beforeExecute(ProceedingJoinPoint pjp, SecurityLayer securityLayer) throws Throwable {
        Long idToCheck =  makeSecurityOwnerControl(pjp, securityLayer);
        if (idToCheck == null) {
            return pjp.proceed();
        }
        //check if the authenticated user has the same id with the requested IdToCheck;
        Long idLoggedUser = getLoggedUser();
        List<Long> queryUserIds =  createQuery(securityLayer, idToCheck);
        if (!queryUserIds.contains(idLoggedUser)) {
            throw new GenericBadRequestException("Security Control Error: You try to get a resource that does not belongs to you", ErrorType.IM_SECURITY_CONTROL_ERROR);
        }
        return pjp.proceed();
    }

    private Long makeSecurityOwnerControl(JoinPoint pjp, SecurityLayer securityLayer) {
        Signature signature =  pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        //get parameters from method
        Parameter[] parameters =  method.getParameters();
        Object[] args =  pjp.getArgs();

        //iterate parameters
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(IdToCheck.class)) {
                return returnLongValueOfPassedPatemeter(args[i]);
            }
        }

        throw new InternalAuthenticationServiceException("Method does not contain any IdToCheck parameter");
    }

    private Long returnLongValueOfPassedPatemeter(Object arg) {
        if (arg == null) {
            return null;
        }
        if (arg instanceof Long) {
            return (Long) arg;
        }
        if (arg instanceof String) {
            return Long.parseLong((String) arg);
        }
        throw new InternalAuthenticationServiceException("Method does not contain any IdToCheck parameter");
    }

    private Long getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserClaimsDTO userClaimsDTO = (UserClaimsDTO) authentication.getPrincipal();
        return userClaimsDTO.getId();
    }

    private List<Long> createQuery(SecurityLayer securityLayer, Long idToCheck) {
        SecurityQueryEnum queryEnum = securityLayer.securityQueryEnum();
        Query query =  entityManager.createQuery(queryEnum.getText());
        query.setParameter("id", idToCheck);

        List<Object> resultList =  query.getResultList();
        List<Long> results = new ArrayList<>();
        for (Object o: resultList) {
            Long value = returnLongValueOfPassedPatemeter(o);
            results.add(value);
        }
        return results;
    }

}
