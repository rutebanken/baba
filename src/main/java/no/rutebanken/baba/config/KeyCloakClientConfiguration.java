/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.rutebanken.baba.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakClientConfiguration {

	@Value("${iam.keycloak.admin.path}")
	private String adminPath;

	@Value("${keycloak.realm:master}")
	private String realm;

	@Value("${iam.keycloak.user.realm:rutebanken}")
	private String userRealm;


	@Value("${iam.keycloak.admin.client:baba}")
	private String clientId;

	@Value("${iam.keycloak.admin.client.secret}")
	private String clientSecret;

	@Bean
	public RealmResource keycloakAdminClient() {
		return KeycloakBuilder.builder()
				       .serverUrl(adminPath)
				       .realm(realm)
				       .clientId(clientId)
					   .clientSecret(clientSecret)
				       .grantType("client_credentials")
				       .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				       .build().realm(userRealm);

	}
}
