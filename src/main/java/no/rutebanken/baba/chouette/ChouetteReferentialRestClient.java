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

package no.rutebanken.baba.chouette;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import no.rutebanken.baba.exceptions.ChouetteServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Client for the Chouette Referentials REST services.
 */
@Component
public class ChouetteReferentialRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChouetteReferentialRestClient.class);
    private static final int HTTP_TIMEOUT = 10000;

    private final WebClient webClient;
    private final int maxRetryAttempts;

    public ChouetteReferentialRestClient(@Autowired WebClient.Builder webClientBuilder,
                                         @Value("${chouette.rest.referential.base.url:http://chouette/referentials}") String chouetteRestServiceBaseUrl,
                                         @Value("${chouette.rest.referential.retry.max:3}") int maxRetryAttempts) {

        TcpClient tcpClient = TcpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, HTTP_TIMEOUT).doOnConnected(connection -> {
            connection.addHandlerLast(new ReadTimeoutHandler(HTTP_TIMEOUT, TimeUnit.MILLISECONDS));
            connection.addHandlerLast(new WriteTimeoutHandler(HTTP_TIMEOUT, TimeUnit.MILLISECONDS));
        });

        this.webClient = webClientBuilder.baseUrl(chouetteRestServiceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();

        this.maxRetryAttempts = maxRetryAttempts;
    }

    public void createReferential(ChouetteReferentialInfo referential) {
        try {
            exchangeForChouetteReferentialInfo(referential, HttpMethod.POST, "/create");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                LOGGER.warn("The referential {} already exists in Chouette DB. Ignoring creation request", referential.getSchemaName());
            } else {
                throw new ChouetteServiceException("The Chouette referential service returned an error", e);
            }
        } catch (Exception e) {
            throw new ChouetteServiceException("The Chouette referential service is unavailable", e);
        }
    }

    public void updateReferential(ChouetteReferentialInfo referential) {
        exchangeForChouetteReferentialInfo(referential, HttpMethod.POST, "/update");
    }

    public void deleteReferential(ChouetteReferentialInfo referential) {
        exchangeForChouetteReferentialInfo(referential, HttpMethod.DELETE, "/delete");
    }

    private Void exchangeForChouetteReferentialInfo(ChouetteReferentialInfo referential, HttpMethod httpMethod, String service) {
        return webClient.method(httpMethod)
                .uri(service)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(referential))
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofSeconds(1)).filter(is5xx))
                .block(Duration.ofMillis(HTTP_TIMEOUT));
    }

    protected static final Predicate<Throwable> is5xx =
            throwable -> throwable instanceof WebClientResponseException && ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();


}
