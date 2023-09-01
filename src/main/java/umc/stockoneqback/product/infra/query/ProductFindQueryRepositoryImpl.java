package umc.stockoneqback.product.infra.query;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.product.domain.ProductSortCondition;
import umc.stockoneqback.product.domain.SearchCondition;
import umc.stockoneqback.product.domain.StoreCondition;
import umc.stockoneqback.product.infra.query.dto.ProductFindPage;
import umc.stockoneqback.product.infra.query.dto.QProductFindPage;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.booleanTemplate;
import static umc.stockoneqback.product.domain.QProduct.product;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductFindQueryRepositoryImpl implements ProductFindQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<ProductFindPage> findProductByName(Store store, StoreCondition storeCondition, String productName) {
        return query.selectDistinct(new QProductFindPage(product.id, product.name, product.imageUrl, product.stockQuant))
                .from(product)
                .where(product.store.eq(store), product.storeCondition.eq(storeCondition), product.status.eq(Status.NORMAL),
                        product.name.contains(productName))
                .orderBy(product.name.asc())
                .fetch();
    }

    @Override
    public List<ProductFindPage> findPageOfSearchConditionOrderBySortCondition
            (Store store, StoreCondition storeCondition, SearchCondition searchCondition,
             ProductSortCondition productSortCondition, String productName, Long orderFreq, Integer pageSize) {
        return query.selectDistinct(new QProductFindPage(product.id, product.name, product.imageUrl, product.stockQuant))
                .from(product)
                .where(product.store.eq(store), product.storeCondition.eq(storeCondition), product.status.eq(Status.NORMAL),
                        getWhereQueryBySearchCondition(searchCondition),
                        getWhereQueryBySortCondition(productSortCondition, productName, orderFreq))
                .orderBy(getOrderByQueryBySortCondition(productSortCondition))
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression getWhereQueryBySearchCondition(SearchCondition searchCondition) {
        if (searchCondition == null) {
            throw BaseException.type(UserErrorCode.INPUT_VALUE_REQUIRED);
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate standardDate = currentDate.plusDays(3);

        return switch (searchCondition) {
            case ALL -> null;
            case PASS -> product.expirationDate.before(currentDate);
            case CLOSE -> product.expirationDate.between(currentDate, standardDate);
            case LACK -> product.stockQuant.loe(product.requireQuant);
        };
    }

    private BooleanBuilder getWhereQueryBySortCondition(ProductSortCondition productSortCondition, String productName, Long orderFreq) {
        if (productSortCondition == null) {
            throw BaseException.type(UserErrorCode.INPUT_VALUE_REQUIRED);
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        switch (productSortCondition) {
            case NAME -> booleanBuilder.and(
                    booleanTemplate("({0} > {1} or {1} is null)", product.name, productName));
            case ORDER_FREQUENCY -> booleanBuilder.and(
                    booleanTemplate(
                            "(({0} < {1} or {0} = {1} and {2} > {3}) or ({1} is null and {3} is null))",
                            product.orderFreq, orderFreq, product.name, productName
                    )
            );
        }
        return booleanBuilder;
    }

    private OrderSpecifier[] getOrderByQueryBySortCondition(ProductSortCondition productSortCondition) {
        if (productSortCondition == null) {
            throw BaseException.type(UserErrorCode.INPUT_VALUE_REQUIRED);
        }

        List<OrderSpecifier> orderSpecifierList = new ArrayList<>();

        switch (productSortCondition) {
            case NAME -> orderSpecifierList.add(new OrderSpecifier(Order.ASC, product.name));
            case ORDER_FREQUENCY -> {
                orderSpecifierList.add(new OrderSpecifier(Order.DESC, product.orderFreq));
                orderSpecifierList.add(new OrderSpecifier(Order.ASC, product.name));
            }
            default -> orderSpecifierList.add(new OrderSpecifier(Order.ASC, product.name));
        }
        return orderSpecifierList.toArray(new OrderSpecifier[orderSpecifierList.size()]);
    }
}
