package com.quycntt.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.quycntt.domain.Cart;
import com.quycntt.domain.Product;
import com.quycntt.domain.Role;
import com.quycntt.domain.User;
import com.quycntt.repository.UserRepository;
import com.quycntt.serviceimp.ProductServiceImp;

@Controller
@SessionAttributes("cart")
public class DetailController {
	
	@Autowired
	private ProductServiceImp productServiceImp;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/detail")
	public String detail(Principal principal, @RequestParam int id, ModelMap modelMap) {
		modelMap.addAttribute("product", productServiceImp.findById(id));
		
		if (principal != null) {
			User user = userRepository.findByEmail(principal.getName());
			Set<Role> roles = user.getRoles();
			modelMap.addAttribute("user", user);
			modelMap.addAttribute("roles", roles);
		}
		
		return "detail";
	}
	
	private List<Cart> listCart = new ArrayList<Cart>();
 	
	@GetMapping("/addCart")
	@ResponseBody
	public void addCart(@RequestParam int id, HttpSession httpSession) {
		Product product = productServiceImp.findById(id);
		Cart cart = new Cart(); 
		cart.setId(id);
		cart.setName(product.getName());
		cart.setPrice(product.getPrice());
		cart.setQuantity(1);
		
		if (httpSession.getAttribute("cart") == null) {
			listCart.add(cart);
		} else {
			int idCheck = check(id, httpSession);
			if (idCheck == -1) {
				listCart.add(cart);
			} else {
				List<Cart> listCart = (List<Cart>) httpSession.getAttribute("cart");
				int quantityNew = listCart.get(idCheck).getQuantity() + 1;
				listCart.get(idCheck).setQuantity(quantityNew);
			}
		}
		
		httpSession.setAttribute("cart", listCart);
		
	}
	
	private int check(int id, HttpSession httpSession) {
		List<Cart> listCart = (List<Cart>) httpSession.getAttribute("cart");
		for (int i = 0; i < listCart.size(); i++) {
			if (listCart.get(i).getId() == id) {
				return i;
			}
		}
		return -1;
	}
}
