package hello.springtx.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("============ call order =============");
        orderRepository.save(order);

        log.info("결제 프로세스 진입");
        OrderInput input = order.getInput();
        if (input.equals(OrderInput.SYSTEM_EXCEPTION)) {
            log.info("시스템 예외 발생");
            throw new RuntimeException();
        } else if (input.equals(OrderInput.BUSINESS_EXCEPTION)) {
            log.info("비즈니스 예외 발생");
            order.setStatus(OrderStatus.WAITING);
            throw new NotEnoughMoneyException();
        } else {
            log.info("정상 입력");
            order.setStatus(OrderStatus.COMPLETED);
        }

    }
}
