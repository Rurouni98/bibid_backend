package bibid.repository.auction;

import bibid.entity.Auction;
import bibid.entity.QAuction;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AuctionRepositoryCustomImpl implements AuctionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AuctionRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Auction> findByCategory(String category, Pageable pageable) {
        QAuction auction = QAuction.auction;

        List<Auction> results = queryFactory
                .selectFrom(auction)
                .where(auction.category.eq(category)
                        .and(auction.auctionType.eq("일반 경매")))
                .orderBy(auction.regdate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(auction)
                .where(auction.category.eq(category)
                        .and(auction.auctionType.eq("일반 경매")))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Auction> findByCategory2(String category, Pageable pageable) {
        QAuction auction = QAuction.auction;

        List<Auction> results = queryFactory
                .selectFrom(auction)
                .where(auction.category.eq(category)
                        .and(auction.auctionType.eq("일반 경매")))
                .orderBy(auction.regdate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(auction)
                .where(auction.category.eq(category)
                        .and(auction.auctionType.eq("일반 경매")))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Auction> findBest(Pageable pageable) {
        QAuction auction = QAuction.auction;

        List<Auction> results = queryFactory
                .selectFrom(auction)
                .where(auction.auctionType.eq("일반 경매"))
                .orderBy(auction.viewCnt.desc(), auction.regdate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(auction)
                .where(auction.auctionType.eq("일반 경매"))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Auction> findAllGeneralAuction(Pageable pageable) {
        QAuction auction = QAuction.auction;

        List<Auction> results = queryFactory
                .selectFrom(auction)
                .where(auction.auctionType.eq("일반 경매"))
                .orderBy(auction.regdate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(auction)
                .where(auction.auctionType.eq("일반 경매"))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Auction> findConveyor(LocalDateTime currentTime, Pageable pageable) {
        QAuction auction = QAuction.auction;

        List<Auction> results = queryFactory
                .selectFrom(auction)
                .where(auction.endingLocalDateTime.gt(currentTime)
                        .and(auction.auctionType.eq("일반 경매")))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(auction)
                .where(auction.endingLocalDateTime.gt(currentTime)
                        .and(auction.auctionType.eq("일반 경매")))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Auction> searchAll(String searchCondition, String searchKeyword, Pageable pageable) {
        QAuction auction = QAuction.auction;



        BooleanExpression condition = createSearchCondition(searchCondition, searchKeyword, auction);

        List<Auction> results = queryFactory
                .selectFrom(auction)
                .where(condition
                        .and(auction.auctionType.eq("일반 경매")))
                .orderBy(auction.regdate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(auction)
                .where(condition
                        .and(auction.auctionType.eq("일반 경매")))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression createSearchCondition(String searchCondition, String searchKeyword, QAuction auction) {
        if ("all".equals(searchCondition)) {
            return auction.productName.containsIgnoreCase(searchKeyword)
                    .or(auction.category.containsIgnoreCase(searchKeyword))
                    .or(auction.productDescription.containsIgnoreCase(searchKeyword));
        } else if ("productName".equals(searchCondition)) {
            return auction.productName.containsIgnoreCase(searchKeyword);
        } else if ("category".equals(searchCondition)) {
            return auction.category.containsIgnoreCase(searchKeyword);
        } else if ("productDescription".equals(searchCondition)) {
            return auction.productDescription.containsIgnoreCase(searchKeyword);
        } else {
            return null;
        }
    }
}