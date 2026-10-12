# Mini Project: Quản lý khóa học (Spring Boot)

## Chạy bằng VS Code
1. Cài JDK 17+ và extension "Extension Pack for Java" + "Spring Boot Extension Pack".
2. File > Open Folder... > chọn thư mục `coursemanagement`.
3. Mở `CoursemanagementApplication.java` > bấm **Run** (hoặc terminal: `./mvnw spring-boot:run`, Windows: `.\mvnw.cmd spring-boot:run`).
4. Mở http://localhost:8080/courses
5. Xem database: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:file:./data/coursedb` / User: `sa` / Password: để trống

## Đổi sang MySQL (nếu cần)
- `pom.xml`: bỏ comment dependency `mysql-connector-j`.
- `application.properties`: comment khối H2, bỏ comment khối MySQL.

## Reset dữ liệu
Dừng app, xóa thư mục `data/`, chạy lại (dữ liệu mẫu tự tạo lại).
