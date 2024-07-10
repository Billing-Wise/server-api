package site.billingwise.api.serverapi.domain.item.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.dto.request.EditItemDto;
import site.billingwise.api.serverapi.domain.item.dto.response.GetItemDto;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.service.S3Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ClientRepository clientRepository;
    private final S3Service s3Service;

    public String itemImageDirectory = "item";
    public String defaultImageUrl = "https://billing-wise-bucket.s3.ap-northeast-2.amazonaws.com/test.png";

    @Transactional
    public void createItem(CreateItemDto createItemDto, MultipartFile multipartFile) {
        Client client = clientRepository.findById(getClientId()).orElseThrow(() -> 
            new GlobalException(FailureInfo.CLIENT_NOT_FOUND)
        );

        Item item = createItemDto.toEntity(client, defaultImageUrl);
        itemRepository.save(item);

        if (multipartFile != null) {
            uploadImage(item, multipartFile);
        }

    }

    @Transactional
    public void editItem(Long itemId, EditItemDto editItemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new GlobalException(FailureInfo.ITEM_NOT_FOUND));
        isOwner(item, getClientId());

        item.setName(editItemDto.getName());
        item.setPrice(editItemDto.getPrice());
        item.setDescription(editItemDto.getDescription());

    }

    @Transactional
    public void editItemImage(Long itemId, MultipartFile multipartFile) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new GlobalException(FailureInfo.ITEM_NOT_FOUND));
        isOwner(item, getClientId());

        String prevImageUrl = item.getImageUrl();

        if (multipartFile != null) {
            uploadImage(item, multipartFile);
        } else {
            throw new GlobalException(FailureInfo.INVALID_IMAGE);
        }

        s3Service.delete(prevImageUrl, itemImageDirectory);

    }

    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new GlobalException(FailureInfo.ITEM_NOT_FOUND));
        isOwner(item, getClientId());

        if (!item.getImageUrl().equals(defaultImageUrl)) {
            s3Service.delete(item.getImageUrl(), itemImageDirectory);
        }

        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public GetItemDto getItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new GlobalException(FailureInfo.ITEM_NOT_FOUND));
        isOwner(item, getClientId());

        GetItemDto getItemDto = item.toDto();

        return getItemDto;
    }

    @Transactional(readOnly = true)
    public List<GetItemDto> getItemList(String itemName, Pageable pageable) {
        Page<Item> itemList = null;

        if (itemName == null) {
            itemList = itemRepository.findAllByClientId(pageable, getClientId());
        } else {
            itemList = itemRepository.findAllByNameContainingIgnoreCase(itemName, pageable);
        }

        List<GetItemDto> getItemDtoList = itemList.map(item -> item.toDto()).getContent();
        return getItemDtoList;
    }

    private void uploadImage(Item item, MultipartFile multipartFile) {

        if (!multipartFile.getContentType().startsWith("image/")) {
            throw new GlobalException(FailureInfo.INVALID_IMAGE);
        }

        String imageUrl = s3Service.upload(multipartFile, itemImageDirectory);
        item.setImageUrl(imageUrl);

    }

    private Long getClientId() {
        // 스프링 시큐리티를 활용한 로직 추가
        return 3L;
    }

    private void isOwner(Item item, Long clientId) {
        if (item.getClient().getId() != clientId) {
            throw new GlobalException(FailureInfo.ITEM_ACCESS_DENIED);
        }
    }

}
