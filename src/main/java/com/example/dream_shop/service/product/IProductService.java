package com.example.dream_shop.service.product;

import com.example.dream_shop.dto.ProductDTO;
import com.example.dream_shop.model.Product;
import com.example.dream_shop.request.AddProductRequest;
import com.example.dream_shop.request.ProductUpdateRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest request);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest product, Long productId);
    List<Product> getAllProduct();
    List<Product> getProductByCategory(String category);
    List<Product> getProductByBrand(String brand);
    List<Product> getProductByCategoryAndBrand (String category, String brand);
    List<Product> getProductByName(String name);
    List<Product> getProductByBrandAndName(String category, String name);
    Long countProductByBrandAndName(String brand, String name);


    List<ProductDTO> getConvertedProducts(List<Product> products);

    ProductDTO convertToDto(Product product);
}
