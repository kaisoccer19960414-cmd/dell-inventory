package com.example.dell.controller;

import com.example.dell.dto.request.CreateProductRequest;
import com.example.dell.entity.ProductCategory;
import com.example.dell.repository.ProductRepository;
import com.example.dell.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ProductManagementController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    /** 商品管理画面の初期表示。販売中の商品だけを見せる。 */
    @GetMapping("/products")
    public String list(Model model) {
        model.addAttribute("products", productRepository.findByIsActiveTrue());
        model.addAttribute("showingActive", true);
        return "products";
    }

    /** 販売停止中の商品一覧。ボタンで切り替えてここに来る。 */
    @GetMapping("/products/inactive")
    public String inactiveList(Model model) {
        model.addAttribute("products", productRepository.findByIsActiveFalse());
        model.addAttribute("showingActive", false);
        return "products";
    }

    /** カテゴリ選択画面。ここから各カテゴリ専用の登録フォームへ飛ぶ */
    @GetMapping("/products/new")
    public String categorySelect() {
        return "product-category-select";
    }

    @GetMapping("/products/new/{category}")
    public String newForm(@PathVariable ProductCategory category, Model model) {
        CreateProductRequest request = new CreateProductRequest();
        request.setCategory(category);
        model.addAttribute("createProductRequest", request);
        model.addAttribute("category", category);
        return "product-new";
    }

    @PostMapping("/products/new/{category}")
    public String create(@PathVariable ProductCategory category,
                         @Valid @ModelAttribute CreateProductRequest createProductRequest,
                         BindingResult result, Model model) {
        // URLで指定されたカテゴリを正とする。フォームの隠しフィールドが
        // 万一改ざんされていても、ここで必ず上書きするため影響を受けない
        createProductRequest.setCategory(category);

        if (result.hasErrors()) {
            model.addAttribute("category", category);
            return "product-new";
        }

        productService.register(createProductRequest);
        return "redirect:/products";
    }

    @PostMapping("/products/{id}/deactivate")
    public String deactivate(@PathVariable String id) {
        productService.setActive(id, false);
        // 停止すると販売中一覧から消えるので、販売中一覧に戻す
        return "redirect:/products";
    }

    @PostMapping("/products/{id}/activate")
    public String activate(@PathVariable String id) {
        productService.setActive(id, true);
        // 再開すると停止中一覧から消えるので、停止中一覧に戻す
        return "redirect:/products/inactive";
    }
}