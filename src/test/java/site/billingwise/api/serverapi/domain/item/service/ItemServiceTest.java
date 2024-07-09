package site.billingwise.api.serverapi.domain.item.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.service.S3Service;

class ItemServiceTest {

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private S3Service s3Service;

	@Mock
	private ClientRepository clientRepository;

	@InjectMocks
	private ItemService itemService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createItemWithImage() {
		// Arrange
		CreateItemDto createItemDto = CreateItemDto.builder()
				.name("Test Item")
				.description("Test Description")
				.price((long) 1000)
				.build();

		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"test.jpg",
				"image/jpeg",
				"test image content".getBytes());

		Client client = Client.builder().build();
		when(clientRepository.findById(any(Long.class))).thenReturn(Optional.of(client));

		Item item = Item.builder()
				.name(createItemDto.getName())
				.description(createItemDto.getDescription())
				.price(createItemDto.getPrice())
				.client(client)
				.build();

		when(itemRepository.save(any(Item.class))).thenReturn(item);
		when(s3Service.upload(eq(multipartFile), eq("item"))).thenReturn("s3://bucket/test.jpg");

		// Act
		itemService.createItem(createItemDto, multipartFile);

		// Assert
		verify(clientRepository, times(1)).findById(any(Long.class));
		verify(itemRepository, times(2)).save(any(Item.class));
		verify(s3Service, times(1)).upload(eq(multipartFile), eq("item"));
		verifyNoMoreInteractions(itemRepository, s3Service, clientRepository);
	}

	@Test
	void createItemWithoutImage() {
		// Arrange
		CreateItemDto createItemDto = CreateItemDto.builder()
				.name("Test Item")
				.description("Test Description")
				.price((long) 1000)
				.build();

		Client client = Client.builder().build();
		when(clientRepository.findById(any(Long.class))).thenReturn(Optional.of(client));

		Item item = Item.builder()
				.name(createItemDto.getName())
				.description(createItemDto.getDescription())
				.price(createItemDto.getPrice())
				.client(client)
				.build();

		when(itemRepository.save(any(Item.class))).thenReturn(item);

		// Act
		itemService.createItem(createItemDto, null);

		// Assert
		verify(clientRepository, times(1)).findById(any(Long.class));
		verify(itemRepository, times(1)).save(any(Item.class));
		verifyNoMoreInteractions(itemRepository, s3Service, clientRepository);
	}

	@Test
	void editItemWithValidImage() {
		// Arrange
		Long itemId = 1L;
		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"test.png",
				"image/png",
				"some-image".getBytes());

		Item item = Item.builder().id(itemId).build();

		when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
		when(s3Service.upload(any(), any())).thenReturn("image-url");

		// Act
		itemService.editItemImage(itemId, multipartFile);

		// Assert
		verify(itemRepository).findById(itemId);
		verify(s3Service).upload(multipartFile, "item");
		verify(itemRepository).save(item);
	}

	@Test
	void editItemWithInvalidImage() {
		// Arrange
		Long itemId = 1L;
		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"test.txt",
				"text/plain",
				"some-text".getBytes());

		Item item = Item.builder().id(itemId).build();

		when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

		// Act
		GlobalException exception = assertThrows(GlobalException.class, () -> {
			itemService.editItemImage(itemId, multipartFile);
		});

		// Assert
		verify(itemRepository).findById(itemId);
		assert (exception.getFailureInfo().equals(FailureInfo.INVALID_IMAGE));
	}

	@Test
	void editNoItem() {
		// Arrange
		Long itemId = 1L;
		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"test.png",
				"image/png",
				"some-image".getBytes());

		when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

		// Act
		GlobalException exception = assertThrows(GlobalException.class, () -> {
			itemService.editItemImage(itemId, multipartFile);
		});

		// Assert
		verify(itemRepository).findById(itemId);
		assert (exception.getFailureInfo().equals(FailureInfo.NO_ITEM));
	}

}