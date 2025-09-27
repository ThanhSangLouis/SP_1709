package vn.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

import vn.hcmute.entity.Role;
import vn.hcmute.entity.User;
import vn.hcmute.repository.RoleRepository;
import vn.hcmute.repository.UserRepository;

@Controller
@RequestMapping("/init")
public class DataInitController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute("_csrf")
    public CsrfToken csrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @GetMapping("/data")
    public String initData(RedirectAttributes redirectAttributes) {
        try {
            // Tạo role ADMIN nếu chưa có
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName("ADMIN");
                    return roleRepository.save(role);
                });

            // Tạo user admin nếu chưa có
            if (!userRepository.findByUsername("admin").isPresent()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("Administrator");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }

            redirectAttributes.addFlashAttribute("success", "Dữ liệu đã được khởi tạo thành công!");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi khởi tạo dữ liệu: " + e.getMessage());
            return "redirect:/login";
        }
    }
}
