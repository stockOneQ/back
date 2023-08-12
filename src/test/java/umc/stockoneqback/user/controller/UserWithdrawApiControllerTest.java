package umc.stockoneqback.user.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.common.ControllerTest;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;

@DisplayName("User [Controller Layer] -> UserWithdrawApiController 테스트")
public class UserWithdrawApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("회원 탈퇴 API [DELETE /api/user/withdraw]")
    class withdrawUser {
        private static final String BASE_URL = "/api/user/withdraw";

        @Test
        @DisplayName("회원 탈퇴에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(userWithdrawService)
                    .withdrawUser(anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "UserWithdrawApi/Withdraw/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    )
                            )
                    );
        }
    }
}
