package com.erp.school.security;



import com.erp.school.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter{

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// TODO Auto-generated method stub
		final String authHeader = request.getHeader("Authorization");
		String username=null;
		String jwt=null;

		System.out.println("Authorization Header: " + authHeader);

		if(authHeader !=null && authHeader.startsWith("Bearer ")) {
			jwt = authHeader.substring(7);
			username=jwtUtil.extractUsername(jwt);
			Claims claims = jwtUtil.getClaims(jwt);
			System.out.println(claims);
			String tenant = claims.get("tenant", String.class);
			System.out.println(tenant);
			if (tenant != null) {
				TenantContext.setTenant(tenant);
			}
			System.out.println("Extracted Username: " + username);

		}
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			 UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			System.out.println("Loaded UserDetails: " + userDetails);

			if(jwtUtil.validateToken(jwt, userDetails.getUsername())) {
				System.out.println("Token validation successful for user: " + username);
				Claims claims = jwtUtil.getClaims(jwt);
				String role = claims.get("role", String.class);

				System.out.println("Extracted Role from JWT: " + role);
				System.out.println("Extracted Role: " + role);

				List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
				System.out.println("Authorities: " + authorities);

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null,authorities);

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

				System.out.println("Authorities in SecurityContext: " +
						SecurityContextHolder.getContext().getAuthentication().getAuthorities());
			}
			else {
				System.out.println("Token validation failed for user: " + username);
			}

		}
		filterChain.doFilter(request, response);
	}

}
