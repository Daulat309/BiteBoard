package com.example.demo.controllers;

import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.count.*;
import com.example.demo.entities.*;
import com.example.demo.loginCredentials.*;
import com.example.demo.services.*;

@Controller
public class AdminController {

	private static final Logger log = LoggerFactory.getLogger(AdminController.class);

	private final UserServices services;
	private final AdminServices adminServices;
	private final ProductServices productServices;	
	private final OrderServices orderServices;

	@Autowired
	public AdminController(UserServices services, AdminServices adminServices, 
						   ProductServices productServices, OrderServices orderServices) {
		this.services = services;
		this.adminServices = adminServices;
		this.productServices = productServices;
		this.orderServices = orderServices;
	}

	@PostMapping("/adminLogin")
	public String adminLogin(@ModelAttribute("adminLogin") AdminLogin login, Model model, HttpSession session) {
		String email = login.getEmail();
		String password = login.getPassword();
		
		log.info("Admin login attempt for: {}", email);
		if (adminServices.validateAdminCredentials(email, password)) {
			session.setAttribute("adminEmail", email);
			return "redirect:/admin/services";
		} else {
			log.warn("Failed admin login attempt for: {}", email);
			model.addAttribute("error", "Invalid email or password");
			return "Login";
		}
	}

	@PostMapping("/userLogin")
	public String userLogin(@ModelAttribute("userLogin") UserLogin login, Model model, HttpSession session) {
		String email = login.getUserEmail();
		String password = login.getUserPassword();
		
		log.info("User login attempt for: {}", email);
		if (services.validateLoginCredentials(email, password)) {
			User user = this.services.getUserByEmail(email);
			session.setAttribute("user", user);
			session.setAttribute("userEmail", email);
			
			List<Orders> orders = this.orderServices.getOrdersForUser(user);
			model.addAttribute("orders", orders);
			model.addAttribute("name", user.getUname());
			return "BuyProduct";
		} else {
			log.warn("Failed user login attempt for: {}", email);
			model.addAttribute("error2", "Invalid email or password");
			return "Login";
		}
	}

	@PostMapping("/product/search")
	public String seachHandler(@RequestParam("productName") String name, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login";
		}

		Product product = this.productServices.getProductByName(name);
		List<Orders> orders = this.orderServices.getOrdersForUser(user);
		model.addAttribute("orders", orders);
		model.addAttribute("name", user.getUname());

		if (product == null) {
			model.addAttribute("message", "SORRY...!  Product Unavailable");
			model.addAttribute("product", product);
			return "BuyProduct";
		}
		
		model.addAttribute("product", product);
		return "BuyProduct";
	} 

	@GetMapping("/admin/services")
	public String returnBack(Model model, HttpSession session) {
		if (session.getAttribute("adminEmail") == null) {
			return "redirect:/login";
		}

		List<User> users = this.services.getAllUser();
		List<Admin> admins = this.adminServices.getAll(); 
		List<Product> products = this.productServices.getAllProducts();
		List<Orders> orders = this.orderServices.getOrders();
		
		model.addAttribute("users", users);
		model.addAttribute("admins", admins);
		model.addAttribute("products", products);
		model.addAttribute("orders", orders);

		return "Admin_Page";
	}

	@GetMapping("/addAdmin")
	public String addAdminPage(HttpSession session) {
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		return "Add_Admin";
	}

	@PostMapping("addingAdmin")
	public String addAdmin(@ModelAttribute Admin admin, HttpSession session) {
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		this.adminServices.addAdmin(admin);
		log.info("New admin added: {}", admin.getAdminEmail());
		return "redirect:/admin/services";
	}

	@GetMapping("/updateAdmin/{adminId}")
	public String update(@PathVariable("adminId") int id, Model model, HttpSession session) {
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		Admin admin = this.adminServices.getAdmin(id);
		model.addAttribute("admin", admin);
		return "Update_Admin";
	}

	@GetMapping("/updatingAdmin/{id}")
	public String updateAdmin(@ModelAttribute Admin admin, @PathVariable("id") int id, HttpSession session) {
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		this.adminServices.update(admin, id);
		log.info("Admin updated: {}", id);
		return "redirect:/admin/services";
	}

	@GetMapping("/deleteAdmin/{id}")
	public String deleteAdmin(@PathVariable("id") int id, HttpSession session) {
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		this.adminServices.delete(id);
		log.info("Admin deleted: {}", id);
		return "redirect:/admin/services";
	}

	@GetMapping("/addProduct")
	public String addProduct(HttpSession session) {
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		return "Add_Product";
	}
	
	@GetMapping("/updateProduct/{productId}")
	public String updateProduct(@PathVariable("productId") int id, Model model, HttpSession session) {
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		Product product = this.productServices.getProduct(id);
		model.addAttribute("product", product);
		return "Update_Product";
	}

	@GetMapping("/addUser")
	public String addUser(HttpSession session) {
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		return "Add_User";
	}

	@GetMapping("/updateUser/{userId}")
	public String updateUserPage(@PathVariable("userId") int id, Model model, HttpSession session) {
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		User user = this.services.getUser(id);
		model.addAttribute("user", user);
		return "Update_User";
	}

	@PostMapping("/product/order")
	public String orderHandler(@ModelAttribute() Orders order, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login";
		}

		double totalAmount = Logic.countTotal(order.getoPrice(), order.getoQuantity());
		order.setTotalAmmout(totalAmount);
		order.setUser(user);
		order.setOrderDate(new Date());
		
		this.orderServices.saveOrder(order);
		log.info("Order placed by user: {}, total: {}", user.getUemail(), totalAmount);
		
		model.addAttribute("amount", totalAmount);
		return "Order_success";
	}

	@GetMapping("/product/back")
	public String back(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login";
		}

		List<Orders> orders = this.orderServices.getOrdersForUser(user);
		model.addAttribute("orders", orders);
		model.addAttribute("name", user.getUname());
		return "BuyProduct";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}