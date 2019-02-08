package com.quycntt.controller;


import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quycntt.domain.Role;
import com.quycntt.domain.User;
import com.quycntt.repository.RoleRepository;
import com.quycntt.repository.UserRepository;
import com.quycntt.serviceimp.CategoryServiceImp;
import com.quycntt.serviceimp.ProductServiceImp;

@Controller
public class MainController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired 
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CategoryServiceImp categoryServiceImp;
	
	@Autowired
	private ProductServiceImp productServiceImp;
	
	@GetMapping("/")
	public String index(Principal principal, ModelMap modelMap, HttpSession httpSession) {
//		List<User> listUserLimit = userRepository.findUserLimit(new PageRequest(0, 2));
//		System.out.println(listUserLimit.size());
		if (principal != null) {
			User user = userRepository.findByEmail(principal.getName());
			Set<Role> roles = user.getRoles();
			modelMap.addAttribute("user", user);
			modelMap.addAttribute("roles", roles);
		}
		
		modelMap.addAttribute("categories", categoryServiceImp.findAll());
		modelMap.addAttribute("products", productServiceImp.findProductLimit(new PageRequest(0, 3)));
		return "index";
	}
	
	@GetMapping("/admin") 
	public String admin() {
		return "admin";
	}
	
	@GetMapping("/403")
	public String accessDenied() {
		return "403";
	}
	
	@GetMapping("/login") 
	public String getLogin(Principal principal, ModelMap modelMap) {
		return "login";
	}
	
	@GetMapping("/register")
	public String getRegister() {
		return "register";
	}
	
	@PostMapping("/createUser")
	public String register(@RequestParam String email, @RequestParam String password) {
		User user = new User();
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		HashSet<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByName("ROLE_MEMBER"));
		user.setRoles(roles);
		userRepository.save(user);
		return "login";
	}
	
	public void getUser(Principal principal, HttpSession httpSession) {
		if (principal.getName().equals("")) {
			System.out.println("Not Found");
		} else {
			User user = userRepository.findByEmail(principal.getName());
			httpSession.setAttribute("users", user);
		}
	}
	
}
