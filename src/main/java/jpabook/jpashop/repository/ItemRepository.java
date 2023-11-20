package jpabook.jpashop.repository;


import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemRepository {
    @PersistenceContext

    private EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            log.info("11111111111");
            em.persist(item);  //신규로 등록 하는 경우.
        } else {
            log.info("22222222222");
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();

    }

}
