package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jdbctemplate.JdbcTemplateRepositoryV1;
import hello.itemservice.repository.jdbctemplate.JdbcTemplateRepositoryV2;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Configuration
public class JdbcTemplateConfig {

    private final DataSource dataSource;

    @Bean
    public ItemRepository itemRepository() {
//        return new JdbcTemplateRepositoryV1(dataSource);
        return new JdbcTemplateRepositoryV2(dataSource);
    }

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }
}
