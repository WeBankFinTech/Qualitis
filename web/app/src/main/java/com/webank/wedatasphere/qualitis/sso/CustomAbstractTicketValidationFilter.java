/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.sso;

import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.ReflectUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;

import javax.net.ssl.HostnameVerifier;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author howeye
 */
public class CustomAbstractTicketValidationFilter extends AbstractCasFilter {


    /** The TicketValidator we will use to validate tickets. */
    private TicketValidator ticketValidator;

    /**
     * Specify whether the filter should redirect the user agent after a
     * successful validation to remove the ticket parameter from the query
     * string.
     */
    private boolean redirectAfterValidation = true;

    /** Determines whether an exception is thrown when there is a ticket validation failure. */
    private boolean exceptionOnValidationFailure = false;

    /**
     * Specify whether the Assertion should be stored in a session
     * attribute {@link AbstractCasFilter#CONST_CAS_ASSERTION}.
     */
    private boolean useSession = true;

    /**
     * Template method to return the appropriate validator.
     *
     * @param filterConfig the FilterConfiguration that may be needed to construct a validator.
     * @return the ticket validator.
     */
    protected TicketValidator getTicketValidator(final FilterConfig filterConfig) throws FileNotFoundException {
        return this.ticketValidator;
    }

    /**
     * Gets the ssl config to use for HTTPS connections
     * if one is configured for this filter.
     * @param filterConfig Servlet filter configuration.
     * @return Properties that can contains key/trust info for Client Side Certificates
     */
    protected Properties getSslConfig(final FilterConfig filterConfig) throws FileNotFoundException {
        final Properties properties = new Properties();
        final String fileName = getPropertyFromInitParams(filterConfig, "sslConfigFile", null);

        if (fileName != null) {
            try (FileInputStream fis = new FileInputStream(fileName);
                 InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                properties.load(inputStreamReader);
                logger.trace("Loaded {} entries from {}", properties.size(), fileName);
            } catch (final IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            } catch (Exception e) {
                logger.error("Failed to get ssl Config, caused by: {}", e.getMessage(), e);
            }
        }
        return properties;
    }

    /**
     * Gets the configured {@link HostnameVerifier} to use for HTTPS connections
     * if one is configured for this filter.
     * @param filterConfig Servlet filter configuration.
     * @return Instance of specified host name verifier or null if none specified.
     */
    protected HostnameVerifier getHostnameVerifier(final FilterConfig filterConfig) {
        final String className = getPropertyFromInitParams(filterConfig, "hostnameVerifier", null);
        logger.trace("Using hostnameVerifier parameter: {}", className);
        final String config = getPropertyFromInitParams(filterConfig, "hostnameVerifierConfig", null);
        logger.trace("Using hostnameVerifierConfig parameter: {}", config);
        if (className != null) {
            if (config != null) {
                return ReflectUtils.newInstance(className, config);
            } else {
                return ReflectUtils.newInstance(className);
            }
        }
        return null;
    }

    @Override
    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        setExceptionOnValidationFailure(parseBoolean(getPropertyFromInitParams(filterConfig,
                "exceptionOnValidationFailure", "false")));
        logger.trace("Setting exceptionOnValidationFailure parameter: {}", this.exceptionOnValidationFailure);
        setRedirectAfterValidation(parseBoolean(getPropertyFromInitParams(filterConfig, "redirectAfterValidation",
                "true")));
        logger.trace("Setting redirectAfterValidation parameter: {}", this.redirectAfterValidation);
        setUseSession(parseBoolean(getPropertyFromInitParams(filterConfig, "useSession", "true")));
        logger.trace("Setting useSession parameter: {}", this.useSession);

        if (!this.useSession && this.redirectAfterValidation) {
            logger.warn("redirectAfterValidation parameter may not be true when useSession parameter is false. Resetting it to false in order to prevent infinite redirects.");
            setRedirectAfterValidation(false);
        }

        try {
            setTicketValidator(getTicketValidator(filterConfig));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        super.initInternal(filterConfig);
    }

    @Override
    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.ticketValidator, "ticketValidator cannot be null.");
    }

    /**
     * Pre-process the request before the normal filter process starts.  This could be useful for pre-empting code.
     *
     * @param servletRequest The servlet request.
     * @param servletResponse The servlet response.
     * @param filterChain the filter chain.
     * @return true if processing should continue, false otherwise.
     * @throws IOException if there is an I/O problem
     * @throws ServletException if there is a servlet problem.
     */
    protected boolean preFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                                final FilterChain filterChain) throws IOException, ServletException {
        return true;
    }

    /**
     * Template method that gets executed if ticket validation succeeds.  Override if you want additional behavior to occur
     * if ticket validation succeeds.  This method is called after all ValidationFilter processing required for a successful authentication
     * occurs.
     *
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     * @param assertion the successful Assertion from the server.
     */
    protected void onSuccessfulValidation(final HttpServletRequest request, final HttpServletResponse response,
                                          final Assertion assertion) {
        // nothing to do here.
    }

    /**
     * Template method that gets executed if validation fails.  This method is called right after the exception is caught from the ticket validator
     * but before any of the processing of the exception occurs.
     *
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     */
    protected void onFailedValidation(final HttpServletRequest request, final HttpServletResponse response) {
        // nothing to do here.
    }

    @Override
    public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                               final FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = HttpUtils.getUserName(httpServletRequest);
        if (username != null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        logger.debug("come to third filter--AbstractTicketValidationFilter");
        if (!preFilter(servletRequest, servletResponse, filterChain)) {
            logger.debug("preFilter is false!");
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String ticket = retrieveTicketFromRequest(request);

        if (CommonUtils.isNotBlank(ticket)) {

            logger.debug("Attempting to validate ticket: {}", ticket);

            try {

                final Assertion assertion = this.ticketValidator.validate(ticket,constructServiceUrl(request, response));

                logger.debug("Successfully authenticated user: {}", assertion.getPrincipal().getName());

                request.setAttribute(CONST_CAS_ASSERTION, assertion);

                if (this.useSession) {
                    request.getSession().setAttribute(CONST_CAS_ASSERTION, assertion);
                }

                onSuccessfulValidation(request, response, assertion);

                if (this.redirectAfterValidation) {

                    logger.debug("Redirecting after successful ticket validation.");

                    String redirect=constructServiceUrl(request, response);

                    logger.debug("Redirect:{}",redirect);

                    Cookie cookie = new Cookie("CASTGC", URLEncoder.encode(ticket, "UTF-8"));
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    response.sendRedirect(redirect);
                    return;
                }

            } catch (final TicketValidationException e) {
                logger.debug("AbstractTicketValidationFilter have exception!");
                logger.debug(e.getMessage(), e);
                onFailedValidation(request, response);
                if (this.exceptionOnValidationFailure) {
                    throw new ServletException(e);
                }
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
        logger.debug("finish third filter--AbstractTicketValidationFilter");
    }

    public final void setTicketValidator(final TicketValidator ticketValidator) {
        this.ticketValidator = ticketValidator;
    }

    public final void setRedirectAfterValidation(final boolean redirectAfterValidation) {
        this.redirectAfterValidation = redirectAfterValidation;
    }

    public final void setExceptionOnValidationFailure(final boolean exceptionOnValidationFailure) {
        this.exceptionOnValidationFailure = exceptionOnValidationFailure;
    }

    public final void setUseSession(final boolean useSession) {
        this.useSession = useSession;
    }

}
