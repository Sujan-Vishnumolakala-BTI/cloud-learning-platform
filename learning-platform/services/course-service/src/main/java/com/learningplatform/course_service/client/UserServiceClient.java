// // // package com.learningplatform.course_service.client;

// // // import com.learningplatform.course_service.dto.UserResponse;
// // // import com.learningplatform.course_service.exception.UserServiceException;

// // // import org.springframework.http.HttpHeaders;
// // // import org.springframework.http.MediaType;
// // // import org.springframework.stereotype.Component;
// // // import org.springframework.web.client.RestClient;

// // // @Component
// // // public class UserServiceClient {

// // //     private final RestClient restClient = null;
// // //     private final UserServiceClient userServiceClient = new UserServiceClient();

    

// // //     public UserResponse getUserById(Long userId, String token) {

// // //         try {

// // //             return restClient
// // //                     .get()
// // //                     .uri("/api/users/{id}", userId)
// // //                     .header(
// // //                             HttpHeaders.AUTHORIZATION,
// // //                             token)
// // //                     .accept(MediaType.APPLICATION_JSON)
// // //                     .retrieve()
// // //                     .body(UserResponse.class);

// // //         } catch (Exception e) {

// // //             throw new UserServiceException(
// // //                     "Unable to verify user with User Service",
// // //                     e);
// // //         }
// // //     }
// // // }

// // package com.learningplatform.course_service.client;

// // import com.learningplatform.course_service.dto.UserResponse;

// // import org.springframework.beans.factory.annotation.Value;
// // import org.springframework.stereotype.Component;
// // import org.springframework.web.client.RestClient;

// // @Component
// // public class UserServiceClient {

// //     private final RestClient restClient;

// //     public UserServiceClient(
// //             RestClient.Builder builder,
// //             @Value("${user-service.url}") String userServiceUrl) {

// //         this.restClient = builder
// //                 .baseUrl(userServiceUrl)
// //                 .build();
// //     }

// //     public UserResponse getUserById(Long userId) {

// //         return restClient
// //                 .get()
// //                 .uri("/api/users/{id}", userId)
// //                 .retrieve()
// //                 .body(UserResponse.class);
// //     }
// // }

// package com.learningplatform.course_service.client;

// import com.learningplatform.course_service.dto.UserResponse;

// import jakarta.servlet.http.HttpServletRequest;

// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestClient;
// import org.springframework.web.context.request.RequestContextHolder;
// import org.springframework.web.context.request.ServletRequestAttributes;

// @Component
// public class UserServiceClient {

//     private final RestClient restClient;

//     public UserServiceClient(RestClient.Builder builder) {

//         this.restClient = builder
//                 .baseUrl("http://127.0.0.1:8081")
//                 .build();
//     }

//     public UserResponse getUserById(Long userId) {

//         ServletRequestAttributes attributes =
//                 (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

//         if (attributes == null) {
//             throw new IllegalStateException(
//                     "No current HTTP request available");
//         }

//         HttpServletRequest request = attributes.getRequest();

//         String authorization =
//                 request.getHeader("Authorization");

//         if (authorization == null || authorization.isBlank()) {
//             throw new IllegalStateException(
//                     "Authorization header is missing");
//         }

//         return restClient.get()
//                 .uri("/api/users/{id}", userId)
//                 .header("Authorization", authorization)
//                 .retrieve()
//                 .body(UserResponse.class);
//     }
// }

package com.learningplatform.course_service.client;

import com.learningplatform.course_service.dto.UserResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(
            RestClient.Builder builder,
            @Value("${user-service.url}") String userServiceUrl) {

        this.restClient = builder
                .baseUrl(userServiceUrl)
                .build();
    }

    public UserResponse getUserById(Long userId) {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new IllegalStateException(
                    "No current HTTP request available");
        }

        HttpServletRequest request = attributes.getRequest();

        String authorization =
                request.getHeader("Authorization");

        if (authorization == null || authorization.isBlank()) {
            throw new IllegalStateException(
                    "Authorization header is missing");
        }

        return restClient
                .get()
                .uri("/api/users/{id}", userId)
                .header("Authorization", authorization)
                .retrieve()
                .body(UserResponse.class);
    }
}