package vn.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.hcmute.entity.Category;
import vn.hcmute.service.CategoryService;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

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
        return "category/list"; // nằm trong templates/category/list.html
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/addOrEdit"; // nằm trong templates/category/addOrEdit.html
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("category") Category category) {
        service.save(category);
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
