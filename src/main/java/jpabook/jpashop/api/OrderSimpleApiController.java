package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 *
 * X TO One
 *  Order
 *  Order -> Member
 *  Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;


    /**
     * 즉시로딩 VS 지연로딩
     * 프록시와 즉시 로딩 주의할 점실무에서는 가급적 지연 로딩만 사용하다.
     * 즉시 로딩 쓰지 말자.JPA 구현체도 한번에 가저오려고 하고, 한번에 가져와서 쓰면 좋지 않나?
     * 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생한다.@ManyToOne이 5개 있는데 전부 EAGER로 설정되어 있다고 생각해보자.조인이 5개 일어난다.
     * 실무에선 테이블이 더 많다.
     * @return
     */

    // 안좋은 예제 직접적으로 ENtitiy가 노출 되기 때문이다.
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        //1 번 오류
        // order안에 Memeber가 있고 Member가 order List를 멤버로 가지고 있으므로
        // 무한 루프에 빠지는 현상이 발생.
        // 양방향 연관관계가 발생한거임 ㅇ.ㅇ
        // 어느 한쪽에는 JsonIgnore 해줘야함... (Memeber , orderItem , Delivery)

        //2 번 오류
        // @JsonIgnore 해도 Type definition error: 에러 남
        // order 안에 있는 Member는 ManyToOne(fetch = LAZY) 지연로딩 으로 설정되어 있어서
        // 프록시 객체만 초기화 된 상태! 그래서 TYPE ERROR가 발생한것이다.

        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // Hibernate5Module force 옵션 없이 강제로 lazy 로딩 강제 초기화 하는 방법이 존재하긴 함.
//        for (Order order : all) {
//            String name = order.getMember().getName(); // 이렇게 하면 Lazy 강제 초기화가 가능하다. select name 쿼리를 실제로 날려 버리는거.
//            Address address = order.getMember().getAddress();
//        }
        return all;
    }
}
