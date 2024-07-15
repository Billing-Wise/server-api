package site.billingwise.api.serverapi.domain.consent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetBasicItemDto;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EasyConsentService {

    private final ItemRepository itemRepository;

    public List<GetBasicItemDto> getBasicItemList(Long clientId) {
        return itemRepository.findAllByClientIdAndIsBasic(clientId, true)
                .stream().map((item) -> GetBasicItemDto.toDto(item))
                .collect(Collectors.toList());
    }
}
