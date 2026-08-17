package com.learningplatform.enroll_service.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentEventProducer {

    private static final String TOPIC = "enrollment.events";

    private final KafkaTemplate<String, CourseEnrolledEvent> kafkaTemplate;

    public EnrollmentEventProducer(
            KafkaTemplate<String, CourseEnrolledEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCourseEnrolled(
            CourseEnrolledEvent event) {
        kafkaTemplate.send(
                TOPIC,
                String.valueOf(event.getUserId()),
                event).whenComplete((result, ex) -> {

                    if (ex != null) {
                        System.err.println("========== KAFKA SEND FAILED ==========");
                        ex.printStackTrace();
                    } else {
                        System.out.println("========== KAFKA SEND SUCCESS ==========");
                        System.out.println("TOPIC: "
                                + result.getRecordMetadata().topic());
                        System.out.println("PARTITION: "
                                + result.getRecordMetadata().partition());
                        System.out.println("OFFSET: "
                                + result.getRecordMetadata().offset());
                        System.out.println("========================================");
                    }
                });
    }
}