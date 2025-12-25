package com.example.UrbanFit.models.DTO.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCategoryRequest {
    @NotBlank
    @Size(max = 120)
    private String name;

    public UpdateCategoryRequest() {}

    public UpdateCategoryRequest(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
