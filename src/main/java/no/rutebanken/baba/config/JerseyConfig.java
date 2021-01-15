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

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import no.rutebanken.baba.filter.CorsResponseFilter;
import no.rutebanken.baba.organisation.rest.AdministrativeZoneResource;
import no.rutebanken.baba.organisation.rest.CodeSpaceResource;
import no.rutebanken.baba.organisation.rest.EntityClassificationResource;
import no.rutebanken.baba.organisation.rest.EntityTypeResource;
import no.rutebanken.baba.organisation.rest.NotificationConfigurationResource;
import no.rutebanken.baba.organisation.rest.OrganisationResource;
import no.rutebanken.baba.organisation.rest.ResponsibilitySetResource;
import no.rutebanken.baba.organisation.rest.RoleResource;
import no.rutebanken.baba.organisation.rest.UserResource;
import no.rutebanken.baba.organisation.rest.exception.AccessDeniedExceptionMapper;
import no.rutebanken.baba.organisation.rest.exception.IllegalArgumentExceptionMapper;
import no.rutebanken.baba.organisation.rest.exception.NotAuthenticatedExceptionMapper;
import no.rutebanken.baba.organisation.rest.exception.OrganisationExceptionMapper;
import no.rutebanken.baba.organisation.rest.exception.PersistenceExceptionMapper;
import no.rutebanken.baba.organisation.rest.exception.SpringExceptionMapper;
import no.rutebanken.baba.provider.rest.ProviderResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig {



    @Bean
    public ServletRegistrationBean organisationsAPIJerseyConfig() {
        ServletRegistrationBean publicJersey
                = new ServletRegistrationBean(new ServletContainer(new OrganisationsAPIConfig()));
        publicJersey.addUrlMappings("/services/organisations/*");
        publicJersey.setName("OrganisationAPI");
        publicJersey.setLoadOnStartup(0);
        publicJersey.getInitParameters().put("swagger.scanner.id", "organisations-scanner");
        publicJersey.getInitParameters().put("swagger.config.id","organisations-swagger-doc" );
        return publicJersey;
    }



    @Bean
    public ServletRegistrationBean providersAPIJerseyConfig() {
        ServletRegistrationBean publicJersey
                = new ServletRegistrationBean(new ServletContainer(new ProvidersAPIConfig()));
        publicJersey.addUrlMappings("/services/providers/*");
        publicJersey.setName("ProvidersAPI");
        publicJersey.setLoadOnStartup(0);
        publicJersey.getInitParameters().put("swagger.scanner.id", "providers-scanner");
        publicJersey.getInitParameters().put("swagger.config.id", "providers-swagger-doc");
        return publicJersey;
    }

    private class OrganisationsAPIConfig extends ResourceConfig {

        public OrganisationsAPIConfig() {
            register(CorsResponseFilter.class);

            register(CodeSpaceResource.class);
            register(OrganisationResource.class);
            register(AdministrativeZoneResource.class);
            register(UserResource.class);
            register(NotificationConfigurationResource.class);
            register(RoleResource.class);
            register(EntityTypeResource.class);
            register(EntityClassificationResource.class);
            register(ResponsibilitySetResource.class);

            register(NotAuthenticatedExceptionMapper.class);
            register(PersistenceExceptionMapper.class);
            register(SpringExceptionMapper.class);
            register(IllegalArgumentExceptionMapper.class);
            register(AccessDeniedExceptionMapper.class);
            register(OrganisationExceptionMapper.class);

            configureSwagger();
        }


        private void configureSwagger() {
            // Available at localhost:port/api/swagger.json
            this.register(ApiListingResource.class);
            this.register(SwaggerSerializers.class);

            BeanConfig config = new BeanConfig();
            config.setConfigId("organisations-swagger-doc");
            config.setTitle("Organisations API");
            config.setVersion("v1");
            config.setSchemes(new String[]{"http", "https"});
            config.setResourcePackage("no.rutebanken.baba.organisation");
            config.setPrettyPrint(true);
            config.setScan(true);
            config.setScannerId("organisations-scanner");
        }
    }

    private class ProvidersAPIConfig extends ResourceConfig {

        public ProvidersAPIConfig() {
            register(CorsResponseFilter.class);

            register(ProviderResource.class);

            register(NotAuthenticatedExceptionMapper.class);
            register(PersistenceExceptionMapper.class);
            register(SpringExceptionMapper.class);
            register(IllegalArgumentExceptionMapper.class);
            register(AccessDeniedExceptionMapper.class);
            register(OrganisationExceptionMapper.class);

            configureSwagger();
        }


        private void configureSwagger() {
            // Available at localhost:port/api/swagger.json
            this.register(ApiListingResource.class);
            this.register(SwaggerSerializers.class);

            BeanConfig config = new BeanConfig();
            config.setConfigId("providers-swagger-doc");
            config.setTitle("Providers API");
            config.setVersion("v1");
            config.setSchemes(new String[]{"http", "https"});
            config.setResourcePackage("no.rutebanken.baba.provider");
            config.setPrettyPrint(true);
            config.setScan(true);
            config.setScannerId("providers-scanner");
        }
    }

}
