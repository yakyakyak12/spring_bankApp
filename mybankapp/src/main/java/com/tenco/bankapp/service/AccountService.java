package com.tenco.bankapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tenco.bankapp.dto.SaveFormDto;
import com.tenco.bankapp.handler.exception.CustomRestfullException;
import com.tenco.bankapp.repository.entity.Account;
import com.tenco.bankapp.repository.interfaces.AccountRepository;

@Service // IoC 대상 + 싱글톤 관리
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	/**
	 * 계좌 생성
	 * @param dto
	 * @param pricipaId
	 */
	public void createAccount(SaveFormDto dto, Integer pricipaId) {
		
		// 계좌 중복확인
		
		Account account = Account.builder()
		.number(dto.getNumber())
		.password(dto.getPassword())
		.balance(dto.getBalance())
		.userId(pricipaId)
		.build();
		
		int resultRowCount = accountRepository.insert(account);
		if (resultRowCount != 1) {
			throw new CustomRestfullException("계좌 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
}
