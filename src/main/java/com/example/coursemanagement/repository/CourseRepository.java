package com.example.coursemanagement.repository;

import com.example.coursemanagement.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    boolean existsByNameIgnoreCaseAndCourseCategory_Id(String name, Long categoryId);

    boolean existsByNameIgnoreCaseAndCourseCategory_IdAndIdNot(String name, Long categoryId, Long id);

    long countByCourseCategory_Id(Long categoryId);
}
