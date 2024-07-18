package com.backend.project.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThrottlingFilter implements Filter {

    private final ProjectConfig config;

    @Autowired
    ThrottlingFilter(ProjectConfig config) {
        this.config = config;
    }

    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(config.getBucketCapacity()).refillGreedy(config.getRefillRate(), Duration.ofSeconds(config.getRefillPeriod())))
                .build();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (config.getDisableRateLimiting()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpSession session = httpRequest.getSession(true);
        System.out.println("Inside Bucket");
        String appKey = "Speer";//SecurityUtils.getThirdPartyAppKey();
        Bucket bucket = (Bucket) session.getAttribute("throttler-" + appKey);
        if (bucket == null) {
            bucket = createNewBucket();
            session.setAttribute("throttler-" + appKey, bucket);
        }

//        // tryConsume returns false immediately if no tokens available with the bucket
//        if (bucket.tryConsume(1)) {
//            // the limit is not exceeded
//            filterChain.doFilter(servletRequest, servletResponse);
//        } else {
//            // limit is exceeded
//            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
//            httpResponse.setContentType("text/plain");
//            httpResponse.setStatus(429);
//            httpResponse.getWriter().append("Too many requests");
//        }
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            // the limit is not exceeded
            httpResponse.setHeader("X-Rate-Limit-Remaining", "" + probe.getRemainingTokens());
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // limit is exceeded
//            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setStatus(429);
            httpResponse.setHeader("X-Rate-Limit-Retry-After-Seconds", "" + TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
            httpResponse.setContentType("text/plain");
            httpResponse.getWriter().append("Too many requests");
        }
    }
}
