package com.example.dream_shop.service.image;

import com.example.dream_shop.dto.ImageDTO;
import com.example.dream_shop.exception.ResourceNotFounException;
import com.example.dream_shop.model.Image;
import com.example.dream_shop.model.Product;
import com.example.dream_shop.repository.ImageRepository;
import com.example.dream_shop.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

    private ImageRepository imageRepository;
    private IProductService productService;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() ->new ResourceNotFounException("No Image found with:" + id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository :: delete, () ->{
            throw new ResourceNotFounException("No image found with"+id);
        });
    }

    @Override
    public List<ImageDTO> saveImages(List<MultipartFile> files, Long productId) {

        // 1. Retrieve the product by its ID
        Product product = productService.getProductById(productId);
        // 2. Initialize an empty list to store the DTOs of the saved images
        List<ImageDTO> savedImageDtos = new ArrayList<>();
        // 3. Iterate over each file in the provided list of multipart files
        for(MultipartFile file : files){
            try {
                // 4. Create a new Image object
                Image image = new Image();
                // 5. Set the image's file name, file type, and binary content (as a SerialBlob)
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                // 6. Set the image's associated product
                image.setProduct(product);

                // 7. Construct a download URL for the image (pre-save)
                String buildDownloadUrl = "/api/v1/images/image/downlaod/";
                String downloadUrl = buildDownloadUrl +image.getId();
                image.setDownloadUrl(downloadUrl);
                // 8. Save the image entity to the database
                Image savedImage= imageRepository.save(image);

                // 9. Update the download URL with the saved image's ID (now that it has been persisted and has an ID)
                savedImage.setDownloadUrl("/api/v1/images/image/downlaod/" +savedImage.getId());
                imageRepository.save(savedImage);// Save again to update the download URL

                // 10. Create a new ImageDTO to store details of the saved image
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setImageId(savedImage.getId());
                imageDTO.setImageName(savedImage.getFileName());
                imageDTO.setDownloadUrl(savedImage.getDownloadUrl());
                // 11. Add the ImageDTO to the list of saved image DTOs
                savedImageDtos.add(imageDTO);

            } catch (IOException | SQLException e){
                // 12. Handle any errors that occur during file processing or database operations
                throw new RuntimeException(e.getMessage());
            }
        }
        // 13. Return the list of ImageDTOs for the successfully saved images
        return savedImageDtos;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileName(file.getOriginalFilename());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }


    }
}
