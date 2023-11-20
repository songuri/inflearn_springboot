package jpabook.jpashop.controller;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm()); // model은 뷰의 memberForm 변수에 MemberForm 데이터 바인딩 해줌
        return "members/createMemberForm";
    }

    //BindingResult 를쓰면 해당 컨트롤러에서 오류가 나도 오류 페이지로 안넘기고 처리할 수 있다?
    // Member를 안쓰고 MemberForm을 쓰는 이유는 실제 Entity와 화면에서 쓰는 데이터 형식(valid 등)이 다를 수 있기 때문에 화면에 fit한 MemeberForm을 만드는게 더 깔끔함.
    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm, BindingResult result) { // @Valid 이걸 쓰면 MemeberForm에 @NotEmepty 선언된거에 필수인 애들을 체크해줌

        /**
         * BindingResult result 를 이용한 에러처리 없으면 에러페이지로 보내 버림
         */

        /**
         * createMemberForm.html에 아래 코드에 연결이 된다.
         *  <input type="text" th:field="*{name}" class="form-control" placeholder="이름을 입력하세요"
         *                    th:class="${#fields.hasErrors('name')}? 'form-control fieldError' : 'form-control'">
         *             <p th:if="${#fields.hasErrors('name')}"
         *                th:errors="*{name}">Incorrect date</p>
         */
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";

    }

    @GetMapping("/members")
    public String list(Model model) {

        //API를 만들때는 절대  Entity를 절대 그대로 넘기면 안된다.
        // 이건 그냥 예제니까...
        List<Member> members = memberService.findMember();
        model.addAttribute("members", members);
        return "members/memberList";
    }

}
