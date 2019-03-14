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

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client for the Chouette Referentials REST services.
 */
@Component
public class ChouetteReferentialRestClient {

    private static final int HTTP_TIMEOUT = 5000;


    @Value("${chouette.rest.referential.base.url:http://chouette/referentials}")
    private String chouetteRestServiceBaseUrl;


    public void createReferential(ChouetteReferentialInfo referential) {
        postForChouetteReferentialInfo(referential, "/create");
    }

    public void updateReferential(ChouetteReferentialInfo referential) {
        postForChouetteReferentialInfo(referential, "/update");
    }

    public void deleteReferential(ChouetteReferentialInfo referential) {
        postForChouetteReferentialInfo(referential, "/drop");
    }

    private void postForChouetteReferentialInfo(ChouetteReferentialInfo referential, String method) {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChouetteReferentialInfo> entity = new HttpEntity<>(referential, headers);
        restTemplate.postForEntity(chouetteRestServiceBaseUrl + method, entity, Void.class);
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(HTTP_TIMEOUT)
                .setConnectionRequestTimeout(HTTP_TIMEOUT)
                .setSocketTimeout(HTTP_TIMEOUT)
                .build();
        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }


}