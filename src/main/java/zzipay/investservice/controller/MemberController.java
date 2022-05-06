package zzipay.investservice.controller;

import zzipay.investservice.domain.Address;
import zzipay.investservice.domain.Member;
import zzipay.investservice.dto.MemberDto;
import zzipay.investservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/members/new")
    public MemberDto create (
            @RequestParam String name,
            @RequestParam String city,
            @RequestParam String street,
            @RequestParam String zipcode) {

        Address address = new Address(city, street, zipcode);
        Member member = new Member(name, address);

        Long memberId = memberService.join(member);
        MemberDto dto = MemberDto.builder()
                .memberId(memberId)
                .name(member.getName())
                .address(member.getAddress())
                .build();

        return dto;
    }
}
