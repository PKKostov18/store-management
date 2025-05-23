package com.zafe.store_management.controller;

import com.zafe.store_management.model.Product;
import com.zafe.store_management.model.ProductCategory;
import com.zafe.store_management.model.Store;
import com.zafe.store_management.repository.StoreRepository;
import com.zafe.store_management.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/store/{storeId}/products")
public class ProductController {

    private final StoreRepository storeRepository;
    private final ProductService productService;

    public ProductController(StoreRepository storeRepository, ProductService productService) {
        this.storeRepository = storeRepository;
        this.productService = productService;
    }

    @GetMapping("/add")
    public String showAddProductForm(@PathVariable Long storeId, Model model) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        Product product = new Product();
        product.setStore(store);

        model.addAttribute("product", product);
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("store", store);
        return "product-add";
    }

    @PostMapping("/add")
    public String handleAddProductForm(@ModelAttribute("product") @Valid Product product,
                                       BindingResult bindingResult,
                                       @PathVariable Long storeId,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", ProductCategory.values());
            return "product-add";
        }

        productService.saveProductForStore(product, storeId);
        return "redirect:/store/{storeId}";
    }

    @GetMapping("/list")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product-list";
    }

    @ResponseBody
    @PostMapping("/checkQuantity")
    public Map<String, Integer> checkQuantity(@PathVariable Long storeId,
                                              @RequestParam String productName,
                                              @RequestParam int quantity) {

        productService.checkProductQuantity(productName, storeId, quantity);

        int availableQuantity = productService.getAvailableQuantity(productName, storeId);

        Map<String, Integer> response = new HashMap<>();
        response.put("availableQuantity", availableQuantity);
        return response;
    }

    @PostMapping("/{productId}/delete")
    public String markProductAsDeleted(@PathVariable Long storeId, @PathVariable Long productId) {
        Product product = productService.findById(productId);
        product.setDeleted(true);
        productService.save(product);
        return "redirect:/store/" + storeId + "/details";
    }

}

