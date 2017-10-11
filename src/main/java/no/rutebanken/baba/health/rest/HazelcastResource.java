package no.rutebanken.baba.health.rest;

import io.swagger.annotations.Api;
import no.rutebanken.baba.hazelcast.BabaHazelcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Produces("application/json")
@Path("/hazelcast")
@Api
public class HazelcastResource {

    @Autowired
    private BabaHazelcastService hazelcastService;


    @GET
    public String getInformation() {
        return hazelcastService.information();
    }
}
