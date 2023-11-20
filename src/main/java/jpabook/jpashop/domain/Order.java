package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.val;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @GeneratedValue @Id
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private  List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문시간

    private OrderStatus status;  //  ORDER , CANCEL


    //== 연관 관계 메서드 == //
    //ctrl + shift + enter 세미콜론 및 블록 자동 생성
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);

    }


    // 여러 연관관계가 많고 복잡한 생성은
    // 별도의 생성자가 있어서 거기에서만 관리되도록 하는게 좋다. 그래서 static으로 만듬.
    // 앞으로 뭔가 변경 되면 여기에서만 수정 하면 된다.


    /**
     * 네이밍을 통해 직관적으로 파악할 수 있다.
     * 생성자의 매개변수 타입이 겹칠 경우 사용이 사용할 수 있다.
     * 호출될 때마다 인스턴스를 생성하지 않아도된다.
     * 반환 타입의 하위 타입으로 객체를 반환 할 수있다.
     * static method를 작성하는 시점에 반환할 객체의 클래스가 존재하지 않아도 된다.
     * */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }


    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw  new IllegalStateException("이미 배송완료된 상품은 취소가 불가능 합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    /**
     * 조회 로직
     * 전체 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (final OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
//
//        int totalPrice = orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
//        return totalPrice;
    }

}
