package site.billingwise.api.serverapi.domain.item.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.dto.request.EditItemDto;
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
    private final S3Service s3Service;

    public String itemImageDirectory = "item";
    public String defaultImageUrl = "https://billing-wise-bucket.s3.ap-northeast-2.amazonaws.com/test.png";

    // 나중에 지우면 됩니다.
    private final ClientRepository clientRepository;

    @Transactional
    public void createItem(CreateItemDto createItemDto, MultipartFile multipartFile) {
        // 시큐리티 설정 후 바뀔 겁니다.
        Client client = clientRepository.findById((long) 3).get();

        Item item = createItemDto.toEntity(client, defaultImageUrl);
        itemRepository.save(item);

        if (multipartFile != null) {
            uploadImage(item, multipartFile);
        }

    }

    @Transactional
    public void editItem(Long itemId, EditItemDto editItemDto) {

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new GlobalException(FailureInfo.NO_ITEM));

        item.setName(editItemDto.getName());
        item.setPrice(editItemDto.getPrice());
        item.setDescription(editItemDto.getDescription());

    }

    @Transactional
    public void editItemImage(Long itemId, MultipartFile multipartFile) {

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new GlobalException(FailureInfo.NO_ITEM));
        String prevImageUrl = item.getImageUrl();

        if (multipartFile != null) {
            uploadImage(item, multipartFile);
        } else {
            throw new GlobalException(FailureInfo.INVALID_IMAGE);
        }

        s3Service.delete(prevImageUrl, itemImageDirectory);

    }

    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new GlobalException(FailureInfo.NO_ITEM));

        if (!item.getImageUrl().equals(defaultImageUrl)) {
            s3Service.delete(item.getImageUrl(), itemImageDirectory);
        }

        itemRepository.delete(item);
    }

    private void uploadImage(Item item, MultipartFile multipartFile) {

        if (!multipartFile.getContentType().startsWith("image/")) {
            throw new GlobalException(FailureInfo.INVALID_IMAGE);
        }

        String imageUrl = s3Service.upload(multipartFile, itemImageDirectory);
        item.setImageUrl(imageUrl);

    }

}
