package site.billingwise.api.serverapi.domain.setting.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.auth.controller.AuthController;
import site.billingwise.api.serverapi.domain.auth.dto.request.RegisterDto;
import site.billingwise.api.serverapi.domain.auth.service.AuthService;
import site.billingwise.api.serverapi.domain.setting.dto.request.SetBasicItemsDto;
import site.billingwise.api.serverapi.domain.setting.service.SettingService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SettingController.class)
public class SettingControllerTest extends AbstractRestDocsTests {

    @MockBean
    SettingService settingService;

    @Test
    @DisplayName("간편동의 상품설정")
    void setBasicItems() throws Exception {
        String url = "/api/v1/setting/easy-consent/items";

        List<Long> itemIdList = new ArrayList<>();
        itemIdList.add(1L);
        itemIdList.add(2L);
        itemIdList.add(3L);

        SetBasicItemsDto dto = SetBasicItemsDto.builder()
                .itemIdList(itemIdList)
                .build();

        // given
        willDoNothing().given(settingService).setBasicItems(dto);

        // when
        ResultActions result = mockMvc.perform(put(url)
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result.andExpect(status().isOk()).andDo(document("easy-consent/items",
                requestFields(
                        fieldWithPath("itemIdList").description("아이템 아이디 목록 (* required)").type(JsonFieldType.ARRAY)
                )));
    }
}
