package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public abstract class InfoPrinter {

    protected void printInfo(String name) {
        log.info("call {}", name);
        boolean isActualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("tx active: {}", isActualTransactionActive);
    }
}
