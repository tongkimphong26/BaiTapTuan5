package com.example.coursemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_categories")
@Getter @Setter @NoArgsConstructor
public class CourseCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên loại khóa học không được để trống")
    private String name;

    @Column(length = 1000)
    private String description;

    // Một CourseCategory có nhiều Course
    @OneToMany(mappedBy = "courseCategory")
    private List<Course> courses = new ArrayList<>();
}
