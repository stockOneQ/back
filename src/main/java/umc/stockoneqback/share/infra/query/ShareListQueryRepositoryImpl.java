package umc.stockoneqback.share.infra.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.share.domain.Category;
import umc.stockoneqback.share.domain.SearchType;
import umc.stockoneqback.share.infra.query.dto.CustomShareListPage;
import umc.stockoneqback.share.infra.query.dto.QShareList;
import umc.stockoneqback.share.infra.query.dto.ShareList;

import java.util.List;

import static umc.stockoneqback.business.domain.QBusiness.business;
import static umc.stockoneqback.share.domain.QShare.share;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShareListQueryRepositoryImpl implements ShareListQueryRepository{
    private final JPAQueryFactory query;
    @Override
    public CustomShareListPage<ShareList> findShareList(Long businessId, Category category, SearchType searchType, String searchWord, int page) {
        Pageable pageable = PageRequest.of(page, 6);
        List<ShareList> shareLists = query
                .selectDistinct(new QShareList(
                        share.id,
                        share.title,
                        share.createdDate,
                        share.business.supervisor.name,
                        share.file))
                .from(share)
                .innerJoin(business).on(share.business.id.eq(businessId))
                .where(share.status.eq(Status.NORMAL),
                        share.category.eq(category),
                        search(searchType, searchWord))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(share.createdDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(share.count())
                .from(share)
                .innerJoin(business).on(share.business.id.eq(businessId))
                .where(share.status.eq(Status.NORMAL),
                        share.category.eq(category),
                        search(searchType, searchWord));

        return new CustomShareListPage<>(PageableExecutionUtils.getPage(shareLists, pageable, countQuery::fetchOne));
    }

    private BooleanExpression search(SearchType searchType, String searchWord) {
        if (searchWord == null || searchWord.isEmpty()) {
            return null;
        } else {
            switch (searchType) {
                case TITLE : return share.title.contains(searchWord);
                case CONTENT : return share.content.contains(searchWord);
                default : return null;
            }
        }
    }
}