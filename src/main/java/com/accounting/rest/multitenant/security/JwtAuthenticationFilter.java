package com.accounting.rest.multitenant.security;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.accounting.rest.multitenant.constant.JWTConstants;
import com.accounting.rest.multitenant.mastertenant.config.DBContextHolder;
import com.accounting.rest.multitenant.mastertenant.entity.MasterTenant;
import com.accounting.rest.multitenant.mastertenant.services.MasterTenantService;
import com.accounting.rest.multitenant.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	MasterTenantService masterTenantService;
	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		String header = httpServletRequest.getHeader(JWTConstants.HEADER_STRING);
		String username = null;
		String audience = null; // tenantOrClientId
		String authToken = null;

		if (header != null && header.startsWith(JWTConstants.TOKEN_PREFIX)) {
			authToken = header.replace(JWTConstants.TOKEN_PREFIX, "");
			log.info(">>>>>>>>>>>>>>>>>JwtAuthenticationFilter authToken:", authToken);

			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
				log.info(">>>>>>>>>>>>>>>>>JwtAuthenticationFilter username:", username);
				audience = jwtTokenUtil.getAudienceFromToken(authToken);
				log.info(">>>>>>>>>>>>>>>>>JwtAuthenticationFilter audience:", audience);
				MasterTenant masterTenant = masterTenantService.findByClientId(Integer.valueOf(audience));

				if (null == masterTenant) {
					log.error(">>>>>>>>>>>>>>>>>>>>masterTenant Null!");
					throw new BadCredentialsException("Invalid tenant and user.");
				}
				DBContextHolder.setCurrentDb(masterTenant.getDbName());

			} catch (IllegalArgumentException ex) {
				log.error("An error during getting username from token", ex);
			} catch (ExpiredJwtException ex) {
				log.warn("The token is expired and not valid anymore", ex);
			} catch (SignatureException ex) {
				log.error("Authentication Failed. Username or Password not valid.", ex);
			}
		} else {
			log.warn("Couldn't find bearer string, will ignore the header");
		}
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
			if (jwtTokenUtil.validateToken(authToken, userDetails)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
				log.info("authenticated user " + username + ", setting security context");
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}
