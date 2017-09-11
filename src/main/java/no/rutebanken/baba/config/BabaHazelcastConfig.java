package no.rutebanken.baba.config;

import com.hazelcast.core.HazelcastInstance;
import no.rutebanken.baba.hazelcast.BabaHazelcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BabaHazelcastConfig {

    @Bean
    public HazelcastInstance hazelcastInstance(@Autowired BabaHazelcastService hazelcastService) {
        return hazelcastService.getHazelcastInstance();
    }

}
