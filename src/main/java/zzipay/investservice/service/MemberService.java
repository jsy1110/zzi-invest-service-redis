package zzipay.investservice.service;

import zzipay.investservice.domain.Member;
import zzipay.investservice.exception.CustomException;
import zzipay.investservice.exception.ExceptionEnum;
import zzipay.investservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //Exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) {
            log.info("Try to register duplicate member. memberName = {}", member.getName());
            throw new CustomException(ExceptionEnum.DUPLICATE_MEMBER);
        }
    }

}
