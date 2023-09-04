package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class InternalCallV1Test {

    @Autowired
    private CallService callService;

    @Test
    void testCallExternal() {
        callService.external();
    }

    @Test
    void testCallInternal() {
        callService.internal();
    }

    @TestConfiguration
    static class InternalCallV1Config {
        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    @Slf4j
    static class CallService extends InfoPrinter{

        public void external() {
            printInfo("external");
            internal();
        }

        @Transactional
        public void internal() {
            printInfo("internal");
        }
    }
}
