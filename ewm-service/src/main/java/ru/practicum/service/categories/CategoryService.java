package ru.practicum.service.categories;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    void deleteCategoryById(Long catId);

    List<CategoryDto> getCategoryAll(PageRequest of);

    CategoryDto getCategoryById(Long catId);
}
