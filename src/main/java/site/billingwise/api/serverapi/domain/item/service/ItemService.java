package site.billingwise.api.serverapi.domain.item.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.global.service.S3Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final S3Service s3Service;

    // 나중에 지우면 됩니다.
    private final ClientRepository clientRepository;
    
    @Transactional
    public void createItem(CreateItemDto createItemDto, MultipartFile multipartFile) {
        // 시큐리티 설정 후 바뀔 겁니다.
        Client client = clientRepository.findById((long)3).get();

        Item item = createItemDto.toEntity(client);
        itemRepository.save(item);

        if (multipartFile != null) {
            String imageUrl = s3Service.upload(multipartFile, "item");
            item.setImageUrl(imageUrl);
    
            itemRepository.save(item);
        } 

    }

}
