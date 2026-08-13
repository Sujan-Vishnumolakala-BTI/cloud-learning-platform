// package com.learningplatform.course_service.client;

// import jakarta.servlet.http.HttpServletRequest;

// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestClient;

// import java.util.Arrays;
// import java.util.List;

// @Component
// public class EnrollmentServiceClient {

//     private final RestClient restClient;
//     private final HttpServletRequest request;

//     public EnrollmentServiceClient(
//             RestClient restClient,
//             HttpServletRequest request) {

//         this.restClient = restClient;
//         this.request = request;
//     }

//     public List<EnrollmentStudentResponse>
//     getStudentsByCourse(Long courseId) {

//         EnrollmentStudentResponse[] response =
//                 restClient
//                         .get()
//                         .uri(
//                                 "/api/internal/courses/{courseId}/students",
//                                 courseId)
//                         .header(
//                                 "Authorization",
//                                 getAuthorization())
//                         .retrieve()
//                         .body(
//                                 EnrollmentStudentResponse[].class);

//         if (response == null) {
//             return List.of();
//         }

//         return Arrays.asList(response);
//     }

//     private String getAuthorization() {

//         String authorization =
//                 request.getHeader(
//                         "Authorization");

//         if (authorization == null ||
//                 !authorization.startsWith("Bearer ")) {

//             throw new RuntimeException(
//                     "Authorization header missing");
//         }

//         return authorization;
//     }
// }

package com.learningplatform.course_service.client;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.learningplatform.course_service.dto.QuizAttemptResponse;

import java.util.List;

@Component
public class EnrollmentServiceClient {

    private final RestClient restClient;
    private final HttpServletRequest request;
    private final String enrollmentServiceUrl;

    public EnrollmentServiceClient(
            RestClient restClient,
            HttpServletRequest request,
            @Value("${enrollment.service.url}") String enrollmentServiceUrl) {

        this.restClient = restClient;
        this.request = request;
        this.enrollmentServiceUrl = enrollmentServiceUrl;
    }

    public List<EnrollmentStudentResponse> getStudentsByCourse(Long courseId) {

        String url = enrollmentServiceUrl
                + "/api/internal/courses/"
                + courseId
                + "/students";

        System.out.println(
                "ENROLLMENT SERVICE URL: " + url);

        EnrollmentStudentResponse[] response = restClient
                .get()
                .uri(url)
                .header(
                        "Authorization",
                        getAuthorization())
                .retrieve()
                .body(
                        EnrollmentStudentResponse[].class);

        if (response == null) {
            return List.of();
        }

        return List.of(response);
    }

    public List<CourseStudentProgressResponse> getCourseProgress(Long courseId) {

        String url = enrollmentServiceUrl
                + "/api/internal/courses/"
                + courseId
                + "/progress";

        System.out.println(
                "ENROLLMENT SERVICE PROGRESS URL: "
                        + url);

        CourseStudentProgressResponse[] response = restClient
                .get()
                .uri(url)
                .header(
                        "Authorization",
                        getAuthorization())
                .retrieve()
                .body(
                        CourseStudentProgressResponse[].class);

        if (response == null) {
            return List.of();
        }

        return List.of(response);
    }

    public List<QuizAttemptResponse> getQuizResults(Long quizId) {

        String url = enrollmentServiceUrl
                + "/api/internal/quizzes/"
                + quizId
                + "/results";

        QuizAttemptResponse[] response = restClient
                .get()
                .uri(url)
                .header(
                        "Authorization",
                        getAuthorization())
                .retrieve()
                .body(
                        QuizAttemptResponse[].class);

        if (response == null) {
            return List.of();
        }

        return List.of(response);
    }

    private String getAuthorization() {

        String authorization = request.getHeader(
                "Authorization");

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            throw new RuntimeException(
                    "Authorization header missing");
        }

        return authorization;
    }
}