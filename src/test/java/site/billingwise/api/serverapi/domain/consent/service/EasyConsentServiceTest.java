package site.billingwise.api.serverapi.domain.consent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetBasicItemDto;
import site.billingwise.api.serverapi.domain.consent.repository.ConsentAccountRepository;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class EasyConsentServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private EasyConsentService easyConsentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBasicItemList() {
        Long clientId = 1L;

        // Given
        List<Item> items = Arrays.asList(
                Item.builder()
                        .id(1L)
                        .name("test1")
                        .price(1000L)
                        .imageUrl("test1.png")
                        .build(),
                Item.builder()
                        .id(2L)
                        .name("test2")
                        .price(2000L)
                        .imageUrl("test2.png")
                        .build()
        );
        when(itemRepository.findAllByClientIdAndIsBasic(clientId, true)).thenReturn(items);

        // When
        List<GetBasicItemDto> result = easyConsentService.getBasicItemList(clientId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("test1");
        assertThat(result.get(0).getPrice()).isEqualTo(1000);
        assertThat(result.get(0).getImageUrl()).isEqualTo("test1.png");

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("test2");
        assertThat(result.get(1).getPrice()).isEqualTo(2000);
        assertThat(result.get(1).getImageUrl()).isEqualTo("test2.png");
    }
}
