// // package com.learningplatform.course_service.repository;

// // import com.learningplatform.course_service.entity.Course;

// // import org.springframework.data.jpa.repository.JpaRepository;

// // import java.util.List;

// // public interface CourseRepository
// //         extends JpaRepository<Course, Long> {

// //     List<Course> findByPublishedTrueAndActiveTrue();

// //     List<Course> findByCategoryIgnoreCaseAndPublishedTrueAndActiveTrue(
// //             String category);

// //     List<Course> findByTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
// //             String title);

// //     List<Course> findByCategoryIgnoreCaseAndTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
// //             String category,
// //             String title);
// // }

// package com.learningplatform.course_service.repository;

// import com.learningplatform.course_service.entity.Course;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.JpaRepository;

// public interface CourseRepository
//         extends JpaRepository<Course, Long> {

//     Page<Course> findByPublishedTrueAndActiveTrue(
//             Pageable pageable
//     );

//     Page<Course> findByCategoryIgnoreCaseAndPublishedTrueAndActiveTrue(
//             String category,
//             Pageable pageable
//     );

//     Page<Course> findByTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
//             String title,
//             Pageable pageable
//     );

//     Page<Course> findByCategoryIgnoreCaseAndTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
//             String category,
//             String title,
//             Pageable pageable
//     );
// }

package com.learningplatform.course_service.repository;

import com.learningplatform.course_service.entity.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository
        extends JpaRepository<Course, Long> {

    // =========================
    // Existing search methods
    // =========================

    List<Course> findByPublishedTrueAndActiveTrue();

    List<Course> findByCategoryIgnoreCaseAndPublishedTrueAndActiveTrue(
            String category
    );

    List<Course> findByTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
            String title
    );

    List<Course> findByCategoryIgnoreCaseAndTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
            String category,
            String title
    );
    List<Course> findByInstructorId(Long instructorId);
    

    // =========================
    // Pagination methods
    // =========================

    Page<Course> findByPublishedTrueAndActiveTrue(
            Pageable pageable
    );

    Page<Course> findByCategoryIgnoreCaseAndPublishedTrueAndActiveTrue(
            String category,
            Pageable pageable
    );

    Page<Course> findByTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
            String title,
            Pageable pageable
    );

    Page<Course> findByCategoryIgnoreCaseAndTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
            String category,
            String title,
            Pageable pageable
    );
}