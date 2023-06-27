package com.panov.store.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * This class initializes DelegatingFilterProxy that will manage SecurityFilterChain
 * beans in the servlet container's filter chain.
 *
 * @author Maksym Panov
 * @version 1.0
 */
@Configuration
public class DelegatingFilterProxyRegistrar extends AbstractSecurityWebApplicationInitializer {
}
