// // package com.learningplatform.course_service.repository;

// // import com.learningplatform.course_service.dto.LessonResponse;
// // import com.learningplatform.course_service.entity.Lesson;

// // import org.springframework.data.jpa.repository.JpaRepository;

// // import java.util.List;

// // public interface LessonRepository
// //                 extends JpaRepository<Lesson, Long> {

// //         List<Lesson> findByModuleIdOrderByOrderIndexAsc(
// //                         Long moduleId);

// //         boolean existsByModuleIdAndOrderIndex(
// //                         Long moduleId,
// //                         Integer orderIndex);

// //         boolean existsByModuleIdAndOrderIndexAndIdNot(
// //                         Long moduleId,
// //                         Integer orderIndex,
// //                         Long id);

// //         List<Lesson> findByModuleIdInOrderByModuleIdAscOrderIndexAsc(
// //                         List<Long> moduleIds);

// //         List<LessonResponse> findByModuleId(Long id);
// // }

// package com.learningplatform.course_service.repository;

// import com.learningplatform.course_service.entity.Lesson;

// import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.List;

// public interface LessonRepository
//         extends JpaRepository<Lesson, Long> {

//     List<Lesson> findByModuleIdOrderByOrderIndexAsc(
//             Long moduleId);

//     long countByModuleId(
//             Long moduleId);

//     boolean existsByModuleIdAndOrderIndex(
//             Long moduleId,
//             Integer orderIndex);

//     boolean existsByModuleIdAndOrderIndexAndIdNot(
//             Long moduleId,
//             Integer orderIndex,
//             Long id);
// }

package com.learningplatform.course_service.repository;

import com.learningplatform.course_service.entity.Lesson;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository
                extends JpaRepository<Lesson, Long> {

        List<Lesson> findByModuleIdOrderByOrderIndexAsc(
                        Long moduleId);

        /*
         * Used by existing LessonService
         */
        List<Lesson> findByModuleIdInOrderByModuleIdAscOrderIndexAsc(
                        List<Long> moduleIds);

        List<Lesson> findByModuleIdIn(
                        List<Long> moduleIds);

        /*
         * Used for course lesson count
         */
        long countByModuleId(
                        Long moduleId);

        boolean existsByModuleIdAndOrderIndex(
                        Long moduleId,
                        Integer orderIndex);

        boolean existsByModuleIdAndOrderIndexAndIdNot(
                        Long moduleId,
                        Integer orderIndex,
                        Long id);
}