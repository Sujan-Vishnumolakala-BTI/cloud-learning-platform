import json
import os
import threading

from kafka import KafkaConsumer


KAFKA_BOOTSTRAP_SERVERS = os.getenv(
    "KAFKA_BOOTSTRAP_SERVERS",
    "kafka:9092"
)

TOPIC = "course.events"

GROUP_ID = "recommendation-service"


# In-memory course catalogue.
# Kafka events keep this catalogue synchronized.
course_catalog = {}

catalog_lock = threading.Lock()


def consume_course_events():

    print(
        "========== RECOMMENDATION KAFKA CONSUMER STARTING ==========",
        flush=True
    )

    consumer = KafkaConsumer(
        TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id=GROUP_ID,
        auto_offset_reset="earliest",
        enable_auto_commit=True,
        value_deserializer=lambda value:
            json.loads(value.decode("utf-8"))
    )

    print(
        "========== RECOMMENDATION KAFKA CONSUMER CONNECTED ==========",
        flush=True
    )

    for message in consumer:

        try:

            event = message.value

            print(
                "========== COURSE EVENT RECEIVED ==========",
                flush=True
            )

            print(
                event,
                flush=True
            )

            course_id = str(
                event["courseId"]
            )

            event_type = event.get(
                "eventType"
            )

            with catalog_lock:

                if event_type == "COURSE_DELETED":

                    course_catalog.pop(
                        course_id,
                        None
                    )

                    print(
                        f"COURSE REMOVED: {course_id}",
                        flush=True
                    )

                elif event.get("published") and event.get("active"):

                    course_catalog[course_id] = event

                    print(
                        f"COURSE CATALOG UPDATED: "
                        f"courseId={course_id}",
                        flush=True
                    )

                else:

                    course_catalog.pop(
                        course_id,
                        None
                    )

                    print(
                        f"COURSE NOT AVAILABLE: "
                        f"courseId={course_id}",
                        flush=True
                    )

            print(
                "============================================",
                flush=True
            )

        except Exception as e:

            print(
                "KAFKA COURSE EVENT PROCESSING ERROR:",
                e,
                flush=True
            )


def start_kafka_consumer():

    thread = threading.Thread(
        target=consume_course_events,
        daemon=True
    )

    thread.start()

    return thread