package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class TxBasicTest {

    @Autowired
    private BasicService basicService;

    @Test
    void proxyCheck() {
        log.info("aop class={}", basicService.getClass());
        assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    @Test
    void testTx() {
        basicService.tx();
        basicService.nonTx();
    }

    @TestConfiguration
    static class TxBasicTestConfig {

        @Bean
        public BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService extends InfoPrinter {

        @Transactional
        public void tx() {
            printInfo("tx");
        }

        public void nonTx() {
            printInfo("nonTx");
        }
    }

}
