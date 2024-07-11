package site.billingwise.api.serverapi.domain.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;
import site.billingwise.api.serverapi.domain.member.dto.response.GetMemberDto;
import site.billingwise.api.serverapi.domain.member.repository.MemberRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    MockedStatic<SecurityUtil> mockSecurityUtil;

    private CreateMemberDto createMemberDto;
    private User mockUser;
    private Client mockClient;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockSecurityUtil = mockStatic(SecurityUtil.class);

        mockClient = Client.builder().id(1L).build();

        mockUser = User.builder().client(mockClient).build();

        createMemberDto = CreateMemberDto.builder()
                .name("kim")
                .email("example@example.com")
                .phone("010-1234-5678")
                .description("Test description")
                .build();

        mockMember = Member.builder()
                .id(1L)
                .client(mockClient)
                .name("kim")
                .email("example@example.com")
                .phone("010-1234-5678")
                .description("Test description")
                .contractList(new HashSet<>())
                .build();
    }

    @AfterEach
    void tearDown() {
        mockSecurityUtil.close();
    }

    @Test
    void createMember() {
        // given
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(memberRepository.existsByEmail(createMemberDto.getEmail())).thenReturn(false);

        // when
        memberService.createMember(createMemberDto);

        // then
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void createMemberEmailExist() {
        // given
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(memberRepository.existsByEmail(createMemberDto.getEmail())).thenReturn(true);

        // when, then
        assertThrows(GlobalException.class, () -> memberService.createMember(createMemberDto));
    }

    @Test
    void editMember() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(memberRepository.existsByEmail(createMemberDto.getEmail())).thenReturn(false);

        // when
        memberService.editMember(1L, createMemberDto);

        // then
        assertEquals(createMemberDto.getName(), mockMember.getName());
        assertEquals(createMemberDto.getEmail(), mockMember.getEmail());
        assertEquals(createMemberDto.getPhone(), mockMember.getPhone());
        assertEquals(createMemberDto.getDescription(), mockMember.getDescription());
    }

    @Test
    void editMemberEmailExist() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember));
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(memberRepository.existsByEmail(createMemberDto.getEmail())).thenReturn(true);

        // when, then
        assertThrows(GlobalException.class, () -> memberService.editMember(1L, createMemberDto));
    }

    @Test
    void deleteMember() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));

        // when
        memberService.deleteMember(1L);

        // then
        verify(memberRepository).delete(mockMember);
    }

    @Test
    void getMember() {
        when(memberRepository.findByIdWithContractsWithInvoices(anyLong())).thenReturn(Optional.of(mockMember));
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));

        // when
        GetMemberDto result = memberService.getMember(1L);

        // then
        assertNotNull(result);
        assertEquals(mockMember.getId(), result.getId());
        assertEquals(mockMember.getName(), result.getName());
        assertEquals(mockMember.getEmail(), result.getEmail());
        assertEquals(mockMember.getPhone(), result.getPhone());
        assertEquals(mockMember.getDescription(), result.getDescription());
    }

    @Test
    public void getMemberList() {
        // given
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        List<Member> memberList = new ArrayList<>();
        memberList.add(mockMember);

        Page<Member> memberPage = new PageImpl<>(memberList, PageRequest.of(0, 10), memberList.size());

        when(memberRepository.findByClientIdWithContractsWithInvoices(anyLong(), any(Pageable.class)))
                .thenReturn(memberPage);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<GetMemberDto> result = memberService.getMemberList(null, pageable);

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(mockMember.getId(), result.get(0).getId());
    }

    @Test
    public void testGetMemberListWithName() {
        // given
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        List<Member> memberList = new ArrayList<>();
        memberList.add(mockMember);

        Page<Member> memberPage = new PageImpl<>(memberList, PageRequest.of(0, 10), memberList.size());

        when(memberRepository.findByClientIdAndNameWithContractsWithInvoices(anyLong(), anyString(),
                any(Pageable.class)))
                .thenReturn(memberPage);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<GetMemberDto> result = memberService.getMemberList("Member 1", pageable);

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(mockMember.getId(), result.get(0).getId());
    }

}
