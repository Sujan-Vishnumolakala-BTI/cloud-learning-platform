import json
import os
import threading

from kafka import KafkaConsumer

from .database import SessionLocal
from .models import CoursePopularity


KAFKA_BOOTSTRAP_SERVERS = os.getenv(
    "KAFKA_BOOTSTRAP_SERVERS",
    "kafka:9092"
)

TOPIC = "enrollment.events"


def consume_enrollment_events():

    print("========================================", flush=True)
    print("REPORTING KAFKA CONSUMER STARTING", flush=True)
    print(
        "BOOTSTRAP SERVERS:",
        KAFKA_BOOTSTRAP_SERVERS,
        flush=True
    )
    print("TOPIC:", TOPIC, flush=True)
    print("========================================", flush=True)

    consumer = KafkaConsumer(
        TOPIC,
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

            print(event, flush=True)

            print(
                "==========================================",
                flush=True
            )

            course_id = event.get("courseId")

            if course_id is None:
                print(
                    "EVENT ERROR: courseId missing",
                    flush=True
                )
                continue

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
                    "DATABASE ERROR:",
                    error,
                    flush=True
                )

            finally:
                db.close()

        except Exception as error:

            print(
                "KAFKA EVENT PROCESSING ERROR:",
                error,
                flush=True
            )
def start_kafka_consumer():

    thread = threading.Thread(
        target=consume_enrollment_events,
        daemon=True
    )

    thread.start()