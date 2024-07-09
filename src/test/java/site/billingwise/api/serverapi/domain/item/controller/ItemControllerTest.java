package site.billingwise.api.serverapi.domain.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.auth.dto.RegisterDto;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.dto.request.EditItemDto;
import site.billingwise.api.serverapi.domain.item.service.ItemService;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.global.service.S3Service;

@WebMvcTest(ItemController.class)
public class ItemControllerTest extends AbstractRestDocsTests {

	static final Long ITEM_ID = 3L;

	@Autowired
	private ObjectMapper objectMapper;

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
				.contentType(MediaType.MULTIPART_FORM_DATA));

		// then
		result.andExpect(status().isOk())
				.andDo(document("item/create",
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
		Long itemId = 3L;
		String url = "/api/v1/items/{itemId}";

		EditItemDto editItemDto = EditItemDto.builder()
				.name("UPDATED")
				.price(100000L)
				.description("UPDATED")
				.build();

		willDoNothing().given(itemService).editItem(itemId, editItemDto);

		// when
		ResultActions result = mockMvc.perform(put(url, itemId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editItemDto)));

        //then
        result.andExpect(status().isOk()).andDo(document("item/edit-info",
            requestFields(
                    fieldWithPath("name").description("상품명 (* required)").type(JsonFieldType.STRING),
                    fieldWithPath("price").description("상품 가격 (* required)").type(JsonFieldType.NUMBER),
                    fieldWithPath("description").description("상세 설명").type(JsonFieldType.STRING)
            )));
	}

	@Test
	@DisplayName("상품 이미지 수정")
	void editItemImage() throws Exception {
		// given
		Long itemId = 3L;
		String url = "/api/v1/items/{itemId}/image";

		MockMultipartFile itemImage = new MockMultipartFile(
				"image", "item.png", "image/png", "item data".getBytes());

		willDoNothing().given(itemService).editItemImage(anyLong(), eq(itemImage));

		// when
		ResultActions result = mockMvc.perform(
				multipart(url, itemId)
						.file(itemImage)
						.with(request -> {
							request.setMethod("PUT");
							return request;
						}));

		// then
		result.andExpect(status().isOk())
				.andDo(document("item/edit-image",
						pathParameters(
								parameterWithName("itemId").description("상품 ID")),
						requestParts(
								partWithName("image").description("상품 이미지"))));

		Mockito.verify(itemService, Mockito.times(1)).editItemImage(ArgumentMatchers.eq(itemId),
				ArgumentMatchers.any());
	}
}
