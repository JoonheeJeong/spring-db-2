package hello.springtx.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class RollbackTest {

    @Autowired
    private RollbackService sut;

    @Test
    void callUncheckedException() {
        assertThatThrownBy(() -> sut.uncheckedException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void callCheckedException() {
        assertThatThrownBy(() -> sut.checkedException())
                .isInstanceOf(Exception.class);
    }

    @Test
    void callRollbackForException() {
        assertThatThrownBy(() -> sut.rollbackForException())
                .isInstanceOf(Exception.class);
    }

    @TestConfiguration
    static class RollbackTestConfig {
        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {

        @Transactional
        public void uncheckedException() {
            log.info("call uncheckedException: expected to rollback");
            throw new RuntimeException();
        }

        @Transactional
        public void checkedException() throws Exception {
            log.info("call checkedException: expected to commit");
            throw new Exception();
        }

        @Transactional(rollbackFor = Exception.class)
        public void rollbackForException() throws Exception {
            log.info("call rollbackForException: expected to rollback");
            throw new Exception();
        }
    }
}
