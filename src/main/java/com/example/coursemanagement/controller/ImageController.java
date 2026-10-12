package com.example.coursemanagement.controller;

import com.example.coursemanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ImageController {
    private final CourseService courseService;

    @PostMapping("/courses/{courseId}/images")
    public String add(@PathVariable Long courseId,
                      @RequestParam String imageUrl,
                      @RequestParam(defaultValue = "false") boolean thumbnail,
                      RedirectAttributes ra) {
        courseService.getOrThrow(courseId);
        if (imageUrl == null || imageUrl.isBlank()
                || !(imageUrl.trim().startsWith("http://") || imageUrl.trim().startsWith("https://"))) {
            ra.addFlashAttribute("error", "Đường dẫn ảnh không hợp lệ (phải bắt đầu bằng http:// hoặc https://)");
        } else {
            courseService.addImage(courseId, imageUrl, thumbnail);
            ra.addFlashAttribute("message", "Đã thêm ảnh");
        }
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/courses/{courseId}/images/{imageId}/thumbnail")
    public String setThumbnail(@PathVariable Long courseId, @PathVariable Long imageId,
                               RedirectAttributes ra) {
        courseService.setThumbnail(courseId, imageId);
        ra.addFlashAttribute("message", "Đã đổi ảnh đại diện");
        return "redirect:/courses/" + courseId;
    }
}
