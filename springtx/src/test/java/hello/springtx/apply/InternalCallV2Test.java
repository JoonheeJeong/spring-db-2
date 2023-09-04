package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class InternalCallV2Test {

    @Autowired
    private CallService callService;

    @Test
    void testCallExternal() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV2Config {
        @Bean
        CallService callService() {
            return new CallService(internalCallService());
        }

        @Bean
        InternalCallService internalCallService() {
            return new InternalCallService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService extends InfoPrinter {

        private final InternalCallService internalCallService;

        public void external() {
            printInfo("external");
            internalCallService.internal();
        }
    }

    static class InternalCallService extends InfoPrinter {

        @Transactional
        public void internal() {
            printInfo("internal");
        }
    }
}
