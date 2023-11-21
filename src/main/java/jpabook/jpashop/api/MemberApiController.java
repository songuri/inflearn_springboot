package jpabook.jpashop.api;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController // 이 안에 @Controller @ResponseBody 이 두가지가 같이 있음.
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;


    // 가장 안좋은 버전의 조회
    // 엔티티가 직접 노출됨(가장 중요!! 이러면 안돼!!)
    // 만약 회원의 order 정보를 노출시키고 싶지 않다면
    // @JsonIgnore 태그를 Orders 변수 위에 선언하면 됨
    // 하지만 저 JsonIgnore 선언을 한다는거 자체가 화면을 위한 로직이 Entity에 들어간거임
    // 이건 무조건 피해야 함! (엔티티 좀만 바뀌면 바로 장애임 ㅋ)
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMember();
    }


    //이렇게 노출할것만 DTO로 만들어서 하는게 좋다.
    // 절대 Entity를 노출 하지 맙시다!!
    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMember();
        List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(),collect) ;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count; //카운트 추가 하는 방법
        private T data;

    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }



    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        //PostMan에서 /api/v1/members 이거 쏘면 잘 실행됨.
        // 여기서 빈값을 보내도 생성이 되는데, Validation 체크를 하고 싶으면 @Valid 넣고 Member에서 원하는 변수에 @NotEmpty선언 하면 됨
        // 여기서 문제는 모든 검증에 대한 로직이 Entity쪽에 들어가 있는것! API마다 상황이 다를 수 있는데 이러면 문제가 있을 수 있음.
        // 그리고 Entity가 바뀌면 API SPEC 자체가 바뀌어 버림. 그리고 ENTITY 자체가 그대로 노출 되는 것도 문제임.
        // 이런거 때문에 DTO가 필요!

        //RequestBody <- Json을 Member로 바꿔줌.
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreasteMemberRequest request) {
        // 이렇게 하면 Entity에서 name -> userName으로 바뀌어도 애초에 컴파일 에러가 발생해서 파악하기 쉬움.
        Member member = new Member();
        member.setName(request.name);

        Long id = memberService.join(member);
        return new  CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMEmberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        //  여러분 업데이트는 되도록 변경감지를 쓰는게 좋다고 했죠?
        //  어설프게 Controller에서 엔티티 만들지 말자!
        memberService.update(id, request.getName());
        // 그냥 쿼리를 다시 짜서 가져오는게 정석임
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;

    }

    @Data static class UpdateMemberRequest {

        private String name;
//        public UpdateMemberRequest(String name) {
//            this.name = name;
//        }

    }



    /**
     * 이게 DTO
     */
    @Data
    static class CreasteMemberRequest {
        @NotEmpty
        public String name;

        // 왜 생성자가 있으면 오류가 발생하는 거지?
//        public CreasteMemberRequest(String name) {
//            this.name = name;
//        }
    }


    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }

    }
}
