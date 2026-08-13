// // package com.learningplatform.course_service.config;

// // import org.springframework.beans.factory.annotation.Value;
// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
// // import org.springframework.web.client.RestClient;

// // @Configuration
// // public class RestClientConfig {

// //     @Bean
// //     public RestClient restClient(
// //             @Value("${user-service.url}") String userServiceUrl) {

// //         return RestClient.builder()
// //                 .baseUrl(userServiceUrl)
// //                 .build();
// //     }
// // }

// // package com.learningplatform.course_service.config;

// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
// // import org.springframework.web.client.RestClient;

// // @Configuration
// // public class RestClientConfig {

// //     @Bean
// //     public RestClient.Builder restClientBuilder() {
// //         return RestClient.builder();
// //     }
// // }

// package com.learningplatform.course_service.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.client.RestClient;

// @Configuration
// public class RestClientConfig {

//     @Bean
//     public RestClient restClient() {
//         return RestClient.builder()
//                 .build();
//     }
// }

package com.learningplatform.course_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient restClient(
            RestClient.Builder builder) {

        return builder.build();
    }
}