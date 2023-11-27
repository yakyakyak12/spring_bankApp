package com.tenco.bankapp.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tenco.bankapp.repository.entity.Account;

@Mapper
public interface AccountRepository {
	
	// 입금
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(Integer id);
	public List<Account> findAll();
	public Account findById(Integer id);
	public List<Account> findByUserId(Integer principalId);
	public Account findByNumber(String number);

}
