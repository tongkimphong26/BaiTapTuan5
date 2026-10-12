package com.example.coursemanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class CourseForm {
    private Long id;

    @NotBlank(message = "Tên khóa học không được để trống")
    private String name;

    @NotNull(message = "Học phí không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Học phí phải lớn hơn 0")
    private BigDecimal tuitionFee;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    private Integer duration;

    private boolean active = true;
    private String description;

    @NotNull(message = "Phải chọn loại khóa học")
    private Long categoryId;
}
