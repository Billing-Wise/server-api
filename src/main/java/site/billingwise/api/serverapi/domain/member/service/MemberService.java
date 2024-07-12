package site.billingwise.api.serverapi.domain.member.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.invoice.Invoice;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;
import site.billingwise.api.serverapi.domain.member.dto.response.CreateBulkResultDto;
import site.billingwise.api.serverapi.domain.member.dto.response.GetMemberDto;
import site.billingwise.api.serverapi.domain.member.repository.MemberRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.PoiUtil;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@Service
@Validated
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final Validator validator;

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

        return toGetMemberDto(member);
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

        List<GetMemberDto> getMemberDtoList = memberList.map((member) -> toGetMemberDto(member)).getContent();

        return getMemberDtoList;
    }

    @Transactional
    public CreateBulkResultDto createMemberBulk(MultipartFile file) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));
        Client client = user.getClient();

        CreateBulkResultDto createBulkResultDto = toCreateBulkResultDto(file);

        if (createBulkResultDto.isSuccess()) {
            List<Member> memberList = new ArrayList<>();
            for (CreateMemberDto createMemberDto : createBulkResultDto.getMemberList()) {
                memberList.add(createMemberDto.toEntity(client));
            }
            memberRepository.saveAll(memberList);
        }

        return createBulkResultDto;
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

    private GetMemberDto toGetMemberDto(Member member) {
        long contractCount = 0L;
        long unPaidCount = 0L;
        long totalInvoiceAmount = 0L;
        long totalUnpaidAmount = 0L;

        for (Contract contract : member.getContractList()) {
            boolean isUnpaid = false;

            for (Invoice invoice : contract.getInvoiceList()) {
                totalInvoiceAmount += invoice.getChargeAmount();
                if (invoice.getPaymentStatus().getId() == 1) {
                    totalUnpaidAmount += invoice.getChargeAmount();
                    isUnpaid = true;
                }
            }

            contractCount++;
            if (isUnpaid) {
                unPaidCount++;
            }
        }

        GetMemberDto getMemberDetailDto = GetMemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .description(member.getDescription())
                .contractCount(contractCount)
                .unPaidCount(unPaidCount)
                .totalInvoiceAmount(totalInvoiceAmount)
                .totalUnpaidAmount(totalUnpaidAmount)
                .contractCount(contractCount)
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();

        return getMemberDetailDto;
    }

    private CreateBulkResultDto toCreateBulkResultDto(MultipartFile file) {
        boolean isSuccess = true;
        List<CreateMemberDto> createMemberDtoList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                CreateMemberDto createMemberDto = CreateMemberDto.builder()
                        .name(PoiUtil.getCellValue(row.getCell(0)))
                        .email(PoiUtil.getCellValue(row.getCell(1)))
                        .phone(PoiUtil.getCellValue(row.getCell(2)))
                        .description(PoiUtil.getCellValue(row.getCell(3)))
                        .build();

                BindingResult bindingResult = new BeanPropertyBindingResult(createMemberDto, "createMemberDto");
                validator.validate(createMemberDto, bindingResult);

                if (bindingResult.hasErrors()) {
                    isSuccess = false;
                    bindingResult.getAllErrors()
                            .forEach(error -> errorList.add(row.getRowNum() + "행 : " + error.getDefaultMessage()));
                }

                if (memberRepository.existsByEmail(createMemberDto.getEmail())) {
                    isSuccess = false;
                    errorList.add(row.getRowNum() + "행 : " + "중복된 이메일입니다.");
                }

                createMemberDtoList.add(createMemberDto);
            }
        } catch (Exception e) {
            throw new GlobalException(FailureInfo.INVALID_FILE);
        }

        CreateBulkResultDto createBulkResultDto = CreateBulkResultDto.builder()
                .isSuccess(isSuccess)
                .memberList(createMemberDtoList)
                .errorList(errorList)
                .build();

        return createBulkResultDto;
    }

}
