package vn.hcmute.controller.api;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import vn.hcmute.entity.Category;
import vn.hcmute.model.Response;
import vn.hcmute.service.CategoryService;
import vn.hcmute.service.StorageService;

@RestController
@RequestMapping(path = "/api/category")
@Tag(name = "Category API", description = "API quản lý danh mục sản phẩm")
public class CategoryAPIController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    @Operation(summary = "Lấy tất cả danh mục", description = "Trả về danh sách tất cả danh mục sản phẩm")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công",
            content = @Content(schema = @Schema(implementation = Response.class)))
    })
    public ResponseEntity<?> getAllCategory() {
        return new ResponseEntity<Response>(
            new Response(true, "Thành công", categoryService.findAll()), 
            HttpStatus.OK
        );
    }

    @PostMapping(path = "/getCategory")
    @Operation(summary = "Lấy danh mục theo ID", description = "Trả về thông tin danh mục theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công",
            content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
    })
    public ResponseEntity<?> getCategory(
            @Parameter(description = "ID của danh mục", required = true)
            @Validated @RequestParam("id") Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            return new ResponseEntity<Response>(
                new Response(true, "Thành công", category.get()), 
                HttpStatus.OK
            );
        } else {
            return new ResponseEntity<Response>(
                new Response(false, "Thất bại", null), 
                HttpStatus.NOT_FOUND
            );
        }
    }

    @PostMapping(path = "/addCategory")
    @Operation(summary = "Thêm danh mục mới", description = "Tạo danh mục sản phẩm mới với tên và hình ảnh")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thêm thành công",
            content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "400", description = "Danh mục đã tồn tại")
    })
    public ResponseEntity<?> addCategory(
            @Parameter(description = "Tên danh mục", required = true)
            @Validated @RequestParam("categoryName") String categoryName,
            @Parameter(description = "Hình ảnh danh mục", required = false)
            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        
        Optional<Category> optCategory = categoryService.findByCategoryName(categoryName);
        if (optCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Category đã tồn tại trong hệ thống");
        } else {
            Category category = new Category();
            
            // Kiểm tra tồn tại file, lưu file
            if (icon != null && !icon.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                // Lưu file vào trường categoryImage
                category.setCategoryImage(storageService.getStorageFilename(icon, uuString));
                storageService.store(icon, category.getCategoryImage());
            } else {
                // Nếu không có file ảnh, set null
                category.setCategoryImage(null);
            }
            
            category.setCategoryName(categoryName);
            categoryService.save(category);
            
            return new ResponseEntity<Response>(
                new Response(true, "Thêm Thành công", category), 
                HttpStatus.OK
            );
        }
    }

    @PutMapping(path = "/updateCategory")
    @Operation(summary = "Cập nhật danh mục", description = "Cập nhật thông tin danh mục sản phẩm")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
            content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "400", description = "Không tìm thấy danh mục")
    })
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "ID danh mục cần cập nhật", required = true)
            @Validated @RequestParam("categoryId") Long categoryId,
            @Parameter(description = "Tên danh mục mới", required = true)
            @Validated @RequestParam("categoryName") String categoryName,
            @Parameter(description = "Hình ảnh danh mục mới", required = false)
            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<Response>(
                new Response(false, "Không tìm thấy Category", null), 
                HttpStatus.BAD_REQUEST
            );
        } else if (optCategory.isPresent()) {
            // Kiểm tra tồn tại file, lưu file
            if (icon != null && !icon.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                // Lưu file vào trường categoryImage
                optCategory.get().setCategoryImage(storageService.getStorageFilename(icon, uuString));
                storageService.store(icon, optCategory.get().getCategoryImage());
            }
            // Nếu không có file ảnh mới, giữ nguyên ảnh cũ
            
            optCategory.get().setCategoryName(categoryName);
            categoryService.save(optCategory.get());
            
            return new ResponseEntity<Response>(
                new Response(true, "Cập nhật Thành công", optCategory.get()), 
                HttpStatus.OK
            );
        }
        return null;
    }

    @DeleteMapping(path = "/deleteCategory")
    @Operation(summary = "Xóa danh mục", description = "Xóa danh mục sản phẩm theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa thành công",
            content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "400", description = "Không tìm thấy danh mục")
    })
    public ResponseEntity<?> deleteCategory(
            @Parameter(description = "ID danh mục cần xóa", required = true)
            @Validated @RequestParam("categoryId") Long categoryId) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<Response>(
                new Response(false, "Không tìm thấy Category", null), 
                HttpStatus.BAD_REQUEST
            );
        } else if (optCategory.isPresent()) {
            categoryService.delete(optCategory.get());
            return new ResponseEntity<Response>(
                new Response(true, "Xóa Thành công", optCategory.get()), 
                HttpStatus.OK
            );
        }
        return null;
    }
}
