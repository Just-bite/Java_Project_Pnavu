package com.example.restservice.aspect;

import com.example.restservice.service.VisitCounterService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestAttributes;

@Aspect
@Component
public class RequestCountingAspect {
    private final VisitCounterService visitCounterService;

    @Autowired
    public RequestCountingAspect(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @Before("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void countRequest(JoinPoint joinPoint) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String url = (String) attributes.getAttribute(
                    "org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping",
                    RequestAttributes.SCOPE_REQUEST);

            if (url != null) {
                visitCounterService.incrementCounter(url);
            }
        }
    }
}