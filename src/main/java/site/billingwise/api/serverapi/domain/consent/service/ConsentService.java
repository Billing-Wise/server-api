package site.billingwise.api.serverapi.domain.consent.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.billingwise.api.serverapi.domain.consent.ConsentAccount;
import site.billingwise.api.serverapi.domain.consent.dto.request.RegisterConsentDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetConsentDto;
import site.billingwise.api.serverapi.domain.consent.repository.ConsentAccountRepository;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.contract.repository.ContractRepository;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.repository.MemberRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.service.S3Service;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@RequiredArgsConstructor
@Service
public class ConsentService {

    private final ConsentAccountRepository consentAccountRepository;
    private final MemberRepository memberRepository;
    private final ContractRepository contractRepository;
    private final S3Service s3Service;
    @Value("${aws.s3.sign-directory}")
    private String signImageDirectory;

    public GetConsentDto registerConsent(Long memberId,
            RegisterConsentDto registerConsentDto,
            MultipartFile multipartFile) {
        Member member = checkMemberPermission(memberId);

        if (consentAccountRepository.existsById(memberId)) {
            throw new GlobalException(FailureInfo.ALREADY_EXIST_CONSENT);
        }

        List<Contract> contractList = contractRepository
                .findAllByMemberAndPaymentTypeAndContractStatus(
                        member,
                        PaymentType.AUTO_TRANSFER,
                        ContractStatus.PENDING);

        for (Contract c : contractList) {
            c.setContractStatus(ContractStatus.PROGRESS);
        }

        String signUrl = uploadImage(multipartFile);
        ConsentAccount consentAccount =  consentAccountRepository.save(registerConsentDto.toEntity(member, signUrl));

        return GetConsentDto.toDto(consentAccount);
    }

    public String uploadImage(MultipartFile multipartFile) {

        if (!multipartFile.getContentType().startsWith("image/")) {
            throw new GlobalException(FailureInfo.INVALID_IMAGE);
        }

        return s3Service.upload(multipartFile, signImageDirectory);
    }

    public GetConsentDto getConsent(Long memberId) {
        checkMemberPermission(memberId);

        ConsentAccount consentAccount = consentAccountRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CONSENT));
        return GetConsentDto.toDto(consentAccount);
    }

    @Transactional
    public GetConsentDto editConsent(Long memberId, RegisterConsentDto editConsentDto) {
        checkMemberPermission(memberId);

        ConsentAccount consentAccount = consentAccountRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CONSENT));

        consentAccount.update(editConsentDto);

        return GetConsentDto.toDto(consentAccount);
    }

    public Member checkMemberPermission(Long memberId) {
        Client client = SecurityUtil.getCurrentClient();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_MEMBER));

        if (!member.getClient().getId().equals(client.getId())) {
            throw new GlobalException(FailureInfo.ACCESS_DENIED);
        }

        return member;
    }

    @Transactional
    public GetConsentDto editConsentSignImage(Long memberId, MultipartFile multipartFile) {
        checkMemberPermission(memberId);

        ConsentAccount consentAccount = consentAccountRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CONSENT));

        String prevSignUrl = consentAccount.getSignUrl();
        s3Service.delete(prevSignUrl, signImageDirectory);

        String newSignUrl = uploadImage(multipartFile);
        consentAccount.setSignUrl(newSignUrl);

        return GetConsentDto.toDto(consentAccount);
    }

    public void deleteConsent(Long memberId) {
        checkMemberPermission(memberId);

        ConsentAccount consentAccount = consentAccountRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CONSENT));

        s3Service.delete(consentAccount.getSignUrl(), signImageDirectory);

        consentAccountRepository.delete(consentAccount);
    }
}
