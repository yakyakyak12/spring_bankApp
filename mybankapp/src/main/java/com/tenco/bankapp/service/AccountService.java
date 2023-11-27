package com.tenco.bankapp.service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bankapp.dto.DepositFormDto;
import com.tenco.bankapp.dto.SaveFormDto;
import com.tenco.bankapp.dto.WithdrawFormDto;
import com.tenco.bankapp.handler.exception.CustomRestfullException;
import com.tenco.bankapp.repository.entity.Account;
import com.tenco.bankapp.repository.entity.History;
import com.tenco.bankapp.repository.interfaces.AccountRepository;
import com.tenco.bankapp.repository.interfaces.HistoryRepository;
@Service //Ioc 대상 + 싱글톤 패턴으로 관리
public class AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private HistoryRepository historyRepository;
	
	
	//계좌 중복 여부 확인
	
	/*계좌 생성 기능
	 * @param dto
	 * @param pricipalId
	 */
	
	@Transactional
	public void createdAccount(SaveFormDto dto, Integer principalId) {
		Account account = Account.builder()
							.number(dto.getNumber())
							.password(dto.getPassword())
							.balance(dto.getBalance())
							.userId(principalId)
							.build();
		
		int resultRowCount = accountRepository.insert(account);
		if(resultRowCount != 1) {
			throw new CustomRestfullException("계좌 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
							
	}
	// 계좌 목록 보기 기능
	public List<Account> readAccountList(Integer userId){
		List<Account> list = accountRepository.findByUserId(userId);
		return list;
	}
	// 출금 기능 로직 고민해보기
	// 1. 계좌 존재 여부 확인 -> select
	// 2. 본인 계좌 여부 확인 -> select
	// 3. 계좌 비밀번호 확인
	// 4. 잔액 여부 확인
	// 5. 출금 처리 --> update
	// 6. 거래 내역 등록 --> insert
	// 7. 트랜잭션 처리
	
	@Transactional
	public void updateAccountWithdraw(WithdrawFormDto dto, Integer principalId) {
		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		
		if(accountEntity == null) {
			throw new CustomRestfullException("해당 계좌가 없습니다.", HttpStatus.BAD_REQUEST);
	
		}
		if(accountEntity.getUserId() != principalId) {
			throw new CustomRestfullException("본인 소유 계좌가 아닙니다", HttpStatus.UNAUTHORIZED);
		}
		if(accountEntity.getPassword().equals(dto.getPassword()) == false) {
			throw new CustomRestfullException("출금 계좌 비밀번호가 틀렸습니다", HttpStatus.BAD_REQUEST);
		}
		if(accountEntity.getBalance() < dto.getAmount()) {
			throw new CustomRestfullException("계좌 잔액이 부족합니다", HttpStatus.BAD_REQUEST);
		}
		// 객체 모델 상태값 변경 처리
		accountEntity.withdraw(dto.getAmount());
		accountRepository.updateById(accountEntity);
		
		// 거래 내역 등록
		History history = new History();
		history.setAmount(dto.getAmount());
		
		//
		history.setWBalance(accountEntity.getBalance());
		history.setDBalance(null);
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);
		int resultRowCount = historyRepository.insert(history);
		if(resultRowCount != 1) {
			throw new CustomRestfullException("정상 처리되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// 입금하기 
		@Transactional
		public void updateAccountDeposit(DepositFormDto dto, Integer principalId) {
			Account accountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
			
			if(accountEntity == null) {
				throw new CustomRestfullException("해당 계좌가 없습니다.", HttpStatus.BAD_REQUEST);
		
			}
			if(accountEntity.getUserId() != principalId) {
				throw new CustomRestfullException("본인 소유 계좌가 아닙니다", HttpStatus.UNAUTHORIZED);
			}
			if(accountEntity.getPassword().equals(dto.getPassword()) == false) {
				throw new CustomRestfullException("입금 계좌 비밀번호가 틀렸습니다", HttpStatus.BAD_REQUEST);
			}

			// 객체 모델 상태값 변경 처리
			accountEntity.deposit(dto.getAmount());
			accountRepository.updateById(accountEntity);
			
			// 거래 내역 등록
			History history = new History();
			history.setAmount(dto.getAmount());
			
			//
			history.setWBalance(null);
			history.setDBalance(accountEntity.getBalance());
			history.setWAccountId(null);
			history.setDAccountId(accountEntity.getId());
			int resultRowCount = historyRepository.insert(history);
			if(resultRowCount != 1) {
				throw new CustomRestfullException("정상 처리되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		
	}
}