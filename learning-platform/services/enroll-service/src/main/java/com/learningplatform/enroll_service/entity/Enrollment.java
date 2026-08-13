// package com.learningplatform.enroll_service.entity;

// import jakarta.persistence.*;

// import java.time.LocalDateTime;

// @Entity
// @Table(
//     name = "enrollments",
//     uniqueConstraints = {
//         @UniqueConstraint(
//             name = "uk_user_course",
//             columnNames = {"user_id", "course_id"}
//         )
//     }
// )
// public class Enrollment {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "user_id", nullable = false)
//     private Long userId;

//     @Column(name = "course_id", nullable = false)
//     private Long courseId;

//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false)
//     private EnrollmentStatus status;

//     @Column(nullable = false)
//     private LocalDateTime enrolledAt;

//     private LocalDateTime completedAt;

//     @Column(nullable = false)
//     private LocalDateTime updatedAt;
    

//     @PrePersist
//     protected void onCreate() {

//         LocalDateTime now = LocalDateTime.now();

//         enrolledAt = now;
//         updatedAt = now;

//         if (status == null) {
//             status = EnrollmentStatus.ACTIVE;
//         }
//     }

//     @PreUpdate
//     protected void onUpdate() {
//         updatedAt = LocalDateTime.now();
//     }

//     public Long getId() {
//         return id;
//     }

//     public Long getUserId() {
//         return userId;
//     }

//     public void setUserId(Long userId) {
//         this.userId = userId;
//     }

//     public Long getCourseId() {
//         return courseId;
//     }

//     public void setCourseId(Long courseId) {
//         this.courseId = courseId;
//     }

//     public EnrollmentStatus getStatus() {
//         return status;
//     }

//     public void setStatus(EnrollmentStatus status) {
//         this.status = status;
//     }

//     public LocalDateTime getEnrolledAt() {
//         return enrolledAt;
//     }

//     public LocalDateTime getCompletedAt() {
//         return completedAt;
//     }

//     public LocalDateTime getUpdatedAt() {
//         return updatedAt;
//     }

    

//     public void setEnrolledAt(LocalDateTime now) {

//         this.enrolledAt = enrolledAt;
// 
//         // throw new UnsupportedOperationException("Unimplemented method 'setEnrolledAt'");
//     }

//     public void setCompletedAt(Object object) {

//          this.completedAt = completedAt;
//    
//         // throw new UnsupportedOperationException("Unimplemented method 'setCompletedAt'");
//     }
// }

package com.learningplatform.enroll_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_course",
                        columnNames = {
                                "user_id",
                                "course_id"
                        }
                )
        }
)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    @Column(nullable = false)
    private LocalDateTime enrolledAt;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        enrolledAt = now;
        updatedAt = now;

        if (status == null) {
            status = EnrollmentStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}