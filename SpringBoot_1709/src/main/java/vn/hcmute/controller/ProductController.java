package vn.hcmute.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

import vn.hcmute.entity.Product;
import vn.hcmute.entity.Category;
import vn.hcmute.service.ProductService;
import vn.hcmute.service.CategoryService;
import vn.hcmute.service.StorageService;

import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StorageService storageService;

    @ModelAttribute("_csrf")
    public CsrfToken csrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @GetMapping
    public String listProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "productId") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Product.ProductStatus productStatus = null;
            if (status != null && !status.isEmpty()) {
                try {
                    productStatus = Product.ProductStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Ignore invalid status
                }
            }
            
            Page<Product> products;
            if (keyword != null || categoryId != null || productStatus != null || minPrice != null || maxPrice != null) {
                products = productService.findWithFilters(keyword, categoryId, productStatus, minPrice, maxPrice, pageable);
            } else {
                products = productService.findAll(pageable);
            }
            
            List<Category> categories = categoryService.findAll();
            
            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("keyword", keyword);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("status", status);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            
            return "product/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách sản phẩm: " + e.getMessage());
            return "product/list";
        }
    }

    @GetMapping("/add")
    public String addProductForm(Model model) {
        Product product = new Product();
        List<Category> categories = categoryService.findAll();
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("isEdit", false);
        
        return "product/addOrEdit";
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
            return "redirect:/products";
        }
        
        List<Category> categories = categoryService.findAll();
        
        model.addAttribute("product", productOpt.get());
        model.addAttribute("categories", categories);
        model.addAttribute("isEdit", true);
        
        return "product/addOrEdit";
    }

    @PostMapping("/save")
    public String saveProduct(
            @ModelAttribute("product") Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "removeImage", required = false) String removeImage,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Kiểm tra tên sản phẩm trùng lặp
            if (product.getProductId() == null) {
                // Thêm mới
                Optional<Product> existingProduct = productService.findByProductName(product.getProductName());
                if (existingProduct.isPresent()) {
                    redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại");
                    return "redirect:/products/add";
                }
            } else {
                // Cập nhật
                Optional<Product> existingProduct = productService.findByProductNameAndProductIdNot(
                    product.getProductName(), product.getProductId());
                if (existingProduct.isPresent()) {
                    redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại");
                    return "redirect:/products/edit/" + product.getProductId();
                }
            }
            
            // Xử lý upload ảnh
            if (imageFile != null && !imageFile.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String fileName = storageService.getStorageFilename(imageFile, uuid.toString());
                product.setProductImage(fileName);
                storageService.store(imageFile, fileName);
            } else if ("true".equals(removeImage)) {
                product.setProductImage(null);
            }
            
            productService.save(product);
            
            if (product.getProductId() == null) {
                redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công");
            } else {
                redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công");
            }
            
            return "redirect:/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu sản phẩm: " + e.getMessage());
            if (product.getProductId() == null) {
                return "redirect:/products/add";
            } else {
                return "redirect:/products/edit/" + product.getProductId();
            }
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Product> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
                return "redirect:/products";
            }
            
            productService.delete(productOpt.get());
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công");
            
            return "redirect:/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return "redirect:/products";
        }
    }
}
