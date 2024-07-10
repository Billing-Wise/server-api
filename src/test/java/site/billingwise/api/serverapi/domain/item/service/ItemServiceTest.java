package site.billingwise.api.serverapi.domain.item.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.dto.request.EditItemDto;
import site.billingwise.api.serverapi.domain.item.dto.response.GetItemDto;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.service.S3Service;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

class ItemServiceTest {

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private S3Service s3Service;

	@Mock
	private ClientRepository clientRepository;

	@InjectMocks
	private ItemService itemService;

	MockedStatic<SecurityUtil> mockSecurityUtil;

	private User mockUser;
	private Item mockItem;
	private Client mockClient;

	private static final String defaultImageUrl = "http://default-image-url.com";
	private static final String itemImageDirectory = "item";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(itemService, "defaultImageUrl", defaultImageUrl);
		ReflectionTestUtils.setField(itemService, "itemImageDirectory", itemImageDirectory);

		mockSecurityUtil = mockStatic(SecurityUtil.class);

		mockClient = Client.builder()
				.id(1L)
				.build();

		mockUser = User.builder()
				.id(1L)
				.client(mockClient)
				.build();

		mockItem = Item.builder()
				.id(1L)
				.name("Old Name")
				.price(1000L)
				.description("Old Description")
				.imageUrl("test.png")
				.client(mockClient)
				.build();
	}

	@AfterEach
	void tearDown() {
		mockSecurityUtil.close();
	}

	@Test
	void createItemWithImage() {
		// given
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

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));

		Item item = createItemDto.toEntity(mockUser.getClient(), defaultImageUrl);

		when(itemRepository.save(any(Item.class))).thenReturn(item);
		when(s3Service.upload(eq(multipartFile), eq(itemImageDirectory))).thenReturn("s3://bucket/test.jpg");

		// when
		itemService.createItem(createItemDto, multipartFile);

		// then

		verify(itemRepository, times(1)).save(any(Item.class));
		verify(s3Service, times(1)).upload(eq(multipartFile), eq(itemImageDirectory));
		verifyNoMoreInteractions(itemRepository, s3Service, clientRepository);
	}

	@Test
	void createItemWithoutImage() {
		// given
		CreateItemDto createItemDto = CreateItemDto.builder()
				.name("Test Item")
				.description("Test Description")
				.price((long) 1000)
				.build();

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));

		Item item = createItemDto.toEntity(mockUser.getClient(), defaultImageUrl);

		when(itemRepository.save(any(Item.class))).thenReturn(item);

		// when
		itemService.createItem(createItemDto, null);

		// then
		verify(itemRepository, times(1)).save(any(Item.class));
		verifyNoMoreInteractions(itemRepository, s3Service, clientRepository);
	}

	@Test
	void editItem() {
		// given
		Long itemId = 6L;

		EditItemDto editItemDto = EditItemDto.builder()
				.name("New Name")
				.price(1000L)
				.description("New Description")
				.build();

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));

		// when
		itemService.editItem(itemId, editItemDto);

		// then
		verify(itemRepository, times(1)).findById(itemId);

		assert (mockItem.getName().equals("New Name"));
		assert (mockItem.getPrice().equals(1000L));
		assert (mockItem.getDescription().equals("New Description"));
	}

	@Test
	void editItemNotFound() {
		// given
		Long itemId = 1L;
		EditItemDto editItemDto = EditItemDto.builder()
				.name("New Name")
				.price(1000L)
				.description("New Description")
				.build();

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(GlobalException.class, () -> itemService.editItem(itemId, editItemDto));
		verify(itemRepository, times(1)).findById(itemId);
	}

	@Test
	void editItemWithValidImage() {
		// given
		Long itemId = 1L;
		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"test.png",
				"image/png",
				"some-image".getBytes());

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
		when(s3Service.upload(any(), any())).thenReturn("image-url");

		// when
		itemService.editItemImage(itemId, multipartFile);

		// then
		verify(itemRepository).findById(itemId);
		verify(s3Service).upload(multipartFile, itemImageDirectory);
	}

	@Test
	void editItemWithInvalidImage() {
		// given
		Long itemId = 1L;
		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"test.txt",
				"text/plain",
				"some-text".getBytes());

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));

		// when
		GlobalException exception = assertThrows(GlobalException.class, () -> {
			itemService.editItemImage(itemId, multipartFile);
		});

		// then
		verify(itemRepository).findById(itemId);
		assert (exception.getFailureInfo().equals(FailureInfo.INVALID_IMAGE));
	}

	@Test
	void editItemImageNotFound() {
		// given
		Long itemId = 1L;
		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"test.png",
				"image/png",
				"some-image".getBytes());

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when
		GlobalException exception = assertThrows(GlobalException.class, () -> {
			itemService.editItemImage(itemId, multipartFile);
		});

		// then
		verify(itemRepository).findById(itemId);
		assert (exception.getFailureInfo().equals(FailureInfo.ITEM_NOT_FOUND));
	}

	@Test
	void deleteItemNoDefaultImage() {
		// given
		Long itemId = 1L;
		String imageUrl = "https://example.com/image.jpg";

		Item item = Item.builder()
				.id(itemId)
				.imageUrl(imageUrl)
				.client(mockClient)
				.build();

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

		// when
		itemService.deleteItem(itemId);

		// then
		verify(s3Service).delete(eq(item.getImageUrl()), eq(itemImageDirectory));
		verify(itemRepository).delete(eq(item));
	}

	@Test
	void deleteItemDefaultImage() {
		// given
		Long itemId = 1L;

		Item item = Item.builder()
				.id(itemId)
				.imageUrl(itemService.defaultImageUrl)
				.client(mockClient)
				.build();

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

		// when
		itemService.deleteItem(itemId);

		// then
		verify(s3Service, never()).delete(anyString(), anyString());
		verify(itemRepository).delete(eq(item));
	}

	@Test
	void deleteItemNotFound() {
		// given
		Long itemId = 1L;

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

		// when, then
		assertThrows(GlobalException.class, () -> itemService.deleteItem(itemId));
	}

	@Test
	void getItem() {
		// given
		Long itemId = 1L;

		Item item = Item.builder()
				.id(itemId)
				.name("Name")
				.price(1000L)
				.client(mockClient)
				.description("Item Description")
				.imageUrl("http://example.com/image.jpg")
				.contractCount(5L)
				.build();

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

		// when
		GetItemDto getItemDto = itemService.getItem(itemId);

		// then
		assertThat(getItemDto.getId()).isEqualTo(itemId);
		assertThat(getItemDto.getName()).isEqualTo("Name");
		assertThat(getItemDto.getPrice()).isEqualTo(1000L);
		assertThat(getItemDto.getDescription()).isEqualTo("Item Description");
		assertThat(getItemDto.getImageUrl()).isEqualTo("http://example.com/image.jpg");
		assertThat(getItemDto.getCreatedAt()).isEqualTo(item.getCreatedAt());
		assertThat(getItemDto.getUpdatedAt()).isEqualTo(item.getUpdatedAt());
		assertThat(getItemDto.getContractCount()).isEqualTo(5L);

		verify(itemRepository).findById(itemId);
	}

	@Test
	void getItemNotFound() {
		// given
		Long itemId = 1L;
		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		given(itemRepository.findById(itemId)).willReturn(Optional.empty());

		// when
		GlobalException exception = assertThrows(GlobalException.class, () -> itemService.getItem(itemId));

		// then
		assertThat(exception.getFailureInfo()).isEqualTo(FailureInfo.ITEM_NOT_FOUND);

		verify(itemRepository).findById(itemId);
	}

	@Test
	void getItemListAll() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Item item1 = Item.builder()
				.id(1L)
				.name("Item 1")
				.price(1000L)
				.client(mockClient) // assuming mockClient is a mock or a test object
				.description("Item 1 Description")
				.imageUrl("http://example.com/item1.jpg")
				.contractCount(5L)
				.build();
		Item item2 = Item.builder()
				.id(2L)
				.name("Item 2")
				.price(2000L)
				.client(mockClient)
				.description("Item 2 Description")
				.imageUrl("http://example.com/item2.jpg")
				.contractCount(10L)
				.build();

		List<Item> items = Arrays.asList(item1, item2);
		Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		given(itemRepository.findAllByClientId(any(Pageable.class), any(Long.class))).willReturn(itemPage);

		// when
		List<GetItemDto> itemList = itemService.getItemList(null, pageable);

		// then
		assertThat(itemList).hasSize(2);
		assertThat(itemList.get(0).getId()).isEqualTo(1L);
		assertThat(itemList.get(0).getName()).isEqualTo("Item 1");
		assertThat(itemList.get(0).getPrice()).isEqualTo(1000L);
		assertThat(itemList.get(0).getDescription()).isEqualTo("Item 1 Description");
		assertThat(itemList.get(0).getImageUrl()).isEqualTo("http://example.com/item1.jpg");
		assertThat(itemList.get(0).getContractCount()).isEqualTo(5L);

		assertThat(itemList.get(1).getId()).isEqualTo(2L);
		assertThat(itemList.get(1).getName()).isEqualTo("Item 2");
		assertThat(itemList.get(1).getPrice()).isEqualTo(2000L);
		assertThat(itemList.get(1).getDescription()).isEqualTo("Item 2 Description");
		assertThat(itemList.get(1).getImageUrl()).isEqualTo("http://example.com/item2.jpg");
		assertThat(itemList.get(1).getContractCount()).isEqualTo(10L);

		verify(itemRepository).findAllByClientId(any(Pageable.class), any(Long.class));
	}

	@Test
	void getItemListSearch() {
		// given
		String itemName = "Item";
		Pageable pageable = PageRequest.of(0, 10);
		Item item1 = Item.builder()
				.id(1L)
				.name("Item 1")
				.price(1000L)
				.client(mockClient)
				.description("Item 1 Description")
				.imageUrl("http://example.com/item1.jpg")
				.contractCount(5L)
				.build();
		Item item2 = Item.builder()
				.id(2L)
				.name("Item 2")
				.price(2000L)
				.client(mockClient)
				.description("Item 2 Description")
				.imageUrl("http://example.com/item2.jpg")
				.contractCount(10L)
				.build();

		List<Item> items = Arrays.asList(item1, item2);
		Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());
		
		when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
		given(itemRepository.findAllByNameContainingIgnoreCaseAndClientId(anyString(), any(Pageable.class), anyLong()))
				.willReturn(itemPage);

		// when
		List<GetItemDto> itemList = itemService.getItemList(itemName, pageable);

		// then
		assertThat(itemList).hasSize(2);
		assertThat(itemList.get(0).getId()).isEqualTo(1L);
		assertThat(itemList.get(0).getName()).isEqualTo("Item 1");
		assertThat(itemList.get(0).getPrice()).isEqualTo(1000L);
		assertThat(itemList.get(0).getDescription()).isEqualTo("Item 1 Description");
		assertThat(itemList.get(0).getImageUrl()).isEqualTo("http://example.com/item1.jpg");
		assertThat(itemList.get(0).getContractCount()).isEqualTo(5L);

		assertThat(itemList.get(1).getId()).isEqualTo(2L);
		assertThat(itemList.get(1).getName()).isEqualTo("Item 2");
		assertThat(itemList.get(1).getPrice()).isEqualTo(2000L);
		assertThat(itemList.get(1).getDescription()).isEqualTo("Item 2 Description");
		assertThat(itemList.get(1).getImageUrl()).isEqualTo("http://example.com/item2.jpg");
		assertThat(itemList.get(1).getContractCount()).isEqualTo(10L);

		verify(itemRepository).findAllByNameContainingIgnoreCaseAndClientId(anyString(), any(Pageable.class), anyLong());
	}

}