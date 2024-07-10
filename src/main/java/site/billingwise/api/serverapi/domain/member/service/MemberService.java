package site.billingwise.api.serverapi.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;
import site.billingwise.api.serverapi.domain.member.repository.MemberRepository;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

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
        member.setemail(createMemberDto.getEmail());
        member.setPhone(createMemberDto.getPhone());
        member.setDescription(createMemberDto.getDescription());
    }

    public void deleteMember(Long memberId) {
        Member member = getCurrentMember(memberId);

        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public Member getMember(Long memberId) {
        Member member = memberRepository.findByIdWithContractsWithInvoicesWithPaymentStatus(memberId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_MEMBER));

        return member;
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
