package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 여러분 이렇게 repository에서 컨트롤러 쪽에 의존광이 생기면 망하는 거죠
 * 컨트롤러에서 리파지토리를 가는 것 정도는 괜찮구요
 */

@Data
public class OrderSimpleQueryDto {


    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Order order) {
        orderId = order.getId();
        //여기서 getName() , GetAddress()는 Order의 갯수만큼 쿼리가 나가게 된다.
        name = order.getMember().getName(); // Lazy 초기화 (영속성 컨텍스트가 이 멤버 아이디로 컨텍스트를 찾다가 없으면 DB 쿼리 날림.
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
    }
    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
