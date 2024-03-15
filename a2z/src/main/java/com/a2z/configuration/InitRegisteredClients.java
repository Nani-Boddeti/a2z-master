package com.a2z.configuration;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

@Component
public class InitRegisteredClients implements ApplicationRunner {
	private final RegisteredClientRepository repository;

	public InitRegisteredClients(RegisteredClientRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {

		/*
		 * RegisteredClient.Builder registration = RegisteredClient.withId("spring")
		 * .clientId("spring") // plaintext is secret It is encoded with BCrypt from
		 * EncodedSecretTests // do not include secrets in the source code because bad
		 * actors can get access to your secrets .clientSecret(
		 * "{bcrypt}$2a$14$R4y8inie3JrOiY3w.wqlL.H1fle8XjFnJNfPt/IeFPKgFI2NiJ95C")
		 * .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
		 * .authorizationGrantTypes(types -> {
		 * types.add(AuthorizationGrantType.AUTHORIZATION_CODE);
		 * types.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
		 * types.add(AuthorizationGrantType.REFRESH_TOKEN); })
		 * .redirectUri("http://127.0.0.1:8080/login/oauth2/code/spring") .scopes(scopes
		 * -> { scopes.add("openid"); scopes.add("profile"); scopes.add("email");
		 * scopes.add("phone"); scopes.add("address"); scopes.add("keys.write"); })
		 * .clientSettings(ClientSettings.builder() .requireAuthorizationConsent(true)
		 * .build());
		 */
		String client_ID = "oidc-client";
		RegisteredClient registeredClient = repository.findByClientId(client_ID);
		if(Objects.isNull(registeredClient)) {
			RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
					.clientId("oidc-client")
					.clientSecret("$2a$11$ODW3iXk8cYyCNOked2FM1OXjwHA2K0mOxqtX.CDwjuNwxG2BMkC6.")
					.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
					.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
					.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
					.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
					.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
					.redirectUri("http://localhost:4200/")
					.scope("app.read")
					.scope("app.write")
					.clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).requireProofKey(true).build())
					.tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofSeconds(900)).build())
					.build();
			repository.save(oidcClient);
		}


	}
}