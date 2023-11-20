package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;


    @Transactional // 기본적으로 위에 Transactional(readOnly = true)되 있어서 따로 이렇게 선언 안해주면 save가 안됨
    // save는 readonly가 아니기 때문이다!!!!!!!!!!
    public void saveItem(Item item) {
        log.info("saveItem");
        itemRepository.save(item);
    }


    @Transactional
    public Item updateItem(Long itemId, Book param) {
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());
        // 이렇게 다 바꾼 시점에 Transactional에 의해서 이 transaction이 Commit이 된다.
        // commit이 되는 순간 JAP는 Flush를 한다.
        // Flush를 하면 영속성 콘텍스트중에 변경된걸 다 찾아서 업데이트 쿼리를 디비에 날려 버린다.\

        return findItem;
    }
    /**
     * 영속성 컨텍스트가 자동 변경
     */
    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity) {
        Item item = itemRepository.findOne(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }


}
