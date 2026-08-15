package com.example.dell.controller;

import com.example.dell.dto.request.CreateProductRequest;
import com.example.dell.repository.ProductRepository;
import com.example.dell.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ProductManagementController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    @GetMapping("/products")
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products";
    }

    @GetMapping("/products/new")
    public String newForm(Model model) {
        model.addAttribute("createProductRequest", new CreateProductRequest());
        return "product-new";
    }

    @PostMapping("/products/new")
    public String create(@Valid @ModelAttribute CreateProductRequest createProductRequest, BindingResult result) {
        if (result.hasErrors()) {
            return "product-new";
        }

        productService.register(createProductRequest);
        return "redirect:/products";
    }
}