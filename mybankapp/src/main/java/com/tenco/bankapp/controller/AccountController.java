package com.tenco.bankapp.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysql.cj.Session;
import com.tenco.bankapp.dto.DepositFormDto;
import com.tenco.bankapp.dto.SaveFormDto;
import com.tenco.bankapp.dto.TransferFormDto;
import com.tenco.bankapp.dto.WithdrawFormDto;
import com.tenco.bankapp.handler.exception.CustomPageException;
import com.tenco.bankapp.handler.exception.CustomRestfullException;
import com.tenco.bankapp.handler.exception.UnAuthorizedException;
import com.tenco.bankapp.repository.entity.Account;
import com.tenco.bankapp.repository.entity.History;
import com.tenco.bankapp.repository.entity.User;
import com.tenco.bankapp.service.AccountService;
import com.tenco.bankapp.utils.Define;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private HttpSession session;

	@Autowired
	private AccountService accountService;

	// 임시 예외 발생 확인 http://localhost:80/account/list
	@GetMapping({ "/list", "/" })
	public String list(Model model) {
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		List<Account> accountList = accountService.readAccountList(principal.getId());
		System.out.println("accountList : " + accountList.toString());
		if (accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		} else {
			model.addAttribute("accountList", accountList);
		}
		return "account/list";
	}

	@GetMapping("/save")
	public String save() {
		return "account/save";
	}

	@PostMapping("/save")
	public String saveProc(SaveFormDto dto) {

		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		// 2. 유효성 검사
		if (dto.getNumber() == null || dto.getNumber().isEmpty()) {
			System.out.println("save에서 오류남 : " + dto.getNumber());
			throw new CustomRestfullException("계좌번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfullException("비밀번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getBalance() == null || dto.getBalance() <= 0) {
			throw new CustomRestfullException("잘못된 입력입니다", HttpStatus.BAD_REQUEST);
		}

		accountService.createdAccount(dto, principal.getId());

		return "account/list";
	}

	// 출금 페이지 요청
	@GetMapping("/withdraw")
	public String withdraw() {
		return "account/withdraw";
	}

	@PostMapping("/withdraw")
	public String withdrawProc(WithdrawFormDto dto) {
		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (dto.getAmount() == null) {
			throw new CustomRestfullException("금액을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getAmount().longValue() <= 0) {
			throw new CustomRestfullException("출금 금액이 0원 이하일 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (dto.getWAccountNumber() == null || dto.getWAccountNumber().isEmpty()) {
			System.out.println("withdraw에서 오류남 : " + dto.getWAccountNumber());

			throw new CustomRestfullException("계좌 번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfullException("비밀번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		// 서비스 호출
		accountService.updateAccountWithdraw(dto, principal.getId());
		
		return "redirect:/account/list"; 
	}
	
	// 입금 페이지 요청
	@GetMapping("/deposit")
	public String deposit() {
		return "account/deposit";
	}
	
	@PostMapping("/deposit")
	public String depositProc(DepositFormDto dto) {

		if (dto.getAmount() == null) {
			throw new UnAuthorizedException("금액을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getAmount().longValue() <= 0) {
			throw new UnAuthorizedException("입금 금액이 0원 이하일 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().isEmpty()) {
			throw new UnAuthorizedException("계좌 번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
	

		// 서비스 호출
		accountService.updateAccountDeposit(dto);
		
		return "redirect:/account/list"; 
	}
	
	// 입금 페이지 요청
	@GetMapping("/transfer")
	public String transfer() {
		return "account/transfer";
	}

	@PostMapping("/transfer")
	public String transferProc(TransferFormDto dto) {
		System.out.println("transfer 들어오나? ");
		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		if (dto.getAmount() == null) {
			throw new CustomRestfullException("금액을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getAmount().longValue() <= 0) {
			throw new CustomRestfullException("이체 금액이 0원 이하일 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (dto.getWAccountNumber() == null || dto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfullException("출금 계좌 번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfullException("입금 계좌 번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfullException("계좌 비밀번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		
		// 2. 서비스 호출
		accountService.updateAccountTransferProc(dto, principal.getId());

		
		return "redirect:/account/list";
	}
	
	// 계좌 상세보기 화면 요청 처리 - 데이터를 입력 받는 방법 정리!
	// http://localhost/account/detail/1
	// http://localhost/account/detail/1?type=deposit
	// http://localhost/account/detail/1?type=withdraw
	// 기본값 세팅 가능
	
	@GetMapping("/detail/{accountId}")
	public String detail(@PathVariable Integer accountId, 
			@RequestParam(name = "type", defaultValue = "all", required = false) String type, Model model) {
		
		// 인증검사, 유효성 검사 
		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		// 상세 보기 화면 요청시 --> 데이터를 내려주어야한다. 
		// account 데이터, 접근주체, 거래내역 정보
		Account account = accountService.findById(accountId); 
		List<History> historyList = accountService.readHistoryListByAccount(type, accountId);

		
		model.addAttribute(Define.PRINCIPAL, principal);
		model.addAttribute("account", account);
		model.addAttribute("historyList",historyList);
		
		
		
		return "account/detail";
	}
	

}


























