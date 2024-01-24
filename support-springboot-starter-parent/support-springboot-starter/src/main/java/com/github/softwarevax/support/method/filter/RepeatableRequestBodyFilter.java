package com.github.softwarevax.support.method.filter;

import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RepeatableRequestBodyFilter implements Filter {

    private StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();

    @Override
    public void init(FilterConfig filterConfiguration) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain)throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (multipartResolver.isMultipart(request)) {
            request = multipartResolver.resolveMultipart(request);
        } else {
            request = new RepeatableRequestWrapper(request);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
