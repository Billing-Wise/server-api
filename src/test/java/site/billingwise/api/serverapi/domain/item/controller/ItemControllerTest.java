package site.billingwise.api.serverapi.domain.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.servlet.http.Cookie;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.dto.request.EditItemDto;
import site.billingwise.api.serverapi.domain.item.dto.response.GetItemDto;
import site.billingwise.api.serverapi.domain.item.service.ItemService;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.global.service.S3Service;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest extends AbstractRestDocsTests {

	static final Long ITEM_ID = 3L;

	@MockBean
	ItemService itemService;

	@MockBean
	S3Service s3Service;

	@MockBean
	ClientRepository clientRepository;

	@Test
	@DisplayName("상품 생성")
	void createItem() throws Exception {
		String url = "/api/v1/items";

		// given
		CreateItemDto createItemDto = CreateItemDto.builder()
				.name("NAME")
				.price((long) 10000)
				.description("Description")
				.build();

		String createItemJsonDto = objectMapper.writeValueAsString(createItemDto);

		MockMultipartFile data = new MockMultipartFile("data", "item", "application/json",
				createItemJsonDto.getBytes(
						StandardCharsets.UTF_8));

		MockMultipartFile itemImage = new MockMultipartFile(
				"image", "item.png", "image/png", "item data".getBytes());

		willDoNothing().given(itemService).createItem(createItemDto, itemImage);

		// when
		ResultActions result = mockMvc.perform(multipart(url)
				.file(data)
				.file(itemImage)
				.cookie(new Cookie("access", "ACCESS_TOKEN"))
				.contentType(MediaType.MULTIPART_FORM_DATA));

		// then
		result.andExpect(status().isOk())
				.andDo(document("item/create",
						requestCookies(
								cookieWithName("access").description("엑세스 토큰")),
						requestParts(
								partWithName("data").description("상품 정보"),
								partWithName("image").description("상품 이미지")),
						requestPartFields("data",
								fieldWithPath("name").description("상품명 (* required)")
										.type(JsonFieldType.STRING),
								fieldWithPath("price").description("상품 가격 (* required)")
										.type(JsonFieldType.NUMBER),
								fieldWithPath("description").description("상품 상세 설명")
										.type(JsonFieldType.STRING))));

	}

	@Test
	@DisplayName("상품 정보 수정")
	void editItem() throws Exception {

		// given
		String url = "/api/v1/items/{itemId}";

		EditItemDto editItemDto = EditItemDto.builder()
				.name("UPDATED")
				.price(100000L)
				.description("UPDATED")
				.build();

		willDoNothing().given(itemService).editItem(anyLong(), eq(editItemDto));

		// when
		ResultActions result = mockMvc.perform(put(url, ITEM_ID)
				.cookie(new Cookie("access", "ACCESS_TOKEN"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editItemDto)));

		// then
		result.andExpect(status().isOk()).andDo(document("item/edit-info",
				requestCookies(
						cookieWithName("access").description("엑세스 토큰")),
				pathParameters(
						parameterWithName("itemId").description("상품 ID")),
				requestFields(
						fieldWithPath("name").description("상품명 (* required)").type(JsonFieldType.STRING),
						fieldWithPath("price").description("상품 가격 (* required)").type(JsonFieldType.NUMBER),
						fieldWithPath("description").description("상세 설명").type(JsonFieldType.STRING))));
	}

	@Test
	@DisplayName("상품 이미지 수정")
	void editItemImage() throws Exception {
		// given
		String url = "/api/v1/items/{itemId}/image";

		MockMultipartFile itemImage = new MockMultipartFile(
				"image", "item.png", "image/png", "item data".getBytes());

		willDoNothing().given(itemService).editItemImage(anyLong(), eq(itemImage));

		// when
		ResultActions result = mockMvc.perform(multipart(url, ITEM_ID)
				.file(itemImage)
				.cookie(new Cookie("access", "ACCESS_TOKEN"))
				.with(request -> {
					request.setMethod("PUT");
					return request;
				}));

		// then
		result.andExpect(status().isOk())
				.andDo(document("item/edit-image",
						requestCookies(
								cookieWithName("access").description("엑세스 토큰")),
						pathParameters(
								parameterWithName("itemId").description("상품 ID")),
						requestParts(
								partWithName("image").description("상품 이미지"))));
	}

	@Test
	@DisplayName("상품 삭제")
	void deleteItem() throws Exception {

		// given
		String url = "/api/v1/items/{itemId}";

		willDoNothing().given(itemService).deleteItem(ITEM_ID);

		// when
		ResultActions result = mockMvc.perform(delete(url, ITEM_ID)
				.cookie(new Cookie("access", "ACCESS_TOKEN")));

		// then
		result.andExpect(status().isOk())
				.andDo(document("item/delete",
						requestCookies(
								cookieWithName("access").description("엑세스 토큰")),
						pathParameters(
								parameterWithName("itemId").description("상품 ID"))));
	}

	@Test
	@DisplayName("상품 상세 조회")
	void getItem() throws Exception {
		// given
		String url = "/api/v1/items/{itemId}";

		GetItemDto getItemDto = GetItemDto.builder()
				.id(1L)
				.name("Name")
				.description("Item Description")
				.price(1000L)
				.imageUrl("http://example.com/image.jpg")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.contractCount(5L)
				.build();

		given(itemService.getItem(anyLong())).willReturn(getItemDto);

		// when
		ResultActions result = mockMvc.perform(get(url, 1L)
				.cookie(new Cookie("access", "ACCESS_TOKEN")));

		// then
		result.andExpect(status().isOk())
				.andDo(document("item/get",
						requestCookies(
								cookieWithName("access").description("엑세스 토큰")),
						pathParameters(
								parameterWithName("itemId").description("상품ID")),
						responseFields(
								fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
								fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
								fieldWithPath("data").description("응답 데이터").type(JsonFieldType.OBJECT),
								fieldWithPath("data.id").description("상품 ID").type(JsonFieldType.NUMBER),
								fieldWithPath("data.name").description("상품명").type(JsonFieldType.STRING),
								fieldWithPath("data.description").description("상품 설명")
										.type(JsonFieldType.STRING),
								fieldWithPath("data.price").description("상품 가격").type(JsonFieldType.NUMBER),
								fieldWithPath("data.imageUrl").description("상품 이미지 URL")
										.type(JsonFieldType.STRING),
								fieldWithPath("data.createdAt").description("상품 생성일")
										.type(JsonFieldType.STRING),
								fieldWithPath("data.updatedAt").description("상품 정보 수정일")
										.type(JsonFieldType.STRING),
								fieldWithPath("data.contractCount").description("관련 계약수")
										.type(JsonFieldType.NUMBER))));
	}

	@Test
	@DisplayName("상품 목록 조회")
	void getItemList() throws Exception {
		// given
		String url = "/api/v1/items";

		GetItemDto item1 = GetItemDto.builder()
				.id(1L)
				.name("Item 1")
				.description("Item 1 Description")
				.price(1000L)
				.imageUrl("http://example.com/item1.jpg")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.contractCount(5L)
				.build();

		GetItemDto item2 = GetItemDto.builder()
				.id(2L)
				.name("Item 2")
				.description("Item 2 Description")
				.price(2000L)
				.imageUrl("http://example.com/item2.jpg")
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.contractCount(10L)
				.build();

		List<GetItemDto> itemList = Arrays.asList(item1, item2);

		PageImpl<GetItemDto> page = new PageImpl<>(itemList, PageRequest.of(0, 20), itemList.size());
		given(itemService.getItemList(anyString(), any(Pageable.class))).willReturn(page);

		// when
		ResultActions result = mockMvc.perform(get(url)
				.param("name", "Item")
				.cookie(new Cookie("access", "ACCESS_TOKEN"))
				.contentType(MediaType.APPLICATION_JSON));

		// then
		result.andDo(document("item/get-list",
				requestCookies(
						cookieWithName("access").description("엑세스 토큰")),
				pathParameters(
						parameterWithName("name").optional().description("상품명"),
						parameterWithName("page").optional().description("페이지 번호 (기본값: 0)"),
						parameterWithName("size").optional().description("페이지 크기 (기본값: 20)")),
				responseFields(
						fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
						fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
						fieldWithPath("data").description("응답 데이터").type(JsonFieldType.OBJECT),
						fieldWithPath("data.content").description("페이지 상품 목록").type(JsonFieldType.ARRAY),
						fieldWithPath("data.content[].id").description("상품 ID").type(JsonFieldType.NUMBER),
						fieldWithPath("data.content[].name").description("상품명").type(JsonFieldType.STRING),
						fieldWithPath("data.content[].description").description("상품 설명").type(JsonFieldType.STRING),
						fieldWithPath("data.content[].price").description("상품 가격").type(JsonFieldType.NUMBER),
						fieldWithPath("data.content[].imageUrl").description("상품 이미지 URL").type(JsonFieldType.STRING),
						fieldWithPath("data.content[].createdAt").description("상품 생성일").type(JsonFieldType.STRING),
						fieldWithPath("data.content[].updatedAt").description("상품 정보 수정일").type(JsonFieldType.STRING),
						fieldWithPath("data.content[].contractCount").description("관련 계약수").type(JsonFieldType.NUMBER),
						fieldWithPath("data.pageable").description("페이징 정보").type(JsonFieldType.OBJECT),
						fieldWithPath("data.pageable.sort").description("정렬 정보").type(JsonFieldType.OBJECT),
						fieldWithPath("data.pageable.sort.empty").description("정렬 정보 비어 있음 여부")
								.type(JsonFieldType.BOOLEAN),
						fieldWithPath("data.pageable.sort.sorted").description("정렬 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("data.pageable.sort.unsorted").description("정렬되지 않음 여부")
								.type(JsonFieldType.BOOLEAN),
						fieldWithPath("data.pageable.offset").description("페이징 오프셋").type(JsonFieldType.NUMBER),
						fieldWithPath("data.pageable.pageNumber").description("페이지 번호").type(JsonFieldType.NUMBER),
						fieldWithPath("data.pageable.pageSize").description("페이지 크기").type(JsonFieldType.NUMBER),
						fieldWithPath("data.pageable.paged").description("페이징 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("data.pageable.unpaged").description("페이징되지 않음 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("data.last").description("마지막 페이지 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("data.totalPages").description("전체 페이지 수").type(JsonFieldType.NUMBER),
						fieldWithPath("data.totalElements").description("전체 요소 수").type(JsonFieldType.NUMBER),
						fieldWithPath("data.size").description("페이지 크기").type(JsonFieldType.NUMBER),
						fieldWithPath("data.number").description("현재 페이지 번호").type(JsonFieldType.NUMBER),
						fieldWithPath("data.sort").description("정렬 정보").type(JsonFieldType.OBJECT),
						fieldWithPath("data.sort.empty").description("정렬 정보 비어 있음 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("data.sort.sorted").description("정렬 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("data.sort.unsorted").description("정렬되지 않음 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("data.first").description("첫 페이지 여부").type(JsonFieldType.BOOLEAN),
						fieldWithPath("data.numberOfElements").description("요소 개수").type(JsonFieldType.NUMBER),
						fieldWithPath("data.empty").description("비어 있음 여부").type(JsonFieldType.BOOLEAN))));
	}
}
