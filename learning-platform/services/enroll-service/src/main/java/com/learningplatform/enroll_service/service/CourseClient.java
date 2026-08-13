// package com.learningplatform.enroll_service.service;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.MediaType;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestClient;

// @Component
// public class CourseClient {

//     private final RestClient restClient;

//     public CourseClient(
//             @Value("${course-service.url}") String courseServiceUrl) {

//         this.restClient = RestClient.builder()
//                 .baseUrl(courseServiceUrl)
//                 .build();
//     }

//     public QuizData getQuiz(Long quizId) {

//         return restClient
//                 .get()
//                 .uri("/api/quizzes/{id}", quizId)
//                 .retrieve()
//                 .body(QuizData.class);
//     }

//     public QuestionData[] getQuestions(
//             Long quizId) {

//         return restClient
//                 .get()
//                 .uri(
//                         "/api/quizzes/{id}/questions",
//                         quizId)
//                 .retrieve()
//                 .body(QuestionData[].class);
//     }

//     public OptionData[] getOptions(
//             Long questionId) {

//         return restClient
//                 .get()
//                 .uri(
//                         "/api/questions/{id}/options",
//                         questionId)
//                 .retrieve()
//                 .body(OptionData[].class);
//     }

//     public record QuizData(
//             Long id,
//             Long lessonId,
//             String title,
//             String description,
//             Integer passingScore) {
//     }

//     public record QuestionData(
//             Long id,
//             Long quizId,
//             String questionText,
//             Integer orderIndex) {
//     }

//     public record OptionData(
//             Long id,
//             Long questionId,
//             String optionText) {
//     }

//     public record QuizAnswerData(
//             Long quizId,
//             Integer passingScore,
//             java.util.List<QuestionAnswerData> questions) {
//     }

//     public record QuestionAnswerData(
//             Long questionId,
//             Long correctOptionId) {
//     }

//     public QuizAnswerData getQuizAnswers(
//             Long quizId) {

//         return restClient
//                 .get()
//                 .uri(
//                         "/api/internal/quizzes/{id}/answers",
//                         quizId)
//                 .retrieve()
//                 .body(QuizAnswerData.class);
//     }

//     public Long getCourseIdForQuiz(
//             Long quizId) {

//         return restClient
//                 .get()
//                 .uri(
//                         "/api/quizzes/{id}/course",
//                         quizId)
//                 .retrieve()
//                 .body(Long.class);
//     }
// }

package com.learningplatform.enroll_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CourseClient {

        private final RestClient restClient;
        private final HttpServletRequest request;

        public CourseClient(
                        @Value("${course-service.url}") String courseServiceUrl,
                        HttpServletRequest request) {

                this.restClient = RestClient.builder()
                                .baseUrl(courseServiceUrl)
                                .build();

                this.request = request;
        }

        private RestClient.RequestHeadersSpec<?> addAuthorization(
                        RestClient.RequestHeadersSpec<?> requestSpec) {

                String authorization = request.getHeader(
                                HttpHeaders.AUTHORIZATION);

                if (authorization != null) {

                        requestSpec.header(
                                        HttpHeaders.AUTHORIZATION,
                                        authorization);
                }

                return requestSpec;
        }

        public QuizData getQuiz(Long quizId) {

                RestClient.RequestHeadersSpec<?> requestSpec = restClient
                                .get()
                                .uri(
                                                "/api/quizzes/{id}",
                                                quizId);

                return addAuthorization(requestSpec)
                                .retrieve()
                                .body(QuizData.class);
        }

        public QuizAnswerData getQuizAnswers(
                        Long quizId) {

                RestClient.RequestHeadersSpec<?> requestSpec = restClient
                                .get()
                                .uri(
                                                "/api/internal/quizzes/{id}/answers",
                                                quizId);

                return addAuthorization(requestSpec)
                                .retrieve()
                                .body(QuizAnswerData.class);
        }

        public Long getCourseIdForQuiz(
                        Long quizId) {

                RestClient.RequestHeadersSpec<?> requestSpec = restClient
                                .get()
                                .uri(
                                                "/api/quizzes/{id}/course",
                                                quizId);

                return addAuthorization(requestSpec)
                                .retrieve()
                                .body(Long.class);
        }

        public record QuizData(
                        Long id,
                        Long lessonId,
                        String title,
                        String description,
                        Integer passingScore) {
        }

        public record QuizAnswerData(
                        Long quizId,
                        Integer passingScore,
                        java.util.List<QuestionAnswerData> questions) {
        }

        public record QuestionAnswerData(
                        Long questionId,
                        Long correctOptionId) {
        }

        public Long getCourseIdForLesson(
                        Long lessonId) {

                RestClient.RequestHeadersSpec<?> requestSpec = restClient
                                .get()
                                .uri(
                                                "/api/lessons/{id}/course",
                                                lessonId);

                return addAuthorization(requestSpec)
                                .retrieve()
                                .body(Long.class);
        }

        public Long getLessonCount(
                        Long courseId) {

                RestClient.RequestHeadersSpec<?> requestSpec = restClient
                                .get()
                                .uri(
                                                "/api/courses/{id}/lesson-count",
                                                courseId);

                return addAuthorization(requestSpec)
                                .retrieve()
                                .body(Long.class);
        }

        public CourseData getCourse(Long courseId) {

                RestClient.RequestHeadersSpec<?> requestSpec = restClient
                                .get()
                                .uri(
                                                "/api/courses/{id}",
                                                courseId);

                return addAuthorization(requestSpec)
                                .retrieve()
                                .body(CourseData.class);
        }

        public record CourseData(
                        Long id,
                        String title) {
        }
}