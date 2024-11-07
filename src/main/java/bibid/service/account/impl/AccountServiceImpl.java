package bibid.service.account.impl;

import bibid.dto.AccountDto;
import bibid.dto.AccountUseHistoryDto;
import bibid.entity.Account;
import bibid.entity.AccountUseHistory;
import bibid.entity.Auction;
import bibid.entity.Member;
import bibid.repository.account.AccountRepository;
import bibid.repository.account.AccountUseHistoryRepository;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.member.MemberRepository;
import bibid.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountUseHistoryRepository accountUseHistoryRepository;
    private final MemberRepository memberRepository;
    private final AuctionRepository auctionRepository;

    // 충전 로직
    @Override
    @Transactional
    public AccountDto chargeAccount(AccountUseHistoryDto accountUseHistoryDto, Long memberIndex) {
        log.info("충전 요청 시작 - Member Index: {}", memberIndex);

        Member member = memberRepository.findById(memberIndex)
                .orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));
        Account account = accountRepository.findByMember_MemberIndex(memberIndex)
                .orElseThrow(() -> new RuntimeException("계좌 정보가 존재하지 않습니다."));

        log.info("회원 및 계좌 정보 조회 완료 - Member ID: {}, Current Balance: {}", member.getMemberId(), account.getUserMoney());

        // 충전 전 잔액 기록
        int currentBalance = Integer.parseInt(account.getUserMoney());
        int changeAmount = Integer.parseInt(accountUseHistoryDto.getChangeAccount());
        int newBalance = currentBalance + changeAmount;

        // 충전 처리 후 잔액 설정
        account.setUserMoney(String.valueOf(newBalance));
        log.info("충전 후 잔액 - New Balance: {}", account.getUserMoney());

        // 기록 추가 및 저장
        AccountUseHistory history = accountUseHistoryDto.toEntity(member, null, account);
        history.setBeforeBalance(String.valueOf(currentBalance)); // 충전 전 잔액
        history.setAfterBalance(account.getUserMoney()); // 충전 후 잔액
        accountUseHistoryRepository.save(history); // AccountUseHistory 개별 저장
        accountRepository.save(account); // Account 개별 저장

        log.info("충전 요청 완료 - Updated Account Balance: {}", account.getUserMoney());
        return account.toDto();
    }

    // 환전 로직
    @Override
    @Transactional
    public AccountDto exchangeAccount(AccountUseHistoryDto accountUseHistoryDto, Long memberIndex) {
        log.info("환전 요청 시작 - Member Index: {}", memberIndex);

        Member member = memberRepository.findById(memberIndex)
                .orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));
        Account account = accountRepository.findByMember_MemberIndex(memberIndex)
                .orElseThrow(() -> new RuntimeException("계좌 정보가 존재하지 않습니다."));

        log.info("회원 및 계좌 정보 조회 완료 - Member ID: {}, Current Balance: {}", member.getMemberId(), account.getUserMoney());

        // 환전 전 잔액 기록
        int currentBalance = Integer.parseInt(account.getUserMoney());
        int changeAmount = Integer.parseInt(accountUseHistoryDto.getChangeAccount());
        int newBalance = currentBalance - changeAmount;

        // 환전 처리 후 잔액 설정
        account.setUserMoney(String.valueOf(newBalance));
        log.info("환전 후 잔액 - New Balance: {}", account.getUserMoney());

        // 기록 추가 및 저장
        AccountUseHistory history = accountUseHistoryDto.toEntity(member, null, account);
        history.setBeforeBalance(String.valueOf(currentBalance)); // 환전 전 잔액
        history.setAfterBalance(account.getUserMoney()); // 환전 후 잔액
        accountUseHistoryRepository.save(history); // AccountUseHistory 개별 저장
        accountRepository.save(account); // Account 개별 저장

        log.info("환전 요청 완료 - Updated Account Balance: {}", account.getUserMoney());
        return account.toDto();
    }

    // 입찰 로직
    @Override
    @Transactional
    public AccountDto buyAuction(AccountUseHistoryDto accountUseHistoryDto, Long memberIndex) {
        log.info("입찰 요청 시작 - Member Index: {}", memberIndex);

        Member member = memberRepository.findById(memberIndex)
                .orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));
        Account account = accountRepository.findByMember_MemberIndex(memberIndex)
                .orElseThrow(() -> new RuntimeException("계좌 정보가 존재하지 않습니다."));

        Auction auction = auctionRepository.findById(accountUseHistoryDto.getAuctionIndex())
                .orElseThrow(() -> new RuntimeException("해당 옥션이 존재하지 않습니다."));

        log.info("옥션 정보 조회 완료 - Auction Index: {}, Auction Status: {}", auction.getAuctionIndex(), auction.getAuctionStatus());


        // 입찰 전 잔액 기록
        int currentBalance = Integer.parseInt(account.getUserMoney());
        int changeAmount = Integer.parseInt(accountUseHistoryDto.getChangeAccount());
        int newBalance = currentBalance - changeAmount;

        // 입찰 처리 후 잔액 설정
        account.setUserMoney(String.valueOf(newBalance));
        log.info("입찰 후 잔액 - New Balance: {}", account.getUserMoney());

        // 기록 추가 및 저장
        AccountUseHistory history = accountUseHistoryDto.toEntity(member, auction, account);
        history.setBeforeBalance(String.valueOf(currentBalance)); // 입찰 전 잔액
        history.setAfterBalance(account.getUserMoney()); // 입찰 후 잔액
        accountUseHistoryRepository.save(history); // AccountUseHistory 개별 저장
        accountRepository.save(account); // Account 개별 저장

        log.info("입찰 요청 완료 - Updated Account Balance: {}", account.getUserMoney());
        return account.toDto();
    }

    // 판매 로직
    @Override
    @Transactional
    public AccountDto sellAuction(AccountUseHistoryDto accountUseHistoryDto, Long memberIndex) {
        log.info("대금 수령 요청 시작 - Member Index: {}", memberIndex);

        Member member = memberRepository.findById(memberIndex)
                .orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));
        Account account = accountRepository.findByMember_MemberIndex(memberIndex)
                .orElseThrow(() -> new RuntimeException("계좌 정보가 존재하지 않습니다."));

        Auction auction = auctionRepository.findById(accountUseHistoryDto.getAuctionIndex())
                .orElseThrow(() -> new RuntimeException("해당 옥션이 존재하지 않습니다."));

        log.info("옥션 정보 조회 완료 - Auction Index: {}, Auction Status: {}", auction.getAuctionIndex(), auction.getAuctionStatus());

        // 판매 전 잔액 기록
        int currentBalance = Integer.parseInt(account.getUserMoney());
        int changeAmount = Integer.parseInt(accountUseHistoryDto.getChangeAccount());
        int newBalance = currentBalance + changeAmount;

        // 판매 처리 후 잔액 설정
        account.setUserMoney(String.valueOf(newBalance));
        log.info("판매 후 잔액 - New Balance: {}", account.getUserMoney());

        // 기록 추가 및 저장
        AccountUseHistory history = accountUseHistoryDto.toEntity(member, auction, account);
        history.setBeforeBalance(String.valueOf(currentBalance)); // 판매 전 잔액
        history.setAfterBalance(account.getUserMoney()); // 판매 후 잔액
        accountUseHistoryRepository.save(history); // AccountUseHistory 개별 저장
        accountRepository.save(account); // Account 개별 저장

        log.info("판매 요청 완료 - Updated Account Balance: {}", account.getUserMoney());
        return account.toDto();
    }

    @Override
    public Account findMemberIndex(Long memberIndex) {
        Account account = accountRepository.findByMember_MemberIndex(memberIndex)
                .orElseThrow(() -> new RuntimeException("계좌 정보가 존재하지 않습니다."));
        return account;
    }
}
