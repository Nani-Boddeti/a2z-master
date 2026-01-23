package com.a2z.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@CrossOrigin("http://localhost:4200")
public class AuthorizationServerConfig {

	private static final String[] WHITE_LIST_URLS = {
			"/", "/ad/all", "/ad/view/**", "/c/**", "/customerSubmit",
			"/suggest/password", "/generate/otp/**", "/validateOTP",
			"/login**", "/loginV2", "/oauth2/**", "/login", "/search/**", "/.well-known/**","/uploads/**","/error","/error/**","/callback"
	};

	private static final String[] AUTHENTICATED_URLS = {
			"/order/**", "/ad/post", "/ad/activate/**", "/media/**",
			"/upload/**", "/myAccount/**", "/users/**"
	};

	// ✅ SHARED SESSION CONFIG (eliminates duplication)
	private void configureSession(HttpSecurity http) throws Exception {
		http.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				.sessionFixation().migrateSession()
		);
	}

	// ✅ SHARED STATELESS API CONFIG
	private void configureStatelessJwt(HttpSecurity http) throws Exception {
		http.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
				/*.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))*/

				.csrf(AbstractHttpConfigurer::disable);
	}

	@Bean @Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfigurer configurer = OAuth2AuthorizationServerConfigurer.authorizationServer();

		http
				.securityMatcher(configurer.getEndpointsMatcher())
				.authorizeHttpRequests(authz -> authz  // ✅ Fixed syntax
						.requestMatchers("/oauth2/authorize").authenticated()
						.anyRequest().permitAll()
				)
				.with(configurer, c -> c.oidc(Customizer.withDefaults()))
				.exceptionHandling(exceptions -> exceptions
						.defaultAuthenticationEntryPointFor(
								new LoginUrlAuthenticationEntryPoint("/loginV3"),
								new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
						)
				)
				.oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
				.formLogin(form -> form
						.loginPage("/loginV3")
						.loginProcessingUrl("/perform_login")
						.defaultSuccessUrl("/callback", true)
						.successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
				)
				.requestCache(Customizer.withDefaults());  // ✅ Keep default

		return http.build();
	}

	@Bean @Order(2)
	public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher(AUTHENTICATED_URLS)
				.authorizeHttpRequests(authz -> authz
						.requestMatchers(WHITE_LIST_URLS).permitAll()
						.anyRequest().authenticated()
				);
		/*http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
				jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));*/
		configureStatelessJwt(http);  // ✅ Reuse
		return http.build();
	}

	@Bean @Order(3)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authz -> authz
						.requestMatchers(WHITE_LIST_URLS).permitAll()
						// Allow OPTIONS requests for CORS preflight
						.requestMatchers("OPTIONS", "/**").permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						// Point to Angular login route instead of default Spring form
						.loginPage("/loginV3")  // Angular serves this route
						.loginProcessingUrl("/perform_login")  // Backend endpoint
						.defaultSuccessUrl("http://localhost:4200/callback", true)
						.successHandler(successHandler())
						.permitAll());
		configureSession(http);  // ✅ Reuse
		http.csrf(AbstractHttpConfigurer::disable);
		http.requestCache(Customizer.withDefaults());
		// Enable CORS for all endpoints
		http.cors(Customizer.withDefaults());
		return http.build();
	}
	@Bean
	public AuthenticationSuccessHandler successHandler() {
		return (request, response, authentication) -> {
			response.setContentType("application/json");
			response.setStatus(200);
			response.getWriter().write("{\"success\":true,\"redirect\":\"/callback\"}");
		};
	}
// Below methods are used to have Roles as authorities in JWT Token instead of Scopes.
	/*@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter gac = new JwtGrantedAuthoritiesConverter();
		gac.setAuthoritiesClaimName("roles");   // claim you will add in the token
		gac.setAuthorityPrefix("");             // because values are already "ROLE_USER"

		JwtAuthenticationConverter jac = new JwtAuthenticationConverter();
		jac.setJwtGrantedAuthoritiesConverter(gac);
		return jac;
	}*/
	/*@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
		return context -> {
			if (context.getTokenType().getValue().equals("access_token")) {
				Authentication principal = context.getPrincipal();
				List<String> roles = principal.getAuthorities().stream()
						.map(GrantedAuthority::getAuthority)
						.toList();
				context.getClaims().claim("roles", roles);
			}
		};
	}*/
//	@Bean
//	SecurityFilterChain api(HttpSecurity http) throws Exception {
//		http.oauth2ResourceServer(oauth2 -> oauth2
//				.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
//		);
//		return http.build();
//	}

	@Bean
	JdbcOAuth2AuthorizationService authorizationService(DataSource dataSource, RegisteredClientRepository clientRepository) {
		return new JdbcOAuth2AuthorizationService(new JdbcTemplate(dataSource), clientRepository);
	}

	@Bean
	JdbcRegisteredClientRepository registeredClientRepository(DataSource dataSource) {
		return new JdbcRegisteredClientRepository(new JdbcTemplate(dataSource));
	}

	// ✅ CORS Configuration Bean to fix Angular preflight requests
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(java.util.List.of("http://localhost:4200", "http://localhost:4201"));
		configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(java.util.List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
