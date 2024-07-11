package site.billingwise.api.serverapi.domain.member.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;
import site.billingwise.api.serverapi.domain.member.dto.response.GetMemberDto;
import site.billingwise.api.serverapi.domain.member.repository.MemberRepository;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public void createMember(CreateMemberDto createMemberDto) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        if (memberRepository.existsByEmail(createMemberDto.getEmail())) {
            throw new GlobalException(FailureInfo.ALREADY_EXIST_EMAIL);
        }

        Member member = createMemberDto.toEntity(user.getClient());

        memberRepository.save(member);
    }

    @Transactional
    public void editMember(Long memberId, CreateMemberDto createMemberDto) {
        Member member = getCurrentMember(memberId);

        if (memberRepository.existsByEmail(createMemberDto.getEmail())) {
            throw new GlobalException(FailureInfo.ALREADY_EXIST_EMAIL);
        }

        member.setName(createMemberDto.getName());
        member.setEmail(createMemberDto.getEmail());
        member.setPhone(createMemberDto.getPhone());
        member.setDescription(createMemberDto.getDescription());
    }

    public void deleteMember(Long memberId) {
        Member member = getCurrentMember(memberId);

        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public GetMemberDto getMember(Long memberId) {
        Member member = memberRepository.findByIdWithContractsWithInvoices(memberId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_MEMBER));

        return member.toDto();
    }

    @Transactional(readOnly = true)
    public List<GetMemberDto> getMemberList(String memberName, Pageable pageable) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Page<Member> memberList = null;

        if (memberName == null) {
            memberList = memberRepository.findByClientIdWithContractsWithInvoices(user.getClient().getId(), pageable);
        } else {
            memberList = memberRepository.findByClientIdAndNameWithContractsWithInvoices(user.getClient().getId(),
                    memberName, pageable);
        }

        List<GetMemberDto> getMemberDtoList = memberList.map((member) -> member.toDto()).getContent();

        return getMemberDtoList;
    }

    private Member getCurrentMember(Long memberId) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_MEMBER));

        if (member.getClient().getId() != user.getClient().getId()) {
            throw new GlobalException(FailureInfo.MEMBER_ACCESS_DENIED);
        }

        return member;
    }
}
