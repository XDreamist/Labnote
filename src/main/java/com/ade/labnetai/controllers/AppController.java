package com.ade.labnetai.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
	
	@GetMapping("/")
	public String chatWindow(){
		return "home";
	}
	
	@GetMapping("/search")
	public String chatbot(){
		return "search";
	}
    
    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("message", "Error Message!");
        return "error";
    }
}