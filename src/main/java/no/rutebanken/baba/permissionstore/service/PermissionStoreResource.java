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
 */

package no.rutebanken.baba.permissionstore.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Predicate;

/**
 * REST-client to access the Permission Store REST-service.
 */
public class PermissionStoreResource {

  private static final long MAX_RETRY_ATTEMPTS = 3;

  private final WebClient webClient;

  public PermissionStoreResource(
    @Qualifier("permissionStoreWebClient") WebClient permissionStoreWebClient
  ) {
    this.webClient = permissionStoreWebClient.mutate().build();
  }

  public PermissionStoreUser getUser(String userID) {
    return webClient.get().uri(uriBuilder -> uriBuilder
                    .path("/users/")
                    .queryParam("subject", userID)
                    .build())
            .retrieve()
      .bodyToMono(PermissionStoreUser.class)
      .retryWhen(
        Retry.backoff(MAX_RETRY_ATTEMPTS, Duration.ofSeconds(1)).filter(is5xx)
      )
      .block();
  }

  protected static final Predicate<Throwable> is5xx = throwable ->
    throwable instanceof WebClientResponseException webClientResponseException &&
    webClientResponseException.getStatusCode().is5xxServerError();
}
