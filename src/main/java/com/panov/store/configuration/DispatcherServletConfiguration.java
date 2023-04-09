package com.panov.store.configuration;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Class of Dispatcher Servlet configuration.
 *
 * @author Maksym Panov
 * @version 1.0
 */
public class DispatcherServletConfiguration extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    /**
     * Application classes with WEB configuration.
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { WebConfiguration.class };
    }

    /**
     * Specify mappings for Dispatcher Servlet
     */
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}
