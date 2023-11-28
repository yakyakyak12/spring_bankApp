package com.tenco.bankapp.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class BalanceUtil {

	//상태값을 가지는 변수를 사용하면 안된다.
	public static String balanceToString(Long balance) {
		DecimalFormat df = new DecimalFormat("###,###");
		return df.format(balance) + "원";
	}
}
