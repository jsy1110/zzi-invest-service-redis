package zzipay.investservice.service;

import zzipay.investservice.domain.Address;
import zzipay.investservice.domain.Member;
import zzipay.investservice.exception.CustomException;
import zzipay.investservice.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@Transactional
@Rollback
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        //given
        Address address = new Address("대전시", "길거리", "123-1");
        Member member = new Member("아이유", address);

        //when
        Long savedId = memberService.join(member);

        //then
        assertThat(member).isEqualTo(memberRepository.getById(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        //given
        Address address1 = new Address("대전시", "길거리", "123-1");
        Member member1 = new Member("아이유", address1);

        Address address2 = new Address("수원시", "저거리", "321-1");
        Member member2 = new Member("아이유", address2);

        //when
        Long savedId = memberService.join(member1);

        //then
        CustomException customException = assertThrows(CustomException.class, () -> {
            memberService.join(member2);
        });

        assertEquals("중복 회원 가입 에러가 발생해야 한다..", customException.getMessage(), "회원가입을 할 수 없습니다. 중복된 이름이 있습니다.");
    }


}