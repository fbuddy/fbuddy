package org.thingsboard.server.config;

import org.slf4j.Logger;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class LoggingFilter extends GenericFilterBean {
    private final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
    /**
     * It's important that you actually register your filter this way rather then just annotating it
     * as @Component as you need to be able to set for which "DispatcherType"s to enable the filter
     * (see point *1*)
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<LoggingFilter> initFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());

        // *1* make sure you sett all dispatcher types if you want the filter to log upon
        registrationBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));

        // *2* this should put your filter above any other filter
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registrationBean;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ContentCachingRequestWrapper wreq =
                new ContentCachingRequestWrapper(
                        (HttpServletRequest) request);

        ContentCachingResponseWrapper wres =
                new ContentCachingResponseWrapper(
                        (HttpServletResponse) response);

        try {



            // let it be ...
            chain.doFilter(wreq, wres);
            // makes sure that the input is read (e.g. in 404 it may not be)
            while (wreq.getInputStream().read() >= 0);
            Map<String, String> headers = new HashMap<>(0);
            Enumeration<String> headerNames = wreq.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (headerName != null) {
                    headers.put(headerName, wreq.getHeader(headerName));
                }
            }
            log.info("API REQUEST "+ wreq.getMethod() +": " + wreq.getRequestURI() + ", Request Headers" + headers + ", data:" + new String(wreq.getContentAsByteArray()) +
                    ", Response: " + new String(wres.getContentAsByteArray()));
            // this is specific of the "ContentCachingResponseWrapper" we are relying on,
            // make sure you call it after you read the content from the response
            wres.copyBodyToResponse();

            // One more point, in case of redirect this will be called twice! beware to handle that
            // somewhat

        } catch (Throwable t) {
            // Do whatever logging you whish here, too
            // here you should also be logging the error!!!
            throw t;
        }

    }
}
