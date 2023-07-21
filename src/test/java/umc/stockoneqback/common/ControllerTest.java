package umc.stockoneqback.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import umc.stockoneqback.board.controller.BoardApiController;
import umc.stockoneqback.board.service.BoardFindService;
import umc.stockoneqback.board.service.BoardService;
import umc.stockoneqback.global.config.SecurityConfig;
import umc.stockoneqback.role.service.CompanyService;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.controller.UserApiController;
import umc.stockoneqback.user.service.UserFindService;
import umc.stockoneqback.user.service.UserService;

@ImportAutoConfiguration(SecurityConfig.class)
@WebMvcTest({
        UserApiController.class,
        BoardApiController.class
})
@AutoConfigureRestDocs
@WithMockUser
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserService userService;

    @MockBean
    protected StoreService storeService;

    @MockBean
    protected CompanyService companyService;

    @MockBean
    protected UserFindService userFindService;

    @MockBean
    protected BoardService boardService;

    @MockBean
    protected BoardFindService boardFindService;
}
