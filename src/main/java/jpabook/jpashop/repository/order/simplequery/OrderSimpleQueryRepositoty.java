package jpabook.jpashop.repository.order.simplequery;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepositoty {

    /**
     *  Repository는 되도록 순수한 Entity를 조회하는데 사용하는게 좋다.
     *  근데 findOrderDtos 이런 화면을 위한 쿼리 같은 경우는 그냥 이렇게 별도로 관리되는게 좋다.
     *  화면에 디펜던시가 있는것들
     */
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        // 이것 봤을때 o 가 SELECT 됬는데 OrderSimpleQueryDto 여기에 맵핑 할 수 가 없다.
        // Order클래스와 OrderSimpleQueryDto 클래는 다르니까?
/*        List<OrderSimpleQueryDto> resultList = em.createQuery(
                        "select o from Order o" +
                                        " join o.member m" +
                                        " join o.delivery d",
                        OrderSimpleQueryDto.class)
                .getResultList();*/

        List<OrderSimpleQueryDto> resultList = em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                                " join o.member m" +
                                " join o.delivery d",
                        OrderSimpleQueryDto.class)
                .getResultList();
        return resultList;
    }

}
