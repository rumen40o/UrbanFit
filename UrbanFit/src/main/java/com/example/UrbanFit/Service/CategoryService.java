package com.example.UrbanFit.Service;

import com.example.UrbanFit.Service.interfaces.CategoryServiceInterface;

import com.example.UrbanFit.models.Category;
import com.example.UrbanFit.models.DTO.CategoryDto;
import com.example.UrbanFit.models.DTO.CreateCategoryRequest;
import com.example.UrbanFit.models.DTO.UpdateCategoryRequest;
import com.example.UrbanFit.repository.CategoryRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class CategoryService implements CategoryServiceInterface {

    private final CategoryRepo repo;

    public CategoryService(CategoryRepo repo) {
        this.repo = repo;
    }

    @Override
    @Transactional()
    public List<CategoryDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional()
    public CategoryDto findById(Long id) {
        Category c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return toDto(c);
    }

    @Override
    public CategoryDto create(CreateCategoryRequest req) {
        if (repo.existsByNameIgnoreCase(req.getName())) {
            throw new IllegalArgumentException("Category already exists");
        }
        Category c = new Category(req.getName().trim());
        c = repo.save(c);
        return toDto(c);
    }

    @Override
    public CategoryDto update(Long id, UpdateCategoryRequest req) {
        Category c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        String newName = req.getName().trim();
        if (!c.getName().equalsIgnoreCase(newName) && repo.existsByNameIgnoreCase(newName)) {
            throw new IllegalArgumentException("Category already exists");
        }
        c.setName(newName);
        return toDto(c);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Category not found");
        repo.deleteById(id);
    }

    private CategoryDto toDto(Category c) {
        return new CategoryDto(c.getId(), c.getName(), c.getCreatedAt());
    }
}
