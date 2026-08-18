import json
import os
import threading
from datetime import datetime

from kafka import KafkaConsumer
from sqlalchemy.exc import IntegrityError

from .database import SessionLocal
from .models import CoursePopularity, CourseCompletion


KAFKA_BOOTSTRAP_SERVERS = os.getenv(
    "KAFKA_BOOTSTRAP_SERVERS",
    "kafka:9092"
)

ENROLLMENT_TOPIC = "enrollment.events"
COMPLETION_TOPIC = "course.completed.events"


def parse_completed_at(value):

    if isinstance(value, list):

        return datetime(
            value[0],
            value[1],
            value[2],
            value[3] if len(value) > 3 else 0,
            value[4] if len(value) > 4 else 0,
            value[5] if len(value) > 5 else 0,
            int(value[6] / 1000)
            if len(value) > 6
            else 0
        )

    if isinstance(value, str):

        return datetime.fromisoformat(
            value.replace("Z", "")
        )

    raise ValueError(
        f"Unsupported completedAt format: {value}"
    )


def consume_events():

    print(
        "========================================",
        flush=True
    )

    print(
        "REPORTING KAFKA CONSUMER STARTING",
        flush=True
    )

    print(
        "BOOTSTRAP SERVERS:",
        KAFKA_BOOTSTRAP_SERVERS,
        flush=True
    )

    print(
        "TOPICS:",
        ENROLLMENT_TOPIC,
        COMPLETION_TOPIC,
        flush=True
    )

    print(
        "========================================",
        flush=True
    )

    consumer = KafkaConsumer(
        ENROLLMENT_TOPIC,
        COMPLETION_TOPIC,

        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,

        group_id="reporting-service",

        auto_offset_reset="earliest",

        enable_auto_commit=True,

        value_deserializer=lambda value:
            json.loads(value.decode("utf-8"))
    )

    print(
        "REPORTING KAFKA CONSUMER CONNECTED",
        flush=True
    )

    for message in consumer:

        try:

            event = message.value

            print(
                "========== KAFKA EVENT RECEIVED ==========",
                flush=True
            )

            print(
                "TOPIC:",
                message.topic,
                flush=True
            )

            print(
                "EVENT:",
                event,
                flush=True
            )

            print(
                "==========================================",
                flush=True
            )

            if message.topic == ENROLLMENT_TOPIC:

                process_enrollment_event(event)

            elif message.topic == COMPLETION_TOPIC:

                process_completion_event(event)

        except Exception as error:

            print(
                "KAFKA EVENT PROCESSING ERROR:",
                error,
                flush=True
            )


def process_enrollment_event(event):

    course_id = event.get("courseId")

    if course_id is None:

        print(
            "ENROLLMENT EVENT ERROR: courseId missing",
            flush=True
        )

        return

    db = SessionLocal()

    try:

        report = db.get(
            CoursePopularity,
            course_id
        )

        if report is None:

            report = CoursePopularity(
                course_id=course_id,
                enrollment_count=1
            )

            db.add(report)

        else:

            report.enrollment_count += 1

        db.commit()

        print(
            f"COURSE POPULARITY UPDATED: "
            f"courseId={course_id}, "
            f"enrollments={report.enrollment_count}",
            flush=True
        )

    except Exception as error:

        db.rollback()

        print(
            "ENROLLMENT DATABASE ERROR:",
            error,
            flush=True
        )

    finally:

        db.close()


def process_completion_event(event):

    user_id = event.get("userId")
    course_id = event.get("courseId")
    completed_at = event.get("completedAt")

    if user_id is None:

        print(
            "COMPLETION EVENT ERROR: userId missing",
            flush=True
        )

        return

    if course_id is None:

        print(
            "COMPLETION EVENT ERROR: courseId missing",
            flush=True
        )

        return

    if completed_at is None:

        print(
            "COMPLETION EVENT ERROR: completedAt missing",
            flush=True
        )

        return

    completed_datetime = parse_completed_at(
        completed_at
    )

    db = SessionLocal()

    try:

        existing = (
            db.query(CourseCompletion)
            .filter(
                CourseCompletion.user_id == user_id,
                CourseCompletion.course_id == course_id
            )
            .first()
        )

        if existing is not None:

            print(
                f"COMPLETION ALREADY EXISTS: "
                f"userId={user_id}, "
                f"courseId={course_id}",
                flush=True
            )

            return

        completion = CourseCompletion(
            user_id=user_id,
            course_id=course_id,
            completed_at=completed_datetime
        )

        db.add(completion)

        db.commit()

        print(
            "========== COURSE COMPLETION SAVED ==========",
            flush=True
        )

        print(
            f"USER ID: {user_id}",
            flush=True
        )

        print(
            f"COURSE ID: {course_id}",
            flush=True
        )

        print(
            f"COMPLETED AT: {completed_datetime}",
            flush=True
        )

        print(
            "==============================================",
            flush=True
        )

    except IntegrityError:

        db.rollback()

        print(
            f"COMPLETION ALREADY EXISTS "
            f"(duplicate Kafka event): "
            f"userId={user_id}, "
            f"courseId={course_id}",
            flush=True
        )

    except Exception as error:

        db.rollback()

        print(
            "COMPLETION DATABASE ERROR:",
            error,
            flush=True
        )

    finally:

        db.close()


def start_kafka_consumer():

    thread = threading.Thread(
        target=consume_events,
        daemon=True
    )

    thread.start()

    return thread