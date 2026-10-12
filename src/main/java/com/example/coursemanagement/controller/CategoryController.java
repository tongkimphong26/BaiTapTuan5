package com.example.coursemanagement.controller;

import com.example.coursemanagement.exception.NotFoundException;
import com.example.coursemanagement.model.CourseCategory;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CourseCategoryRepository categoryRepo;
    private final CourseRepository courseRepo;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("category", new CourseCategory());
        return "categories/index";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("category") CourseCategory category,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("categories", categoryRepo.findAll());
            return "categories/index";
        }
        categoryRepo.save(category);
        ra.addFlashAttribute("message", "Đã thêm loại khóa học");
        return "redirect:/categories";
    }

    // Ràng buộc 1: không cho xóa loại nếu vẫn còn khóa học thuộc loại đó
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        CourseCategory c = categoryRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy loại khóa học có Id = " + id));
        long n = courseRepo.countByCourseCategory_Id(id);
        if (n > 0) {
            ra.addFlashAttribute("error",
                "Không thể xóa loại \"" + c.getName() + "\" vì vẫn còn " + n + " khóa học thuộc loại này");
        } else {
            categoryRepo.delete(c);
            ra.addFlashAttribute("message", "Đã xóa loại khóa học");
        }
        return "redirect:/categories";
    }
}
