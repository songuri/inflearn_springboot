package jpabook.jpashop.controller;


import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


/**
 * 컨트롤러에서 어설프게 엔티티를 생성하지 마세요.
 * 트랜잭션이 있는 서비스 계층에 식별자( id )와 변경할 데이터를 명확하게 전달하세요.(파라미터 or dto)
 * 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경하세요.
 * 트랜잭션 커밋 시점에 변경 감지가 실행됩니다
 */

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());

        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book book = new Book();

        //이건 createBook 이런 생성자 메소드를 따로 만들어서 하는게 더 좋다.
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setIsbn(form.getIsbn());
        book.setAuthor(form.getAuthor());
        book.setStockQuantity(form.getStockQuantity());

        itemService.saveItem(book);

        return "redirect:/";

    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();  // 아이템 리스트 받아와서
        model.addAttribute("items", items); // 모델이 VIEW의 items랑 받아온 데이터 바인딩 해주고

        return "items/itemList"; // html 통으로 넘겨줌
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setIsbn(item.getIsbn());
        form.setAuthor(item.getAuthor());
        form.setStockQuantity(item.getStockQuantity());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

//    /**
//     * 상품 수정
//     */
//    @PostMapping(value = "/items/{itemId}/edit")
//    public String updateItem(@ModelAttribute("form") BookForm form) {
//
//
//        // 데이터 베이스에 한번 다녀온 데이터
//        // 새로 만든게 아니라 기존에 있던 식별자가 분명한 데이터
//        // 이런 데이터를 준영속 데이터라고 한다.
//        // JAP가 관리하지 않는 데이터.
//        Book book = new Book();
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//        itemService.saveItem(book);
//
//        return "redirect:/items";
//    }

    /**
     * 상품 수정, 권장 코드
     */
    @PostMapping(value = "/items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form")
    BookForm form) {
        itemService.updateItem(itemId, form.getName(), form.getPrice(),
                form.getStockQuantity());
        return "redirect:/items";
    }

}
