package com.example.coursemanagement.config;

import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.model.CourseCategory;
import com.example.coursemanagement.model.CourseImage;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

// Tạo dữ liệu mẫu (3 loại, 7 khóa học) khi database còn trống
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final CourseCategoryRepository categoryRepo;
    private final CourseRepository courseRepo;

    @Override
    public void run(String... args) {
        if (categoryRepo.count() > 0) return;

        CourseCategory web  = cat("Lập trình Web", "Các khóa học phát triển web");
        CourseCategory data = cat("Dữ liệu", "Phân tích và cơ sở dữ liệu");
        CourseCategory lang = cat("Ngoại ngữ", "Các khóa học tiếng Anh");

        course(web, "ASP.NET Core", 4500000, 60, true, img("web1"), img("web2"));
        course(web, "ReactJS", 3800000, 40, true, img("react1"));
        course(web, "HTML CSS cơ bản", 1200000, 24, true, img("html1"));
        course(data, "Python phân tích dữ liệu", 3000000, 45, true, img("py1"), img("py2"));
        course(data, "SQL cơ bản", 900000, 20, true);                    // chưa có ảnh
        course(lang, "Tiếng Anh giao tiếp", 2500000, 30, true, img("en1"));
        course(lang, "IELTS 6.5", 6000000, 90, false, img("ielts1"));    // Ngừng tuyển sinh
    }

    private String img(String seed) {
        return "https://picsum.photos/seed/" + seed + "/400/250";
    }

    private CourseCategory cat(String name, String desc) {
        CourseCategory c = new CourseCategory();
        c.setName(name);
        c.setDescription(desc);
        return categoryRepo.save(c);
    }

    private void course(CourseCategory cat, String name, long fee, int duration,
                        boolean active, String... urls) {
        Course c = new Course();
        c.setName(name);
        c.setTuitionFee(BigDecimal.valueOf(fee));
        c.setDuration(duration);
        c.setActive(active);
        c.setDescription("Khóa học " + name);
        c.setCourseCategory(cat);
        for (int i = 0; i < urls.length; i++) {
            CourseImage im = new CourseImage();
            im.setImageUrl(urls[i]);
            im.setThumbnail(i == 0);   // ảnh đầu tiên là ảnh đại diện
            im.setCourse(c);
            c.getImages().add(im);
        }
        courseRepo.save(c);
    }
}
