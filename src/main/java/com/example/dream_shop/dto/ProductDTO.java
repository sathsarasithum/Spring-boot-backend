package com.example.dream_shop.dto;

import com.example.dream_shop.model.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category; //relationship with product
    private List<ImageDTO> images; //relationship
}
