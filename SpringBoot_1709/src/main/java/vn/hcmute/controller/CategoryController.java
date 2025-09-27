package vn.hcmute.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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

import vn.hcmute.entity.Category;
import vn.hcmute.service.CategoryService;
import vn.hcmute.service.FileStorageService;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @Autowired
    private FileStorageService fileStorageService;

    @ModelAttribute("_csrf")
    public CsrfToken csrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String keyword,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Category> list = keyword.isEmpty()
                ? service.findAll(pageable)
                : service.search(keyword, pageable);

        model.addAttribute("categories", list);
        model.addAttribute("keyword", keyword);
        return "category/list"; // template located at templates/category/list.html
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/addOrEdit"; // template located at templates/category/addOrEdit.html
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("category") Category category,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                       @RequestParam(value = "existingImage", required = false) String existingImage,
                       @RequestParam(value = "removeImage", required = false, defaultValue = "false") boolean removeImage,
                       RedirectAttributes redirectAttributes) {
        String imagePath = existingImage;

        try {
            if (removeImage && StringUtils.hasText(imagePath)) {
                fileStorageService.delete(imagePath);
                imagePath = null;
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                if (StringUtils.hasText(imagePath)) {
                    fileStorageService.delete(imagePath);
                }
                imagePath = fileStorageService.storeCategoryImage(imageFile);
            }
        } catch (IOException ex) {
            redirectAttributes.addFlashAttribute("error", "Không thể lưu hình ảnh danh mục: " + ex.getMessage());
            return "redirect:/categories";
        }

        category.setCategoryImage(imagePath);
        service.save(category);
        redirectAttributes.addFlashAttribute("success", "Lưu danh mục thành công!");
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("category", service.findById(id).orElse(new Category()));
        return "category/addOrEdit";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/categories";
    }
}
