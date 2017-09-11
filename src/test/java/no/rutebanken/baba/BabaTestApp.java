package no.rutebanken.baba;


import no.rutebanken.baba.config.BabaSecurityConfiguration;
import no.rutebanken.baba.organisation.repository.BaseRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"no.rutebanken.baba.organisation.repository"},
        repositoryBaseClass = BaseRepositoryImpl.class)
@ComponentScan(excludeFilters = {
                                        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = BabaSecurityConfiguration.class),
                                        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = App.class),
})
public class BabaTestApp {

    public static void main(String[] args) {
        SpringApplication.run(BabaTestApp.class, args);
    }
}
