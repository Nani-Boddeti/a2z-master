package com.a2z.configuration;

import java.util.UUID;

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
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;

@Configuration
@EnableWebSecurity
@CrossOrigin("http://localhost:4200")
public class AuthorizationServerConfig {
	
	private static final String[] WHITE_LIST_URLS = { "/","/ad/all/", "/ad/view/**", "/c/**", "/customerSubmit",
			"/suggest/password", "/generate/otp/**", "/validateOTP", "/login**", "/oauth2/**","/login","/search/**" };

	private static final String[] AUTHENTICATED_URL = { "/order/**", "/ad/post", "/ad/activate/**", "/media/**",
			"/upload/**", "/myAccount/**","/users/**" };

		@Bean
		@Order(1)
		public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
				throws Exception {
			OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
			http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				.oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0
			http
				// Redirect to the login page when not authenticated from the
				// authorization endpoint
				.exceptionHandling((exceptions) -> exceptions
					.defaultAuthenticationEntryPointFor(
						new LoginUrlAuthenticationEntryPoint("/login"),
						new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
					)
				)
				// Accept access tokens for User Info and/or Client Registration
				.oauth2ResourceServer((resourceServer) -> resourceServer
					.jwt(Customizer.withDefaults()));

			return http.build();
		}

		@Bean
		@Order(2)
		public SecurityFilterChain jwtFilterChain(HttpSecurity http)
				throws Exception {
			// chain would be invoked only for paths that start with /api/
			http.securityMatcher(AUTHENTICATED_URL)
					.authorizeHttpRequests((authorize) ->
							authorize
									.requestMatchers(WHITE_LIST_URLS).permitAll()
									.anyRequest().authenticated()
					)
					// Ignoring session cookie
					.sessionManagement(configurer ->
							configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.oauth2ResourceServer((resourceServer) -> resourceServer
							.jwt(Customizer.withDefaults()))
					// disabling csrf tokens for the sake of the example
					.csrf(AbstractHttpConfigurer::disable);

			return http.build();
		}

		@Bean 
		@Order(3)
		public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
				throws Exception {
			http
				.authorizeHttpRequests((authorize) -> authorize
						.requestMatchers(WHITE_LIST_URLS).permitAll()
						.anyRequest().authenticated()
				)
				// Form login handles the redirect to the login page from the
				// authorization server filter chain
				.formLogin(Customizer.withDefaults())
				// disabling csrf tokens for the sake of the example
				.csrf(AbstractHttpConfigurer::disable);

			return http.build();
		}

		

		@Bean
		JdbcOAuth2AuthorizationService authorizationService(DataSource dataSource, RegisteredClientRepository clientRepository) {
			return new JdbcOAuth2AuthorizationService(new JdbcTemplate(dataSource), clientRepository);
		}
		
		@Bean
		JdbcRegisteredClientRepository registeredClientRepository(DataSource dataSource) {
			return new JdbcRegisteredClientRepository(new JdbcTemplate(dataSource));
		}
}
