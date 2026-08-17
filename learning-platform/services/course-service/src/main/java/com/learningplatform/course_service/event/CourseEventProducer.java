package com.learningplatform.course_service.event;

import com.learningplatform.course_service.entity.Course;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CourseEventProducer {

    private static final String TOPIC = "course.events";

    private final KafkaTemplate<String, CourseEvent> kafkaTemplate;

    public CourseEventProducer(
            KafkaTemplate<String, CourseEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(
            Course course,
            String eventType) {

        CourseEvent event =
                new CourseEvent(
                        course.getId(),
                        eventType,
                        course.getTitle(),
                        course.getDescription(),
                        course.getCategory(),
                        course.getSkills(),
                        course.getInstructorId(),
                        course.isPublished(),
                        course.isActive()
                );

        kafkaTemplate
                .send(
                        TOPIC,
                        String.valueOf(course.getId()),
                        event
                )
                .whenComplete((result, exception) -> {

                    if (exception != null) {

                        System.err.println(
                                "========== COURSE KAFKA SEND FAILED =========="
                        );

                        exception.printStackTrace();

                    } else {

                        System.out.println(
                                "========== COURSE KAFKA SEND SUCCESS =========="
                        );

                        System.out.println(
                                "TOPIC: "
                                        + result.getRecordMetadata()
                                                .topic()
                        );

                        System.out.println(
                                "PARTITION: "
                                        + result.getRecordMetadata()
                                                .partition()
                        );

                        System.out.println(
                                "OFFSET: "
                                        + result.getRecordMetadata()
                                                .offset()
                        );

                        System.out.println(
                                "=============================================="
                        );
                    }
                });
    }
}