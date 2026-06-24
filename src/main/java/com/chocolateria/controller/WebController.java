package com.chocolateria.controller;

import com.chocolateria.service.CategoryService;
import com.chocolateria.service.CustomerService;
import com.chocolateria.service.ProductService;
import com.chocolateria.service.SaleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final CategoryService categoryService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final SaleService saleService;

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalCategories",
                categoryService.findAll(null, PageRequest.of(0, 1)).getTotalElements());
        model.addAttribute("totalCustomers",
                customerService.findAll(null, PageRequest.of(0, 1)).getTotalElements());
        model.addAttribute("totalProducts",
                productService.findAll(null, null, PageRequest.of(0, 1)).getTotalElements());
        model.addAttribute("totalSales",
                saleService.findAll(null, null, PageRequest.of(0, 1)).getTotalElements());
        return "dashboard";
    }

    @GetMapping("/categories")
    public String categories(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        var pageable = PageRequest.of(page, size, Sort.by("name"));
        model.addAttribute("pageData", categoryService.findAll(name.isBlank() ? null : name, pageable));
        model.addAttribute("search", name);
        model.addAttribute("currentPage", page);
        return "categories/index";
    }

    @GetMapping("/customers")
    public String customers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        var pageable = PageRequest.of(page, size, Sort.by("lastName"));
        model.addAttribute("pageData", customerService.findAll(search.isBlank() ? null : search, pageable));
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        return "customers/index";
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        var pageable = PageRequest.of(page, size, Sort.by("name"));
        model.addAttribute("pageData", productService.findAll(
                name.isBlank() ? null : name, categoryId, pageable));
        model.addAttribute("categories",
                categoryService.findAll(null, PageRequest.of(0, 100, Sort.by("name"))));
        model.addAttribute("search", name);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("currentPage", page);
        return "products/index";
    }

    @GetMapping("/sales")
    public String sales(
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "saleDate"));
        model.addAttribute("pageData", saleService.findAll(null,
                status.isBlank() ? null : status, pageable));
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        return "sales/index";
    }

    @GetMapping("/sales/new")
    public String newSale(Model model) {
        model.addAttribute("customers",
                customerService.findAll(null, PageRequest.of(0, 200, Sort.by("lastName"))));
        model.addAttribute("products", productService.findAllActive());
        return "sales/checkout";
    }

    @GetMapping("/sales/{id}")
    public String saleDetail(@PathVariable Long id, Model model) {
        model.addAttribute("sale", saleService.findById(id));
        return "sales/detail";
    }
}
