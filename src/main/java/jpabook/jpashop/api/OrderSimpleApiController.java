package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepositoty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * X TO One
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepositoty orderSimpleQueryRepositoty;


    /**
     * 즉시로딩 VS 지연로딩
     * 프록시와 즉시 로딩 주의할 점실무에서는 가급적 지연 로딩만 사용하다.
     * 즉시 로딩 쓰지 말자.JPA 구현체도 한번에 가저오려고 하고, 한번에 가져와서 쓰면 좋지 않나?
     * 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생한다.@ManyToOne이 5개 있는데 전부 EAGER로 설정되어 있다고 생각해보자.조인이 5개 일어난다.
     * 실무에선 테이블이 더 많다.
     *
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

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {
        //여기까지도 문제가 있는게 Lazy 로딩으로 인한 데이터베이스 쿼리가 너무 많이 호출 되고 있음.

        //ORDER 2개 조회. 그러면 각각의 getName getAddress는 order 갯수 만큼 쿼리 조회
        //이게 바로 N + 1 이슈 [지연로딩이 N 번 발생한다는 이야기]
        // 오더 N : 2
        // N + 1 -> 1 + 회원 N + 배송 N = 5
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> collect = orders.stream().map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * Result<T> class를 사용해서 Count 추가 하는 방법
     * @return
     */
    @GetMapping("/api/v2/simple-orders2")
    public MemberApiController.Result orderV2_2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> collect = orders.stream().map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return new MemberApiController.Result(collect.size(), collect);
    }



    /**
     * fetch join을 사용하여 N+1를 사용해서 해결 한 케이스...
     * 솔직히 이해 안감.
     *
     * 여기까지도 Entity를 조회해서 그걸 DTO로 변형한후  반환하고 있음.
     * @return
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        //이렇게 하면 쿼리가 한번만 나감... 시발 졸라 어렵네. FUCK
        // 실무에서는 실제로 이렇게 많이 이용함.
       List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = orders.stream().map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return collect;

        /**
         *     select
         *         order0_.order_id as order_id1_7_0_,
         *         member1_.member_id as member_i1_5_1_,
         *         delivery2_.delivery_id as delivery1_3_2_,
         *         order0_.delivery_id as delivery4_7_0_,
         *         order0_.member_id as member_i5_7_0_,
         *         order0_.order_date as order_da2_7_0_,
         *         order0_.status as status3_7_0_,
         *         member1_.city as city2_5_1_,
         *         member1_.street as street3_5_1_,
         *         member1_.zipcode as zipcode4_5_1_,
         *         member1_.name as name5_5_1_,
         *         delivery2_.city as city2_3_2_,
         *         delivery2_.street as street3_3_2_,
         *         delivery2_.zipcode as zipcode4_3_2_,
         *         delivery2_.status as status5_3_2_
         *     from
         *         orders order0_
         *     inner join
         *         member member1_
         *             on order0_.member_id=member1_.member_id
         *     inner join
         *         delivery delivery2_
         *             on order0_.delivery_id=delivery2_.delivery_id
         */
    }


    /**
     * JPA에서 DTO 바로 조회
     * @return
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        //return orderRepository.findOrderDtos();
        return orderSimpleQueryRepositoty.findOrderDtos();
        /**
         * //SELECT 하는 것들이 줄어 들긴 하는데...(원하는것만 SELECT)
         * 이게 그렇게 성능에 영향을 미치는지는 사실 잘모르겠음
         * 데이터 사이즈가 엄청크거나 많거나 하면 확실히 성능이 좋아지긴 하지만
         * 그렇지 않으면 사실 큰 영향은 없음.
         *    select
         *         order0_.order_id as col_0_0_,
         *         member1_.name as col_1_0_,
         *         order0_.order_date as col_2_0_,
         *         order0_.status as col_3_0_,
         *         delivery2_.city as col_4_0_,
         *         delivery2_.street as col_4_1_,
         *         delivery2_.zipcode as col_4_2_
         *     from
         *         orders order0_
         *     inner join
         *         member member1_
         *             on order0_.member_id=member1_.member_id
         *     inner join
         *         delivery delivery2_
         *             on order0_.delivery_id=delivery2_.delivery_id
         */
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            //여기서 getName() , GetAddress()는 Order의 갯수만큼 쿼리가 나가게 된다.
            name = order.getMember().getName(); // Lazy 초기화 (영속성 컨텍스트가 이 멤버 아이디로 컨텍스트를 찾다가 없으면 DB 쿼리 날림.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
