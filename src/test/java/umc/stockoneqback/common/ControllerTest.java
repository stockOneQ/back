package umc.stockoneqback.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import umc.stockoneqback.auth.controller.AuthApiController;
import umc.stockoneqback.auth.controller.TokenReissueApiController;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.auth.service.TokenReissueService;
import umc.stockoneqback.auth.utils.JwtTokenProvider;
import umc.stockoneqback.board.controller.BoardApiController;
import umc.stockoneqback.board.controller.like.BoardLikeApiController;
import umc.stockoneqback.board.service.BoardFindService;
import umc.stockoneqback.board.service.BoardService;
import umc.stockoneqback.board.service.like.BoardLikeService;
import umc.stockoneqback.business.controller.BusinessApiController;
import umc.stockoneqback.business.controller.BusinessProductApiController;
import umc.stockoneqback.business.service.BusinessProductService;
import umc.stockoneqback.business.service.BusinessService;
import umc.stockoneqback.comment.controller.CommentApiController;
import umc.stockoneqback.comment.service.CommentFindService;
import umc.stockoneqback.comment.service.CommentService;
import umc.stockoneqback.friend.controller.FriendApiController;
import umc.stockoneqback.friend.controller.FriendInformationController;
import umc.stockoneqback.friend.controller.FriendProductApiController;
import umc.stockoneqback.friend.service.FriendFindService;
import umc.stockoneqback.friend.service.FriendInformationService;
import umc.stockoneqback.friend.service.FriendProductService;
import umc.stockoneqback.friend.service.FriendService;
import umc.stockoneqback.global.security.handler.JwtAccessDeniedHandler;
import umc.stockoneqback.global.security.handler.JwtAuthenticationEntryPoint;
import umc.stockoneqback.global.security.service.CustomUserDetailsService;
import umc.stockoneqback.product.controller.ProductApiController;
import umc.stockoneqback.product.service.ProductOthersService;
import umc.stockoneqback.product.service.ProductService;
import umc.stockoneqback.reply.controller.ReplyApiController;
import umc.stockoneqback.reply.service.ReplyFindService;
import umc.stockoneqback.reply.service.ReplyService;
import umc.stockoneqback.role.service.CompanyService;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.controller.UserApiController;
import umc.stockoneqback.user.controller.UserInformationApiController;
import umc.stockoneqback.user.controller.UserUpdateApiController;
import umc.stockoneqback.user.controller.UserFindApiController;
import umc.stockoneqback.user.service.UserFindService;
import umc.stockoneqback.user.service.UserInformationService;
import umc.stockoneqback.user.service.UserService;
import umc.stockoneqback.user.service.UserUpdateService;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(value = {
        UserApiController.class,
        UserUpdateApiController.class,
        UserInformationApiController.class,
        BusinessApiController.class,
        BoardApiController.class,
        BusinessApiController.class,
        AuthApiController.class,
        CommentApiController.class,
        ReplyApiController.class,
        ProductApiController.class,
        FriendApiController.class,
        BoardLikeApiController.class,
        FriendInformationController.class,
        TokenReissueApiController.class,
        UserFindApiController.class,
        FriendProductApiController.class,
        BusinessProductApiController.class
})
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserService userService;

    @MockBean
    protected UserUpdateService userUpdateService;

    @MockBean
    protected UserInformationService userInformationService;

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
    
    @MockBean
    protected UserFindService userFindService;

    @MockBean
    protected BoardService boardService;

    @MockBean
    protected BoardFindService boardFindService;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected CustomUserDetailsService customUserDetailsService;

    @MockBean
    protected JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    protected JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @MockBean
    protected CommentFindService commentFindService;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected ReplyFindService replyFindService;

    @MockBean
    protected ReplyService replyService;
     
    @MockBean
    protected FriendService friendService;

    @MockBean
    protected FriendFindService friendFindService;
  
    @MockBean
    protected BoardLikeService boardLikeService;

    @MockBean
    protected FriendInformationService friendInformationService;

    @MockBean
    protected TokenReissueService tokenReissueService;

    @MockBean
    protected FriendProductService friendProductService;

    @MockBean
    protected BusinessProductService businessProductService;

    @MockBean
    protected ProductOthersService productOthersService;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(log())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}
