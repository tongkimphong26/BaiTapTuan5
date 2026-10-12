package com.example.coursemanagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_images")
@Getter @Setter @NoArgsConstructor
public class CourseImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "is_thumbnail")
    private boolean thumbnail = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;
}
