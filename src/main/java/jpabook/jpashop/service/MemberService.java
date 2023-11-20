package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
//@AllArgsConstructor <- 이거 쓰면 생성자를 자동으로 만들어줌
@RequiredArgsConstructor //<- final로 선언된 애들로만 생성자를 만들어줌 이게 제일 좋음.
public class MemberService {

    //@Autowired
    private final MemberRepository memberRepository; // final로 선언하는게 좋다.
    //@Autowired 하면 Spring이 Spring Bean에 등록되어 있는 리포지토리를 Injection 해준다.
    // 이런걸 Field Injection 이라고 한다.


//    //실제로는 이렇게 쓰는게 좋다.
//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

//    public static void main(String[] args) {
//        MemberService memberService = new MemberService();
//    }

    @Transactional
    public Long join(Member member) {

        validateDuplicateMember(member);

        memberRepository.save(member);
        return member.getId();

    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION
       List<Member> findMembers =  memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public List<Member> findMember() {
        return memberRepository.findAll();
    }
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);

        //Member를 반환하지 않는 이유 커맨드와 쿼리가 분리되지 않아서...?

    }
}
