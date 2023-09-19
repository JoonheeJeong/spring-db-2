package hello.springtx.order;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "orders")
@Entity
public class Order {

    @Id @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderInput input;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
