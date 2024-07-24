package site.billingwise.api.serverapi.domain.member.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.contract.dto.request.CreateContractDto;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;
import site.billingwise.api.serverapi.domain.member.dto.response.CreateBulkResultDto;
import site.billingwise.api.serverapi.domain.member.dto.response.GetMemberDto;
import site.billingwise.api.serverapi.domain.member.service.MemberService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MemberController {
    private final MemberService memberService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    public DataResponse<Long> createMember(@Valid @RequestBody CreateMemberDto createMemberDto) {
        Long memberId = memberService.createMember(createMemberDto);

        return new DataResponse<>(SuccessInfo.MEMBER_CREATED, memberId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{memberId}")
    public DataResponse<GetMemberDto> editMember(@PathVariable("memberId") Long memberId,
            @Valid @RequestBody CreateMemberDto createMemberDto) {
        GetMemberDto getMemberDto = memberService.editMember(memberId, createMemberDto);

        return new DataResponse<>(SuccessInfo.MEMBER_UPDATED, getMemberDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{memberId}")
    public BaseResponse deleteMember(@PathVariable("memberId") Long memberId) {
        memberService.deleteMember(memberId);

        return new BaseResponse(SuccessInfo.MEMBER_DELETED);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{memberId}")
    public DataResponse<GetMemberDto> getMember(@PathVariable("memberId") Long memberId) {
        GetMemberDto getMemberDto = memberService.getMember(memberId);

        return new DataResponse<>(SuccessInfo.MEMBER_LOADED, getMemberDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public DataResponse<Page<GetMemberDto>> getMemberList(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "phone", required = false) String phone,
            Pageable pageable) {
        Page<GetMemberDto> getMemberDtoList = memberService.getMemberList(name, email, phone, pageable);

        return new DataResponse<>(SuccessInfo.MEMBER_LOADED, getMemberDtoList);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/bulk-register")
    public DataResponse<?> createMemberBulk(@RequestPart("file") MultipartFile file) {
        CreateBulkResultDto createBulkResultDto = memberService.createMemberBulk(file);

        if (createBulkResultDto.isSuccess()) {
            List<CreateMemberDto> createMemberDtoList = createBulkResultDto.getMemberList();
            return new DataResponse<>(SuccessInfo.CONTRACT_CREATED, createMemberDtoList);
        } else {
            List<String> errorList = createBulkResultDto.getErrorList();
            return new DataResponse<>(FailureInfo.INVALID_FILE, errorList);
        }
    }

}
