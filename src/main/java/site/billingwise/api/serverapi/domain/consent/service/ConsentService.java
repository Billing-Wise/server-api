package site.billingwise.api.serverapi.domain.consent.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.billingwise.api.serverapi.domain.consent.ConsentAccount;
import site.billingwise.api.serverapi.domain.consent.dto.request.RegisterConsentDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetConsentDto;
import site.billingwise.api.serverapi.domain.consent.repository.ConsentAccountRepository;
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
    private final S3Service s3Service;
    @Value("${aws.s3.sign-directory}")
    private String signImageDirectory;

    public void registerConsent(Long memberId,
                                RegisterConsentDto registerConsentDto,
                                MultipartFile multipartFile) {
        Client client = SecurityUtil.getCurrentClient();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_MEMBER));

        if (!member.getClient().getId().equals(client.getId())) {
            throw new GlobalException(FailureInfo.ACCESS_DENIED);
        }

        if (consentAccountRepository.existsById(memberId)) {
            throw new GlobalException(FailureInfo.ALREADY_EXIST_CONSENT);
        }

        String signUrl = uploadImage(multipartFile);
        consentAccountRepository.save(registerConsentDto.toEntity(member, signUrl));

    }

    private String uploadImage(MultipartFile multipartFile) {

        if (!multipartFile.getContentType().startsWith("image/")) {
            throw new GlobalException(FailureInfo.INVALID_IMAGE);
        }

        return s3Service.upload(multipartFile, signImageDirectory);
    }

    public GetConsentDto getConsent(Long memberId) {
        Client client = SecurityUtil.getCurrentClient();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_MEMBER));

        if (!member.getClient().getId().equals(client.getId())) {
            throw new GlobalException(FailureInfo.ACCESS_DENIED);
        }

        ConsentAccount consentAccount = consentAccountRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CONSENT));
        return GetConsentDto.toDto(consentAccount);
    }
}
