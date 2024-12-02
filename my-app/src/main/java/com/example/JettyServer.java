package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JettyServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/api");

        // Add Jersey servlet
        ServletHolder servletHolder = new ServletHolder(new ServletContainer());
        servletHolder.setInitParameter("jersey.config.server.provider.packages", "com.example");
        context.addServlet(servletHolder, "/*");

        // Add CORS filter
        FilterHolder corsFilterHolder = new FilterHolder(CORSFilter.class);
        context.addFilter(corsFilterHolder, "/*", null);

        server.setHandler(context);

        server.start();
        server.join();
    }

    public static class CORSFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {}
    
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            if (response instanceof HttpServletResponse) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setHeader("Access-Control-Allow-Origin", "*");
                httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, ngrok-skip-browser-warning");
                httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            }
            chain.doFilter(request, response);
        }
    
        @Override
        public void destroy() {}
    }
}