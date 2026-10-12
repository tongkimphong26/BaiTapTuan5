package com.example.coursemanagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses",
       uniqueConstraints = @UniqueConstraint(columnNames = {"name", "course_category_id"}))
@Getter @Setter @NoArgsConstructor
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal tuitionFee;

    @Column(nullable = false)
    private Integer duration;           // số giờ

    @Column(name = "is_active")
    private boolean active = true;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_category_id")
    private CourseCategory courseCategory;

    // Một Course có nhiều CourseImage
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CourseImage> images = new ArrayList<>();

    // Chức năng 3: chỉ tính khi hiển thị, KHÔNG có cột trong DB (không có field)
    public String getDurationType() {
        return duration != null && duration <= 30 ? "Ngắn hạn" : "Dài hạn";
    }

    public CourseImage getThumbnailImage() {
        return images.stream().filter(CourseImage::isThumbnail).findFirst().orElse(null);
    }
}
