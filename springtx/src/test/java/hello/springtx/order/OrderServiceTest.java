package hello.springtx.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static hello.springtx.order.OrderStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService sut;
    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("정상 입력 시 완료")
    @Test
    void whenNormalInput_thenComplete() throws NotEnoughMoneyException {
        // given
        Order order = new Order();
        order.setInput(OrderInput.NORMAL);

        // when
        sut.order(order);

        // then
        Order orderPersisted = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(orderPersisted.getStatus()).isSameAs(OrderStatus.COMPLETED);
    }

    @DisplayName("시스템 예외 발생 시 롤백")
    @Test
    void whenSystemExceptionOccurs_thenRollback() {
        // given
        Order order = new Order();
        order.setInput(OrderInput.SYSTEM_EXCEPTION);

        // when, then
        Assertions.assertThatThrownBy(() -> sut.order(order))
                        .isInstanceOf(RuntimeException.class);

        Optional<Order> orderOptional = orderRepository.findById(order.getId());
        assertThat(orderOptional.isEmpty()).isTrue();
    }

    @DisplayName("비즈니스 예외 발생 시 대기 상태로 커밋")
    @Test
    void whenBusinessExceptionOccurs_thenCommitWithWaitingStatus() {
        // given
        Order order = new Order();
        order.setInput(OrderInput.BUSINESS_EXCEPTION);

        // when, then
        Assertions.assertThatThrownBy(() -> sut.order(order))
                .isInstanceOf(NotEnoughMoneyException.class);

        Order orderPersisted = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(orderPersisted.getStatus()).isSameAs(WAITING);
    }
}