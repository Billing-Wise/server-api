package site.billingwise.api.serverapi.domain.setting.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;
import site.billingwise.api.serverapi.domain.member.repository.MemberRepository;
import site.billingwise.api.serverapi.domain.member.service.MemberService;
import site.billingwise.api.serverapi.domain.setting.dto.request.SetBasicItemsDto;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SettingServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private SettingService settingService;

    MockedStatic<SecurityUtil> mockSecurityUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockSecurityUtil.close();
    }

    @Test
    void setBasicItems() {
        Client client = Client.builder().build();

        List<Long> itemIdList = new ArrayList<>();

        SetBasicItemsDto dto = SetBasicItemsDto.builder()
                .itemIdList(itemIdList)
                .build();

        List<Item> itemSet = new ArrayList<>();
        itemSet.add(Item.builder().isBasic(true).build());
        itemSet.add(Item.builder().isBasic(false).build());
        itemSet.add(Item.builder().isBasic(true).build());

        // given
        when(SecurityUtil.getCurrentClient()).thenReturn(client);
        when(itemRepository.findByClientAndIdIn(client, itemIdList)).thenReturn(itemSet);

        // when
        settingService.setBasicItems(dto);

        // then
        assertEquals(itemSet.get(0).getIsBasic(), false);
        assertEquals(itemSet.get(1).getIsBasic(), true);
        assertEquals(itemSet.get(2).getIsBasic(), false);

    }
}
