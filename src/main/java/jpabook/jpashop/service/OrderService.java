package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional //Transaction 안에서 조회해야 영속성이 유지가 된다?
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item =itemRepository.findOne(itemId);


        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        // delivery , orderItem 따로 persist 안하고도 그냥 save가 되는 이유는?
        // 두개는 지금 Cascade 설정을 해줬기 때문... 지금은 뭔말인지 모르겠음. 대충 이해는 감
        // 여기서 바로 생성해서 쓴건데 따로 persist  하는곳이 없어도 자동으로 되게 하는것
        // orderItem과 Delivery는 여기서만 쓰니까 Cascade를 써도 좋다.


        return order.getId();

    }

    //취소

    /**
     * 주문 취소
     */
    @Transactional
    public void cancel(Long orderID) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderID);
        //주문 취소
        order.cancel(); // 따로 UPDATE 쿼리를 날릴 필요가 없다.
    }


    //검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
