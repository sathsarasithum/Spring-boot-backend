package com.example.dream_shop.service.product;
import com.example.dream_shop.dto.ImageDTO;
import com.example.dream_shop.dto.ProductDTO;
import com.example.dream_shop.exception.AlreadyExistException;
import com.example.dream_shop.exception.ResourceNotFounException;
import com.example.dream_shop.model.Category;
import com.example.dream_shop.model.Image;
import com.example.dream_shop.repository.CategoryRepository;
import com.example.dream_shop.repository.ImageRepository;
import com.example.dream_shop.repository.ProductRepository;
import com.example.dream_shop.request.AddProductRequest;
import com.example.dream_shop.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.example.dream_shop.model.Product;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class ProductService implements IProductService  {


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;


    @Override
    public Product addProduct(AddProductRequest request) {
        //check if the category is found in the DB
        //if Yes, set it as the new product category
        //if No, save it as the new category
        //The set as the new product category

        if (productExists(request.getName(), request.getBrand())){
            throw new AlreadyExistException(request.getBrand()+" "+request.getName()+ "already exists");
        }

        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() ->{
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });

        request.setCategory(category);
        return productRepository.save(createProduct(request,category));

        //The categoryRepository.findByName() method is called to check
        // if a Category with the specified name already exists in the repository (likely a database).

        //The Optional.ofNullable() wraps the result of findByName() in an Optional object,
        // which may or may not contain a value (i.e., the category could be null if it doesn't exist).

        //.orElseGet(() -> {...}):
        //If the Optional from ofNullable() is empty (i.e., the category does not exist), orElseGet() is invoked.
        // It takes a supplier (a lambda function in this case) to execute when the Optional is empty.
        // This will create and save a new category.

        //return categoryRepository.save(newCategory);
        //The newly created Category is saved in the repository
        // (likely persisted to a database) and returned by the orElseGet() function.
    }

    private boolean productExists(String name, String brand){
        return productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProduct(AddProductRequest request, Category category){
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category

        );

    }


    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFounException("Product not Found!"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete,
                        () ->{throw new ResourceNotFounException("Product not Found");});
    }

    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct,request))
                .map(productRepository :: save)
                .orElseThrow(()-> new ResourceNotFounException("Product not found"));

    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request){
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return  existingProduct;

    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductByCategoryAndBrand(String category, String branch) {
        return productRepository.findByCategoryNameAndBrand(category, branch);
    }

    @Override
    public List<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductByBrandAndName(String branch, String name) {
        return productRepository.findByBrandAndName(branch, name);
    }

    @Override
    public Long countProductByBrandAndName(String branch, String name) {
        return productRepository.countProductByBrandAndName(branch, name);
    }

    //The code provided is a Java method that converts a list of Product objects
    //into a list of ProductDTO objects using the Stream API

    //List<ProductDTO>: The return type is a list of ProductDTO objects.
    //List<Product> products: The input parameter is a list of Product
    //objects that the method will convert into ProductDTO objects.

    @Override
    public List<ProductDTO> getConvertedProducts(List<Product> products){
        return products.stream().map(this::convertToDto).toList();

        // products.stream():  Converts the input List<Product> into a stream,
        //                      which allows you to perform operations (like mapping) on the
        //                      collection in a functional style.

        //map(this::convertToDto): The map() method transforms each Product object
                                // in the stream into a ProductDTO object.

                                //The method reference this::convertToDto refers to a method
                                // in the current class (this) that converts a Product to a ProductDTO.
                                // So, for each Product in the stream, convertToDto() is called.
    }


    //this method used convert products to productDTO
    @Override
    public ProductDTO convertToDto(Product product){
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDTO> imageDtos = images.stream().
                map(image -> modelMapper.map(image, ImageDTO.class)).
                toList();
        productDTO.setImages(imageDtos);
        return productDTO;

        //*****productDTO = modelMapper.map(product, ProductDTO.class)******
        //This line maps a product object to a ProductDTO object using modelMapper.
        //The ModelMapper is a library used to automatically map properties from one object to another,
        //typically for data transfer objects (DTOs).

        //product is the source object, and ProductDTO.class is the target class. After mapping,
        //the productDTO will have the same values as the product,
        //but in a different structure suitable for transfer or presentation.

        //*****List<Image> images = imageRepository.findByProductId(product.getId())*****
        //This retrieves a list of Image objects from the imageRepository based on the productId.

        //product.getId() gets the ID of the product, and imageRepository.findByProductId() uses
        //this ID to fetch all associated images from the database.

        //*****List<ImageDTO> imageDtos = images.stream().map(image -> modelMapper.map(image, ImageDTO.class)).toList();****
        //This line maps each Image object in the images list to an ImageDTO object using modelMapper.
        //images.stream() starts a stream of the images list.
        //.map(image -> modelMapper.map(image, ImageDTO.class)) applies the modelMapper mapping to each Image object,
        // converting it to an ImageDTO.
        //.toList() collects the mapped objects into a List<ImageDTO>.

    }
}
