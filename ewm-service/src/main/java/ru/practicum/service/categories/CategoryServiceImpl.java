package ru.practicum.service.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.EntityExistException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.mapper.CategoryMapper;
import ru.practicum.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.model.mapper.CategoryMapper.toCategory;
import static ru.practicum.model.mapper.CategoryMapper.toCategoryDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Поступил запрос в CategoryServiceImpl.createCategory на создание новой категории" +
                " с параметром NewCategoryDto = {}", newCategoryDto);
        if (categoryRepository.findByName(newCategoryDto.getName()) != null) {
            throw new EntityExistException("Категория: {} - уже присутствует в коллекции");
        }
        return toCategoryDto(categoryRepository.save(toCategory(newCategoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("Поступил запрос в CategoryServiceImpl.updateCategory на обновление категории" +
                " с параметрами: catId = {}, CategoryDto = {}", catId, categoryDto);
        Category tempCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Категория с id = " + catId + " не найдена"));
        if ((categoryRepository.findByName(categoryDto.getName()) != null) && !tempCategory.getName().equals(categoryDto.getName())) {
            throw new EntityExistException("Категория: {} - уже присутствует в коллекции");
        }
        tempCategory.setId(catId);
        tempCategory.setName(categoryDto.getName());
        return toCategoryDto(tempCategory);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long catId) {
        log.info("Поступил запрос в CategoryServiceImpl.deleteCategoryById на удаление категории по id" +
                " с параметром catId = {}", catId);
        categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Категория с id = " + catId + " не найдена"));
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getCategoryAll(PageRequest of) {
        log.info("Поступил запрос в CategoryServiceImpl.getCategoryAll на получение списка категорий" +
                " с параметром PageRequest = {}", of);
        return toListCategoryDto(categoryRepository.findAll(of).getContent());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        log.info("Поступил запрос в CategoryServiceImpl.getCategoryById на получение категории" +
                " с параметром catId = {}", catId);
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Категория с id = " + catId + " не найдена"));
        return toCategoryDto(category);
    }

    private List<CategoryDto> toListCategoryDto(List<Category> categories) {
        return categories.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }
}
