package com.erp.school.tenant;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenant = request.getHeader(DataSourceConstants.TENANT_ID.value());

        if (tenant != null) {
            TenantContext.setTenant(tenant);  // Set tenant in ThreadLocal or a static context.
        }
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantContext.clear();  // Clear the tenant context to avoid leakage between requests.
    }
}