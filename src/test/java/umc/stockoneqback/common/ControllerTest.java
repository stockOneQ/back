package umc.stockoneqback.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import umc.stockoneqback.auth.utils.JwtTokenProvider;
import umc.stockoneqback.business.controller.BusinessApiController;
import umc.stockoneqback.business.service.BusinessService;
import umc.stockoneqback.global.config.SecurityConfig;
import umc.stockoneqback.product.controller.ProductApiController;
import umc.stockoneqback.product.service.ProductService;
import umc.stockoneqback.role.service.CompanyService;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.controller.UserApiController;
import umc.stockoneqback.user.service.UserService;

@ImportAutoConfiguration(SecurityConfig.class)
@WebMvcTest({
        UserApiController.class,
        BusinessApiController.class,
        ProductApiController.class
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
    protected BusinessService businessService;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    protected ProductService productService;
}
