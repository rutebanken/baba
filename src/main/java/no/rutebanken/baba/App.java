package no.rutebanken.baba;

import no.rutebanken.baba.organisation.model.CodeSpace;
import no.rutebanken.baba.organisation.repository.BaseRepositoryImpl;
import no.rutebanken.baba.provider.domain.Provider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"no.rutebanken.baba.organisation.repository"},
        repositoryBaseClass = BaseRepositoryImpl.class)
@EntityScan(basePackageClasses = {CodeSpace.class, Provider.class, Jsr310JpaConverters.class})
@EnableCaching
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
