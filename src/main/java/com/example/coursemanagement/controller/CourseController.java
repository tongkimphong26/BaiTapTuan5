package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CourseForm;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.service.CourseService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final CourseCategoryRepository categoryRepo;

    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) BigDecimal minFee,
                        @RequestParam(required = false) BigDecimal maxFee,
                        @RequestParam(required = false) String sort,
                        Model model) {
        List<Course> courses;
        if (minFee != null && maxFee != null && minFee.compareTo(maxFee) > 0) {
            model.addAttribute("error", "Học phí tối thiểu không được lớn hơn học phí tối đa");
            courses = List.of();
        } else {
            courses = courseService.search(keyword, categoryId, status, minFee, maxFee, sort);
        }
        model.addAttribute("courses", courses);
        model.addAttribute("categories", categoryRepo.findAll());
        // giữ lại điều kiện đã nhập trên form
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("minFee", minFee);
        model.addAttribute("maxFee", maxFee);
        model.addAttribute("sort", sort);
        return "courses/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getOrThrow(id));
        return "courses/detail";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        return showForm(new CourseForm(), "Thêm khóa học", model);
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") CourseForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        courseService.validate(form, br);
        if (br.hasErrors()) return showForm(form, "Thêm khóa học", model);
        courseService.save(form);
        ra.addFlashAttribute("message", "Thêm khóa học thành công");
        return "redirect:/courses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Course c = courseService.getOrThrow(id);
        CourseForm f = new CourseForm();
        f.setId(c.getId());
        f.setName(c.getName());
        f.setTuitionFee(c.getTuitionFee());
        f.setDuration(c.getDuration());
        f.setActive(c.isActive());
        f.setDescription(c.getDescription());
        f.setCategoryId(c.getCourseCategory().getId());
        return showForm(f, "Sửa khóa học", model);
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("form") CourseForm form,
                       BindingResult br, Model model, RedirectAttributes ra) {
        courseService.getOrThrow(id);      // id không tồn tại -> trang lỗi thân thiện
        form.setId(id);
        courseService.validate(form, br);
        if (br.hasErrors()) return showForm(form, "Sửa khóa học", model);
        courseService.save(form);
        ra.addFlashAttribute("message", "Cập nhật khóa học thành công");
        return "redirect:/courses";
    }

    private String showForm(CourseForm form, String title, Model model) {
        model.addAttribute("form", form);
        model.addAttribute("title", title);
        model.addAttribute("categories", categoryRepo.findAll()); // lấy từ database
        return "courses/form";
    }
}
