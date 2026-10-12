package com.example.coursemanagement.service;

import com.example.coursemanagement.dto.CourseForm;
import com.example.coursemanagement.exception.NotFoundException;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.model.CourseImage;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseImageRepository;
import com.example.coursemanagement.repository.CourseRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepo;
    private final CourseCategoryRepository categoryRepo;
    private final CourseImageRepository imageRepo;

    public Course getOrThrow(Long id) {
        return courseRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy khóa học có Id = " + id));
    }

    // ---------- Tìm kiếm + lọc + sắp xếp (tất cả chạy ở database, không lọc bằng vòng lặp) ----------
    public List<Course> search(String keyword, Long categoryId, String status,
                               BigDecimal minFee, BigDecimal maxFee, String sort) {
        Specification<Course> spec = (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (keyword != null && !keyword.isBlank())
                ps.add(cb.like(cb.lower(root.<String>get("name")), "%" + keyword.trim().toLowerCase() + "%"));
            if (categoryId != null)
                ps.add(cb.equal(root.get("courseCategory").get("id"), categoryId));
            if ("active".equals(status))        // Ràng buộc 3: Đang tuyển sinh -> IsActive = true
                ps.add(cb.isTrue(root.<Boolean>get("active")));
            else if ("inactive".equals(status))
                ps.add(cb.isFalse(root.<Boolean>get("active")));
            if (minFee != null)
                ps.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("tuitionFee"), minFee));
            if (maxFee != null)
                ps.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("tuitionFee"), maxFee));
            return cb.and(ps.toArray(new Predicate[0]));
        };
        Sort s = switch (sort == null ? "" : sort) {
            case "fee_asc"      -> Sort.by("tuitionFee").ascending();
            case "fee_desc"     -> Sort.by("tuitionFee").descending();
            case "duration_asc" -> Sort.by("duration").ascending();
            default             -> Sort.by("id").ascending();
        };
        return courseRepo.findAll(spec, s);
    }

    // ---------- Validation nghiệp vụ cho Create/Edit ----------
    public void validate(CourseForm f, BindingResult br) {
        if (f.getCategoryId() != null && !categoryRepo.existsById(f.getCategoryId())) {
            br.rejectValue("categoryId", "notfound", "Loại khóa học không tồn tại");
            return;
        }
        if (f.getName() != null && !f.getName().isBlank() && f.getCategoryId() != null) {
            String n = f.getName().trim();
            boolean dup = (f.getId() == null)
                ? courseRepo.existsByNameIgnoreCaseAndCourseCategory_Id(n, f.getCategoryId())
                : courseRepo.existsByNameIgnoreCaseAndCourseCategory_IdAndIdNot(n, f.getCategoryId(), f.getId());
            if (dup) br.rejectValue("name", "duplicate", "Tên khóa học đã tồn tại trong loại khóa học này");
        }
    }

    @Transactional
    public Course save(CourseForm f) {
        Course c = (f.getId() == null) ? new Course() : getOrThrow(f.getId());
        c.setName(f.getName().trim());
        c.setTuitionFee(f.getTuitionFee());
        c.setDuration(f.getDuration());
        c.setActive(f.isActive());
        c.setDescription(f.getDescription());
        c.setCourseCategory(categoryRepo.findById(f.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Loại khóa học không tồn tại")));
        return courseRepo.save(c);
    }

    // ---------- Quản lý ảnh (Chức năng 4) ----------
    @Transactional
    public void addImage(Long courseId, String url, boolean thumbnail) {
        Course c = getOrThrow(courseId);
        CourseImage img = new CourseImage();
        img.setImageUrl(url.trim());
        img.setCourse(c);
        if (thumbnail) c.getImages().forEach(i -> i.setThumbnail(false)); // ảnh đại diện cũ -> ảnh thường
        img.setThumbnail(thumbnail);
        c.getImages().add(img);
        courseRepo.save(c);
    }

    @Transactional
    public void setThumbnail(Long courseId, Long imageId) {
        Course c = getOrThrow(courseId);
        CourseImage target = imageRepo.findById(imageId)
            .filter(i -> i.getCourse().getId().equals(courseId))
            .orElseThrow(() -> new NotFoundException("Không tìm thấy ảnh có Id = " + imageId));
        c.getImages().forEach(i -> i.setThumbnail(false)); // tối đa 1 ảnh đại diện
        target.setThumbnail(true);
    }
}
