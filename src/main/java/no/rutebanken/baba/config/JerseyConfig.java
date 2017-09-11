package no.rutebanken.baba.config;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import no.rutebanken.baba.filter.CorsResponseFilter;
import no.rutebanken.baba.health.rest.HazelcastResource;
import no.rutebanken.baba.health.rest.HealthResource;
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
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(HealthResource.class);
        register(CorsResponseFilter.class);
        register(HazelcastResource.class);

        register(ProviderResource.class);

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


        property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");

        configureSwagger();
    }


    private void configureSwagger() {
        // Available at localhost:port/api/swagger.json
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig config = new BeanConfig();
        config.setConfigId("organisation-registry-swagger-doc");
        config.setTitle("Organisation Registry API");
        config.setVersion("v1");
        config.setSchemes(new String[]{"http", "https"});
        config.setBasePath("/services");
        config.setResourcePackage("no.rutebanken.baba.organisation");
        config.setPrettyPrint(true);
        config.setScan(true);
    }

}
