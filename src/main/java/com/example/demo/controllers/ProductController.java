package com.example.demo.controllers;
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
import org.springframework.web.bind.annotation.PutMapping;

import com.example.demo.entities.Product;
import com.example.demo.services.ProductServices;

@Controller
public class ProductController 
{
	private static final Logger log = LoggerFactory.getLogger(ProductController.class);
	private final ProductServices productServices;

	@Autowired
	public ProductController(ProductServices productServices) {
		this.productServices = productServices;
	}

	//	AddProduct
	@PostMapping("/addingProduct")
	public String addProduct(@ModelAttribute Product product, HttpSession session)
	{
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		this.productServices.addProduct(product);
		log.info("New product added: {}", product.getPname());
		return "redirect:/admin/services";
	}

	//	UpdateProduct
	@GetMapping("/updatingProduct/{productId}")
	public String updateProduct(@ModelAttribute Product product, @PathVariable("productId") int id, HttpSession session)
	{
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		this.productServices.updateproduct(product, id);
		log.info("Product updated: {}", id);
		return "redirect:/admin/services";
	}
	
	//DeleteProduct
	@GetMapping("/deleteProduct/{productId}")
	public String delete(@PathVariable("productId") int id, HttpSession session)
	{
		if (session.getAttribute("adminEmail") == null) return "redirect:/login";
		this.productServices.deleteProduct(id);
		log.info("Product deleted: {}", id);
		return "redirect:/admin/services";
	}
	
}