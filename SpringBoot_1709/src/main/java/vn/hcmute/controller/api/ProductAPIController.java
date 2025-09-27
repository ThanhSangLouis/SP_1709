package vn.hcmute.controller.api;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import vn.hcmute.entity.Product;
import vn.hcmute.entity.Category;
import vn.hcmute.model.Response;
import vn.hcmute.service.ProductService;
import vn.hcmute.service.CategoryService;
import vn.hcmute.service.StorageService;

@RestController
@RequestMapping(path = "/api/product")
@Tag(name = "Product API", description = "API quản lý sản phẩm")
public class ProductAPIController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    @Operation(summary = "Lấy danh sách sản phẩm", description = "Trả về danh sách sản phẩm với phân trang và tìm kiếm")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công",
            content = @Content(schema = @Schema(implementation = Response.class)))
    })
    public ResponseEntity<?> getAllProducts(
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "ID danh mục") @RequestParam(value = "categoryId", required = false) Long categoryId,
            @Parameter(description = "Trạng thái sản phẩm") @RequestParam(value = "status", required = false) String status,
            @Parameter(description = "Giá tối thiểu") @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @Parameter(description = "Giá tối đa") @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @Parameter(description = "Số trang") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "Sắp xếp theo") @RequestParam(value = "sortBy", defaultValue = "productId") String sortBy,
            @Parameter(description = "Thứ tự sắp xếp") @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Product.ProductStatus productStatus = null;
            if (status != null && !status.isEmpty()) {
                try {
                    productStatus = Product.ProductStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                        .body(new Response(false, "Trạng thái không hợp lệ", null));
                }
            }
            
            Page<Product> products;
            if (keyword != null || categoryId != null || productStatus != null || minPrice != null || maxPrice != null) {
                products = productService.findWithFilters(keyword, categoryId, productStatus, minPrice, maxPrice, pageable);
            } else {
                products = productService.findAll(pageable);
            }
            
            return new ResponseEntity<Response>(
                new Response(true, "Thành công", products), 
                HttpStatus.OK
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(false, "Lỗi server: " + e.getMessage(), null));
        }
    }

    @PostMapping(path = "/getProduct")
    @Operation(summary = "Lấy sản phẩm theo ID", description = "Trả về thông tin sản phẩm theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công",
            content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm")
    })
    public ResponseEntity<?> getProduct(
            @Parameter(description = "ID của sản phẩm", required = true)
            @Validated @RequestParam("id") Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<Response>(
                new Response(true, "Thành công", product.get()), 
                HttpStatus.OK
            );
        } else {
            return new ResponseEntity<Response>(
                new Response(false, "Không tìm thấy sản phẩm", null), 
                HttpStatus.NOT_FOUND
            );
        }
    }

    @PostMapping(path = "/addProduct")
    @Operation(summary = "Thêm sản phẩm mới", description = "Tạo sản phẩm mới với thông tin đầy đủ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thêm thành công",
            content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<?> addProduct(
            @Parameter(description = "Tên sản phẩm", required = true)
            @Validated @RequestParam("productName") String productName,
            @Parameter(description = "Mô tả sản phẩm")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Giá sản phẩm", required = true)
            @Validated @RequestParam("price") BigDecimal price,
            @Parameter(description = "Số lượng tồn kho", required = true)
            @Validated @RequestParam("stockQuantity") Integer stockQuantity,
            @Parameter(description = "ID danh mục", required = true)
            @Validated @RequestParam("categoryId") Long categoryId,
            @Parameter(description = "Hình ảnh sản phẩm", required = false)
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            // Kiểm tra tên sản phẩm trùng lặp
            Optional<Product> existingProduct = productService.findByProductName(productName);
            if (existingProduct.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(false, "Tên sản phẩm đã tồn tại", null));
            }
            
            // Kiểm tra danh mục tồn tại
            Optional<Category> category = categoryService.findById(categoryId);
            if (category.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(false, "Danh mục không tồn tại", null));
            }
            
            Product product = new Product();
            product.setProductName(productName);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQuantity(stockQuantity);
            product.setCategory(category.get());
            
            // Xử lý upload ảnh
            if (image != null && !image.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String fileName = storageService.getStorageFilename(image, uuid.toString());
                product.setProductImage(fileName);
                storageService.store(image, fileName);
            }
            
            Product savedProduct = productService.save(product);
            
            return new ResponseEntity<Response>(
                new Response(true, "Thêm sản phẩm thành công", savedProduct), 
                HttpStatus.OK
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(false, "Lỗi server: " + e.getMessage(), null));
        }
    }

    @PutMapping(path = "/updateProduct")
    @Operation(summary = "Cập nhật sản phẩm", description = "Cập nhật thông tin sản phẩm")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
            content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "400", description = "Không tìm thấy sản phẩm")
    })
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "ID sản phẩm cần cập nhật", required = true)
            @Validated @RequestParam("productId") Long productId,
            @Parameter(description = "Tên sản phẩm mới", required = true)
            @Validated @RequestParam("productName") String productName,
            @Parameter(description = "Mô tả sản phẩm mới")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Giá sản phẩm mới", required = true)
            @Validated @RequestParam("price") BigDecimal price,
            @Parameter(description = "Số lượng tồn kho mới", required = true)
            @Validated @RequestParam("stockQuantity") Integer stockQuantity,
            @Parameter(description = "ID danh mục mới", required = true)
            @Validated @RequestParam("categoryId") Long categoryId,
            @Parameter(description = "Trạng thái sản phẩm")
            @RequestParam(value = "status", required = false) String status,
            @Parameter(description = "Hình ảnh sản phẩm mới", required = false)
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            Optional<Product> productOpt = productService.findById(productId);
            if (productOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(false, "Không tìm thấy sản phẩm", null));
            }
            
            // Kiểm tra tên sản phẩm trùng lặp (trừ sản phẩm hiện tại)
            Optional<Product> existingProduct = productService.findByProductNameAndProductIdNot(productName, productId);
            if (existingProduct.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(false, "Tên sản phẩm đã tồn tại", null));
            }
            
            // Kiểm tra danh mục tồn tại
            Optional<Category> category = categoryService.findById(categoryId);
            if (category.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(false, "Danh mục không tồn tại", null));
            }
            
            Product product = productOpt.get();
            product.setProductName(productName);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQuantity(stockQuantity);
            product.setCategory(category.get());
            
            // Cập nhật trạng thái nếu có
            if (status != null && !status.isEmpty()) {
                try {
                    product.setStatus(Product.ProductStatus.valueOf(status.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                        .body(new Response(false, "Trạng thái không hợp lệ", null));
                }
            }
            
            // Xử lý upload ảnh mới
            if (image != null && !image.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String fileName = storageService.getStorageFilename(image, uuid.toString());
                product.setProductImage(fileName);
                storageService.store(image, fileName);
            }
            
            Product updatedProduct = productService.save(product);
            
            return new ResponseEntity<Response>(
                new Response(true, "Cập nhật sản phẩm thành công", updatedProduct), 
                HttpStatus.OK
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(false, "Lỗi server: " + e.getMessage(), null));
        }
    }

    @DeleteMapping(path = "/deleteProduct")
    @Operation(summary = "Xóa sản phẩm", description = "Xóa sản phẩm theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa thành công",
            content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "400", description = "Không tìm thấy sản phẩm")
    })
    public ResponseEntity<?> deleteProduct(
            @Parameter(description = "ID sản phẩm cần xóa", required = true)
            @Validated @RequestParam("productId") Long productId) {
        try {
            Optional<Product> productOpt = productService.findById(productId);
            if (productOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(false, "Không tìm thấy sản phẩm", null));
            }
            
            Product product = productOpt.get();
            productService.delete(product);
            
            return new ResponseEntity<Response>(
                new Response(true, "Xóa sản phẩm thành công", product), 
                HttpStatus.OK
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(false, "Lỗi server: " + e.getMessage(), null));
        }
    }

    @PostMapping(path = "/updateStock")
    @Operation(summary = "Cập nhật tồn kho", description = "Cập nhật số lượng tồn kho của sản phẩm")
    public ResponseEntity<?> updateStock(
            @Parameter(description = "ID sản phẩm", required = true)
            @RequestParam("productId") Long productId,
            @Parameter(description = "Số lượng mới", required = true)
            @RequestParam("quantity") Integer quantity) {
        
        boolean success = productService.updateStock(productId, quantity);
        if (success) {
            return ResponseEntity.ok(new Response(true, "Cập nhật tồn kho thành công", null));
        } else {
            return ResponseEntity.badRequest()
                .body(new Response(false, "Không thể cập nhật tồn kho", null));
        }
    }

    @PostMapping(path = "/changeStatus")
    @Operation(summary = "Thay đổi trạng thái sản phẩm", description = "Kích hoạt/tạm dừng sản phẩm")
    public ResponseEntity<?> changeStatus(
            @Parameter(description = "ID sản phẩm", required = true)
            @RequestParam("productId") Long productId,
            @Parameter(description = "Trạng thái mới", required = true)
            @RequestParam("status") String status) {
        
        try {
            Product.ProductStatus newStatus = Product.ProductStatus.valueOf(status.toUpperCase());
            boolean success = false;
            
            switch (newStatus) {
                case ACTIVE:
                    success = productService.activateProduct(productId);
                    break;
                case INACTIVE:
                    success = productService.deactivateProduct(productId);
                    break;
                case OUT_OF_STOCK:
                    success = productService.setOutOfStock(productId);
                    break;
            }
            
            if (success) {
                return ResponseEntity.ok(new Response(true, "Thay đổi trạng thái thành công", null));
            } else {
                return ResponseEntity.badRequest()
                    .body(new Response(false, "Không thể thay đổi trạng thái", null));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new Response(false, "Trạng thái không hợp lệ", null));
        }
    }
}
