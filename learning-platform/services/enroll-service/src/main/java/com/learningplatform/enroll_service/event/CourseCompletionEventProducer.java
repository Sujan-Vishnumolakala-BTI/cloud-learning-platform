package com.learningplatform.enroll_service.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CourseCompletionEventProducer {

    private static final String TOPIC =
            "course.completed.events";

    private final KafkaTemplate<String, CourseCompletedEvent>
            kafkaTemplate;

    public CourseCompletionEventProducer(
            KafkaTemplate<String, CourseCompletedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCourseCompleted(
            CourseCompletedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                String.valueOf(event.getUserId()),
                event
        );

        System.out.println(
                "========== KAFKA COURSE COMPLETED =========="
        );

        System.out.println(
                "TOPIC: " + TOPIC
        );

        System.out.println(
                "EVENT: COURSE_COMPLETED"
        );

        System.out.println(
                "USER ID: " + event.getUserId()
        );

        System.out.println(
                "COURSE ID: " + event.getCourseId()
        );

        System.out.println(
                "COMPLETED AT: " + event.getCompletedAt()
        );

        System.out.println(
                "============================================="
        );
    }
}