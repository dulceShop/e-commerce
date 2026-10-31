package com.dulceshop.backend.controller;
import com.dulceshop.backend.dto.CategoryResponse;
import com.dulceshop.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController 
@RequestMapping("/api/categories")
@RequiredArgsConstructor 
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping 
    public List<CategoryResponse> findAll(){
        return categoryService.findAll();
    }
}
