package site.billingwise.api.serverapi.domain.setting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.setting.dto.request.SetBasicItemsDto;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final ItemRepository itemRepository;

    @Transactional
    public void setBasicItems(SetBasicItemsDto setBasicItemsDto) {
        List<Item> itemList = itemRepository.findByClientAndIdIn(
                SecurityUtil.getCurrentClient(),
                setBasicItemsDto.getItemIdList());

        for(Item item : itemList) {
            item.setIsBasic(true);
        }
    }
}
