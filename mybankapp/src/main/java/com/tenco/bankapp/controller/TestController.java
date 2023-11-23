package com.tenco.bankapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // IoC에 대상() 
@RequestMapping("/temp") // 반복적인 매핑을 적어두면 기본적으로 인식해줌 (대문)
public class TestController {

	// GET
	// 주소설계 - http://localhost:80/temp/temp-test
	@GetMapping("/temp-test")
	public String tempTest() {
		// yml 파일 
		// prefix : /WEB-INF/view/
		// suffix : temp.jsp"
		return "temp";
	}
	
	// GET
	// 주소설계 - http://localhost:80/temp/main-page
	@GetMapping("/main-page")
	public String tempMainPage() {
		return "main";
	}
}
