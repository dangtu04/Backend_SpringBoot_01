package com.dangthanhtu.example05.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dangthanhtu.example05.entity.Cart;
import com.dangthanhtu.example05.entity.Category;
import com.dangthanhtu.example05.entity.Product;
import com.dangthanhtu.example05.entity.Brand;
import com.dangthanhtu.example05.exceptions.APIException;
import com.dangthanhtu.example05.exceptions.ResourceNotFoundException;
import com.dangthanhtu.example05.payloads.CartDTO;
import com.dangthanhtu.example05.payloads.ProductDTO;
import com.dangthanhtu.example05.payloads.ProductResponse;
import com.dangthanhtu.example05.repository.BrandRepo;
import com.dangthanhtu.example05.repository.CartRepo;
import com.dangthanhtu.example05.repository.CategoryRepo;
import com.dangthanhtu.example05.repository.ProductRepo;
import com.dangthanhtu.example05.service.CartService;
import com.dangthanhtu.example05.service.FileService;
import com.dangthanhtu.example05.service.ProductService;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private BrandRepo brandRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${project.image}")
    private String path;

    @Override
public ProductDTO addProduct(Long categoryId, Long brandId, Product product) {
    Category category = categoryRepo.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

    Brand brand = brandRepo.findById(brandId)
            .orElseThrow(() -> new ResourceNotFoundException("Brand", "brandId", brandId));

    boolean isProductNotPresent = true;
    List<Product> products = category.getProducts();
    for (Product existingProduct : products) {
        if (existingProduct.getProductName().equals(product.getProductName())
                && existingProduct.getDescription().equals(product.getDescription())) {
            isProductNotPresent = false;
            break;
        }
    }

    if (isProductNotPresent) {
        product.setImage("default.png");
        product.setCategory(category);
        product.setBrand(brand);
        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepo.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    } else {
        throw new APIException("Product already exists !!!");
    }
}
    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepo.findAll(pageDetails);

        List<ProductDTO> productDTOs = pageProducts.getContent()
                .stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        Optional<Product> productOptional = productRepo.findById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return modelMapper.map(product, ProductDTO.class);
        } else {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }
    }


    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
    // Kiểm tra danh mục tồn tại
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Tạo đối tượng Sort và Pageable
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // Lọc sản phẩm theo danh mục
        Page<Product> pageProducts = productRepo.findByCategory(category, pageDetails); // Đổi từ findAll sang findByCategory

        List<Product> products = pageProducts.getContent();
        if (products.isEmpty()) {
            throw new APIException(category.getCategoryName() + " category doesn't contain any products !!!");
        }

        // Chuyển đổi Product sang ProductDTO
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        // Tạo và trả về ProductResponse
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }

    @Override
    public Mono<ProductResponse> searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Xử lý và validate keyword
        String trimmedKeyword = keyword.trim();
        if (trimmedKeyword.isEmpty()) {
            return Mono.error(new APIException("Please enter a valid keyword"));
        }
    
        // Tạo Sort và Pageable
        Sort sort = Sort.by(sortBy);
        sort = sortOrder.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    
        // Gọi repository
        return Mono.fromCallable(() -> productRepo.searchByProductNameContainingIgnoreCase(trimmedKeyword, pageable))
                .flatMap(pageProducts -> {
                    // Xử lý kết quả
                    List<Product> products = pageProducts.getContent();
                    if (products.isEmpty()) {
                        return Mono.error(new APIException("No products found with keyword: " + keyword));
                    }
    
                    // Map sang DTO
                    List<ProductDTO> productDTOs = products.stream()
                            .map(product -> modelMapper.map(product, ProductDTO.class))
                            .collect(Collectors.toList());
    
                    // Tạo response
                    ProductResponse response = new ProductResponse();
                    response.setContent(productDTOs);
                    response.setPageNumber(pageProducts.getNumber());
                    response.setPageSize(pageProducts.getSize());
                    response.setTotalElements(pageProducts.getTotalElements());
                    response.setTotalPages(pageProducts.getTotalPages());
                    response.setLastPage(pageProducts.isLast());
    
                    return Mono.just(response);
                });
    }

    @Override
public ProductDTO updateProduct(Long productId, Product product) {
    Product productFromDB = productRepo.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

    if (product.getCategory() != null) {
        productFromDB.setCategory(product.getCategory());
    }
    if (product.getBrand() != null) {
        productFromDB.setBrand(product.getBrand());
    }

    productFromDB.setProductName(product.getProductName());
    productFromDB.setDescription(product.getDescription());
    productFromDB.setPrice(product.getPrice());
    productFromDB.setDiscount(product.getDiscount());
    productFromDB.setSpecialPrice(product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice()));

    Product savedProduct = productRepo.save(productFromDB);

    List<Cart> carts = cartRepo.findCartsByProductId(productId);
    List<CartDTO> cartDTOs = carts.stream().map(cart -> {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(cartItem -> modelMapper.map(cartItem.getProduct(), ProductDTO.class))
                .collect(Collectors.toList());
        cartDTO.setProducts(products);
        return cartDTO;
    }).collect(Collectors.toList());

    cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

    return modelMapper.map(savedProduct, ProductDTO.class);
}

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDB = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String fileName = fileService.uploadImage(path, image);
        productFromDB.setImage(fileName);

        Product updatedProduct = productRepo.save(productFromDB);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public String deleteProduct(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        List<Cart> carts = cartRepo.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepo.delete(product);
        return "Product with productId: " + productId + " deleted successfully !!!";
    }

    @Override
    public InputStream getProductImage(String fileName) throws FileNotFoundException {
        return fileService.getResource(path, fileName);
    }

    @Override
    public ProductResponse searchByBrand(Long brandId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Brand brand = brandRepo.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "brandId", brandId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> pageProducts = productRepo.findByBrand(brand, pageDetails);

        List<Product> products = pageProducts.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products found for the brand: " + brand.getBrandName());
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchByCategoryAndBrand(Long categoryId, Long brandId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Brand brand = brandRepo.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "brandId", brandId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> pageProducts = productRepo.findByCategoryAndBrand(category, brand, pageDetails);

        List<Product> products = pageProducts.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products found for the category: " + category.getCategoryName() + " and brand: " + brand.getBrandName());
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }
}