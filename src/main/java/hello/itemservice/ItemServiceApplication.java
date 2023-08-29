package hello.itemservice;

import hello.itemservice.config.QueryDslConfig;
import hello.itemservice.config.V2Config;
import hello.itemservice.service.ItemService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;


//@Import(MemoryConfig.class)
//@Import(JdbcTemplateConfig.class)
//@Import(JpaConfig.class)
//@Import(SpringDataJpaConfig.class)
//@Import(QueryDslConfig.class)
@Import(V2Config.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemService itemService) {
		return new TestDataInit(itemService);
	}

//	@Profile("test")
//	@Bean
//	public DataSource dataSource() {
//		DriverManagerDataSource ds = new DriverManagerDataSource();
//		ds.setDriverClassName("org.h2.Driver");
//		ds.setUrl("jdbc:h2:mem:test;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
//		ds.setUsername("sa");
//		ds.setPassword("");
//		return ds;
//	}
}
