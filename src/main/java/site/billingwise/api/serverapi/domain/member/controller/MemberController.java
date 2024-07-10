package site.billingwise.api.serverapi.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;
import site.billingwise.api.serverapi.domain.member.service.MemberService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MemberController {
    private final MemberService memberService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    public BaseResponse createMember(@Valid @RequestBody CreateMemberDto createMemberDto) {
        memberService.createMember(createMemberDto);

        return new BaseResponse(SuccessInfo.MEMBER_CREATED);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{memberId}")
    public BaseResponse editMember(@PathVariable("memberId") Long memberId,
            @Valid @RequestBody CreateMemberDto createMemberDto) {
        memberService.editMember(memberId, createMemberDto);

        return new BaseResponse(SuccessInfo.MEMBER_UPDATED);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{memberId}")
    public BaseResponse deleteMember(@PathVariable("memberId") Long memberId) {
        memberService.deleteMember(memberId);

        return new BaseResponse(SuccessInfo.MEMBER_UPDATED);
    }

}
