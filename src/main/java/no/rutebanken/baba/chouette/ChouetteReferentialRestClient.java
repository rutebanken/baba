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

import no.rutebanken.baba.exceptions.BabaException;
import no.rutebanken.baba.exceptions.ChouetteServiceException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Client for the Chouette Referentials REST services.
 */
@Component
public class ChouetteReferentialRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChouetteReferentialRestClient.class);

    @Value("${chouette.rest.referential.base.url:http://chouette/referentials}")
    private String chouetteRestServiceBaseUrl;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;


    public void createReferential(ChouetteReferentialInfo referential) {
        try {
            exchangeForChouetteReferentialInfo(referential, HttpMethod.POST, "/create");
        } catch (HttpClientErrorException e) {
            if (HttpStatus.SC_CONFLICT == e.getStatusCode().value()) {
                LOGGER.warn("The referential {} already exists in Chouette DB. Ignoring creation request", referential.getSchemaName());
            }
        } catch (HttpServerErrorException e) {
            throw new ChouetteServiceException("The Chouette referential service returned an error", e);
        } catch (ResourceAccessException e) {
            throw new ChouetteServiceException("The Chouette referential service is unavailable", e);
        }
    }

    public void updateReferential(ChouetteReferentialInfo referential) {
        exchangeForChouetteReferentialInfo(referential, HttpMethod.POST, "/update");
    }

    public void deleteReferential(ChouetteReferentialInfo referential) {
        exchangeForChouetteReferentialInfo(referential, HttpMethod.DELETE, "/delete");
    }

    private void exchangeForChouetteReferentialInfo(ChouetteReferentialInfo referential, HttpMethod httpMethod, String service) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChouetteReferentialInfo> entity = new HttpEntity<>(referential, headers);
        restTemplate.exchange(chouetteRestServiceBaseUrl + service, httpMethod, entity, Void.class);
    }



}
