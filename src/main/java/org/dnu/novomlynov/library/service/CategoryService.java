package org.dnu.novomlynov.library.service;

import org.dnu.novomlynov.library.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto getCategoryById(Long id);

    List<CategoryDto> getAllCategories();

    List<CategoryDto> searchCategories(String name);

    CategoryDto updateCategory(Long id, CategoryDto categoryDto);

    void deleteCategory(Long id);

    boolean isCategoryUsed(Long id);
}