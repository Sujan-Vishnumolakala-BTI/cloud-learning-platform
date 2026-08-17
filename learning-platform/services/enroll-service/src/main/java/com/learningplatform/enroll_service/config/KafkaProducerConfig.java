// package com.learningplatform.enroll_service.config;

// import com.learningplatform.enroll_service.event.CourseEnrolledEvent;
// import org.apache.kafka.clients.producer.ProducerConfig;
// import org.apache.kafka.common.serialization.StringSerializer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.core.DefaultKafkaProducerFactory;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.core.ProducerFactory;
// import org.springframework.kafka.support.serializer.JsonSerializer;

// import java.util.HashMap;
// import java.util.Map;

// @Configuration
// public class KafkaProducerConfig {

//     @Bean
//     public ProducerFactory<String, CourseEnrolledEvent> producerFactory() {

//         Map<String, Object> config = new HashMap<>();

//         config.put(
//             ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//             "kafka:9092"
//         );

//         config.put(
//             ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//             StringSerializer.class
//         );

//         config.put(
//             ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//             JsonSerializer.class
//         );

//         return new DefaultKafkaProducerFactory<>(config);
//     }

//     @Bean
//     public KafkaTemplate<String, CourseEnrolledEvent> kafkaTemplate() {
//         return new KafkaTemplate<>(producerFactory());
//     }
// }

package com.learningplatform.enroll_service.config;

import com.learningplatform.enroll_service.event.CourseEnrolledEvent;
import com.learningplatform.enroll_service.event.CourseCompletedEvent;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    // =====================================================
    // COURSE ENROLLED
    // =====================================================

    @Bean
    public ProducerFactory<String, CourseEnrolledEvent>
    producerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka:9092"
        );

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(
                config
        );
    }

    @Bean
    public KafkaTemplate<String, CourseEnrolledEvent>
    kafkaTemplate() {

        return new KafkaTemplate<>(
                producerFactory()
        );
    }


    // =====================================================
    // COURSE COMPLETED
    // =====================================================

    @Bean
    public ProducerFactory<String, CourseCompletedEvent>
    courseCompletedProducerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka:9092"
        );

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(
                config
        );
    }

    @Bean
    public KafkaTemplate<String, CourseCompletedEvent>
    courseCompletedKafkaTemplate() {

        return new KafkaTemplate<>(
                courseCompletedProducerFactory()
        );
    }
}