package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional  //JAP에서 같은 트랜젝션 안에서 같은 엔티티, 그러니까 ID값이 똑같으면(같은 PK) 이건 같은 영속성 컨텍스트 에서 똑같은 애가 관리가 된다.
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Rollback(false) // 이걸 안하면 insert문이 실행이 안됨 기본적으로 Transactional / persistens는 Commit 시점에 insert문이 실행.
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("Kim");

//        Member member2 = new Member();
//        member2.setName("Lee");

        //when
        //ctrl + art + v 자동 변수 생성
        Long savedId = memberService.join(member);


        //then
        Assertions.assertEquals(member, memberRepository.findOne(savedId));
        //Assertions.assertEquals(member, member2);
    }

    @Test
    public void 중복회원예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("Kim");

        Member member2 = new Member();
        member2.setName("Kim");

        //when

        memberService.join(member1);
//        memberService.join(member2);  //예외 발생.

//        try {
//            memberService.join(member2);  //예외 발생.
//        } catch (IllegalStateException e) {
//            return ;
//        }
        IllegalStateException thrown =
                Assertions.assertThrows(IllegalStateException.class, () -> memberService.join(member2));


        //then
//        Assertions.fail("예외가 발생해야 한다.");
        Assertions.assertEquals("이미 존재하는 회원입니다.", thrown.getMessage());
    }

}