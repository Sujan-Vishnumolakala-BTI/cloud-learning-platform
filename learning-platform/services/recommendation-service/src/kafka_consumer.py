# import json
# import os
# import threading
# from datetime import datetime

# from kafka import KafkaConsumer

# from .database import SessionLocal
# from .models import RecommendationCourse


# KAFKA_BOOTSTRAP_SERVERS = os.getenv(
#     "KAFKA_BOOTSTRAP_SERVERS",
#     "kafka:9092"
# )

# TOPIC = "course.events"

# GROUP_ID = "recommendation-service"


# # =========================================================
# # IN-MEMORY CACHE
# # =========================================================

# course_catalog = {}

# catalog_lock = threading.Lock()


# # =========================================================
# # NORMALIZE COURSE EVENT
# # =========================================================

# def normalize_course_event(event):

#     raw_skills = event.get(
#         "skills",
#         []
#     )

#     if isinstance(raw_skills, list):

#         skills = {
#             str(skill): 1.0
#             for skill in raw_skills
#         }

#     elif isinstance(raw_skills, dict):

#         skills = {
#             str(skill): float(value)
#             for skill, value in raw_skills.items()
#         }

#     else:

#         skills = {}

#     return {
#         "course_id": str(
#             event["courseId"]
#         ),

#         "course_title": event.get(
#             "title",
#             ""
#         ),

#         "difficulty": event.get(
#             "difficulty",
#             "Unknown"
#         ),

#         "skills": skills
#     }


# # =========================================================
# # SAVE COURSE TO DATABASE
# # =========================================================

# def save_course_to_database(event):

#     course_id = int(
#         event["courseId"]
#     )

#     raw_skills = event.get(
#         "skills",
#         []
#     )

#     if isinstance(raw_skills, list):

#         skills = {
#             str(skill): 1.0
#             for skill in raw_skills
#         }

#     elif isinstance(raw_skills, dict):

#         skills = {
#             str(skill): float(value)
#             for skill, value in raw_skills.items()
#         }

#     else:

#         skills = {}

#     db = SessionLocal()

#     try:

#         course = db.get(
#             RecommendationCourse,
#             course_id
#         )

#         if course is None:

#             course = RecommendationCourse(
#                 course_id=course_id
#             )

#             db.add(course)

#         course.course_title = event.get(
#             "title",
#             ""
#         )

#         course.description = event.get(
#             "description"
#         )

#         course.category = event.get(
#             "category"
#         )

#         course.difficulty = event.get(
#             "difficulty",
#             "Unknown"
#         )

#         course.skills = skills

#         course.instructor_id = event.get(
#             "instructorId"
#         )

#         course.published = bool(
#             event.get(
#                 "published",
#                 False
#             )
#         )

#         course.active = bool(
#             event.get(
#                 "active",
#                 True
#             )
#         )

#         course.updated_at = datetime.utcnow()

#         db.commit()

#         print(
#             f"RECOMMENDATION DB UPDATED: "
#             f"courseId={course_id}, "
#             f"published={course.published}, "
#             f"active={course.active}",
#             flush=True
#         )

#     except Exception as error:

#         db.rollback()

#         print(
#             "RECOMMENDATION DATABASE ERROR:",
#             error,
#             flush=True
#         )

#         raise

#     finally:

#         db.close()


# # =========================================================
# # DELETE COURSE FROM DATABASE
# # =========================================================

# def delete_course_from_database(course_id):

#     db = SessionLocal()

#     try:

#         course = db.get(
#             RecommendationCourse,
#             int(course_id)
#         )

#         if course is not None:

#             db.delete(course)

#             db.commit()

#             print(
#                 f"RECOMMENDATION DB COURSE DELETED: "
#                 f"courseId={course_id}",
#                 flush=True
#             )

#     except Exception as error:

#         db.rollback()

#         print(
#             "RECOMMENDATION DATABASE DELETE ERROR:",
#             error,
#             flush=True
#         )

#         raise

#     finally:

#         db.close()


# # =========================================================
# # LOAD AVAILABLE COURSES FROM DATABASE
# # =========================================================

# def load_course_catalog_from_database():

#     db = SessionLocal()

#     try:

#         courses = (
#             db.query(
#                 RecommendationCourse
#             )
#             .filter(
#                 RecommendationCourse.published.is_(True),
#                 RecommendationCourse.active.is_(True)
#             )
#             .all()
#         )

#         with catalog_lock:

#             course_catalog.clear()

#             for course in courses:

#                 course_catalog[
#                     str(course.course_id)
#                 ] = {

#                     "course_id":
#                         str(course.course_id),

#                     "course_title":
#                         course.course_title,

#                     "difficulty":
#                         course.difficulty,

#                     "skills":
#                         course.skills or {}
#                 }

#         print(
#             f"RECOMMENDATION CATALOG LOADED FROM DB: "
#             f"{len(course_catalog)} courses",
#             flush=True
#         )

#     finally:

#         db.close()


# # =========================================================
# # KAFKA CONSUMER
# # =========================================================

# def consume_course_events():

#     print(
#         "========== RECOMMENDATION KAFKA CONSUMER STARTING ==========",
#         flush=True
#     )

#     consumer = KafkaConsumer(
#         TOPIC,
#         bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
#         group_id=GROUP_ID,
#         auto_offset_reset="earliest",
#         enable_auto_commit=True,
#         value_deserializer=lambda value:
#             json.loads(
#                 value.decode("utf-8")
#             )
#     )

#     print(
#         "========== RECOMMENDATION KAFKA CONSUMER CONNECTED ==========",
#         flush=True
#     )

#     # ---------------------------------------------------------
#     # Load existing DB state before processing new events
#     # ---------------------------------------------------------

#     try:

#         load_course_catalog_from_database()

#     except Exception as error:

#         print(
#             "INITIAL DATABASE LOAD ERROR:",
#             error,
#             flush=True
#         )

#     # ---------------------------------------------------------
#     # Consume Kafka events
#     # ---------------------------------------------------------

#     for message in consumer:

#         try:

#             event = message.value

#             print(
#                 "========== COURSE EVENT RECEIVED ==========",
#                 flush=True
#             )

#             print(
#                 event,
#                 flush=True
#             )

#             course_id = str(
#                 event["courseId"]
#             )

#             event_type = event.get(
#                 "eventType"
#             )

#             # =================================================
#             # DELETE
#             # =================================================

#             if event_type == "COURSE_DELETED":

#                 delete_course_from_database(
#                     course_id
#                 )

#                 with catalog_lock:

#                     course_catalog.pop(
#                         course_id,
#                         None
#                     )

#                 print(
#                     f"COURSE REMOVED: {course_id}",
#                     flush=True
#                 )

#             # =================================================
#             # COURSE EVENT
#             # =================================================

#             else:

#                 save_course_to_database(
#                     event
#                 )

#                 # ---------------------------------------------
#                 # Available
#                 # ---------------------------------------------

#                 if (
#                     event.get("published") is True
#                     and event.get("active") is True
#                 ):

#                     course = normalize_course_event(
#                         event
#                     )

#                     with catalog_lock:

#                         course_catalog[
#                             course_id
#                         ] = course

#                     print(
#                         f"COURSE CATALOG UPDATED: "
#                         f"courseId={course_id}",
#                         flush=True
#                     )

#                     print(
#                         f"CATALOG SIZE: "
#                         f"{len(course_catalog)}",
#                         flush=True
#                     )

#                 # ---------------------------------------------
#                 # Not available
#                 # ---------------------------------------------

#                 else:

#                     with catalog_lock:

#                         course_catalog.pop(
#                             course_id,
#                             None
#                         )

#                     print(
#                         f"COURSE NOT AVAILABLE: "
#                         f"courseId={course_id}",
#                         flush=True
#                     )

#             print(
#                 "============================================",
#                 flush=True
#             )

#         except Exception as error:

#             print(
#                 "KAFKA COURSE EVENT PROCESSING ERROR:",
#                 error,
#                 flush=True
#             )


# # =========================================================
# # GET COURSE CATALOG
# # =========================================================

# def get_course_catalog():

#     with catalog_lock:

#         return list(
#             course_catalog.values()
#         )


# # =========================================================
# # START CONSUMER
# # =========================================================

# def start_kafka_consumer():

#     thread = threading.Thread(
#         target=consume_course_events,
#         daemon=True
#     )

#     thread.start()

#     return thread

import json
import os
import threading

from datetime import datetime

from kafka import KafkaConsumer

from src.database import SessionLocal

from src.models import (
    RecommendationCourse,
    StudentCourseActivity
)


KAFKA_BOOTSTRAP_SERVERS = os.getenv(
    "KAFKA_BOOTSTRAP_SERVERS",
    "kafka:9092"
)


# =========================================================
# COURSE EVENTS
# =========================================================

COURSE_TOPIC = "course.events"

COURSE_GROUP_ID = (
    "recommendation-course-service"
)


# =========================================================
# ENROLLMENT EVENTS
# =========================================================

ENROLLMENT_TOPIC = "enrollment.events"

ENROLLMENT_GROUP_ID = (
    "recommendation-enrollment-service"
)


# =========================================================
# COMPLETION EVENTS
# =========================================================

COMPLETION_TOPIC = (
    "course.completed.events"
)

COMPLETION_GROUP_ID = (
    "recommendation-completion-service"
)


# =========================================================
# IN-MEMORY CATALOG
# =========================================================

course_catalog = {}

catalog_lock = threading.Lock()


# =========================================================
# SKILL NORMALIZATION
# =========================================================

def normalize_skills(raw_skills):

    if isinstance(
        raw_skills,
        list
    ):

        return {
            str(skill): 1.0
            for skill in raw_skills
        }

    if isinstance(
        raw_skills,
        dict
    ):

        normalized = {}

        for skill, value in raw_skills.items():

            try:
                normalized[
                    str(skill)
                ] = float(value)

            except (
                TypeError,
                ValueError
            ):
                normalized[
                    str(skill)
                ] = 1.0

        return normalized

    return {}


# =========================================================
# NORMALIZE COURSE EVENT
# =========================================================

def normalize_course_event(event):

    return {

        "course_id":
            str(
                event["courseId"]
            ),

        "course_title":
            event.get(
                "title",
                ""
            ),

        "description":
            event.get(
                "description"
            ),

        "category":
            event.get(
                "category"
            ),

        "difficulty":
            event.get(
                "difficulty",
                "Unknown"
            ),

        "skills":
            normalize_skills(
                event.get(
                    "skills",
                    []
                )
            ),

        "instructor_id":
            event.get(
                "instructorId"
            )
    }


# =========================================================
# UPDATE RECOMMENDATION DB
# =========================================================

def update_course_database(event):

    db = SessionLocal()

    try:

        course_id = int(
            event["courseId"]
        )

        course = db.get(
            RecommendationCourse,
            course_id
        )

        if course is None:

            course = (
                RecommendationCourse(
                    course_id=course_id
                )
            )

            db.add(course)

        course.course_title = (
            event.get(
                "title",
                ""
            )
        )

        course.description = (
            event.get(
                "description"
            )
        )

        course.category = (
            event.get(
                "category"
            )
        )

        course.difficulty = (
            event.get(
                "difficulty",
                "Unknown"
            )
        )

        course.skills = (
            normalize_skills(
                event.get(
                    "skills",
                    []
                )
            )
        )

        course.instructor_id = (
            event.get(
                "instructorId"
            )
        )

        course.published = bool(
            event.get(
                "published",
                False
            )
        )

        course.active = bool(
            event.get(
                "active",
                True
            )
        )

        course.updated_at = (
            datetime.utcnow()
        )

        db.commit()

        print(
            f"RECOMMENDATION DB UPDATED: "
            f"courseId={course_id}, "
            f"published={course.published}, "
            f"active={course.active}",
            flush=True
        )

    except Exception as error:

        db.rollback()

        print(
            "RECOMMENDATION DB ERROR:",
            error,
            flush=True
        )

    finally:

        db.close()


# =========================================================
# LOAD COURSE CATALOG FROM DB
# =========================================================

def load_course_catalog():

    db = SessionLocal()

    try:

        courses = (
            db.query(
                RecommendationCourse
            )
            .filter(
                RecommendationCourse.published.is_(True),
                RecommendationCourse.active.is_(True)
            )
            .all()
        )

        with catalog_lock:

            course_catalog.clear()

            for course in courses:

                course_catalog[
                    str(course.course_id)
                ] = {

                    "course_id":
                        str(course.course_id),

                    "course_title":
                        course.course_title,

                    "description":
                        course.description,

                    "category":
                        course.category,

                    "difficulty":
                        course.difficulty,

                    "skills":
                        course.skills or {}
                }

        print(
            "RECOMMENDATION CATALOG LOADED FROM DB: "
            f"{len(course_catalog)} courses",
            flush=True
        )

    except Exception as error:

        print(
            "CATALOG LOAD ERROR:",
            error,
            flush=True
        )

    finally:

        db.close()


# =========================================================
# PROCESS COURSE EVENT
# =========================================================

def consume_course_events():

    print(
        "========== "
        "RECOMMENDATION COURSE CONSUMER "
        "STARTING ==========",
        flush=True
    )

    consumer = KafkaConsumer(

        COURSE_TOPIC,

        bootstrap_servers=(
            KAFKA_BOOTSTRAP_SERVERS
        ),

        group_id=COURSE_GROUP_ID,

        auto_offset_reset="earliest",

        enable_auto_commit=True,

        value_deserializer=lambda value:
            json.loads(
                value.decode(
                    "utf-8"
                )
            )
    )

    print(
        "RECOMMENDATION COURSE CONSUMER "
        "CONNECTED",
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

            # -----------------------------------------
            # FIRST: persist event
            # -----------------------------------------

            update_course_database(
                event
            )

            # -----------------------------------------
            # DELETE
            # -----------------------------------------

            with catalog_lock:

                if (
                    event_type ==
                    "COURSE_DELETED"
                ):

                    course_catalog.pop(
                        course_id,
                        None
                    )

                    print(
                        f"COURSE REMOVED: "
                        f"{course_id}",
                        flush=True
                    )

                # -------------------------------------
                # AVAILABLE
                # -------------------------------------

                elif (
                    event.get(
                        "published"
                    ) is True
                    and
                    event.get(
                        "active"
                    ) is True
                ):

                    course = (
                        normalize_course_event(
                            event
                        )
                    )

                    course_catalog[
                        course_id
                    ] = course

                    print(
                        f"COURSE CATALOG UPDATED: "
                        f"courseId={course_id}",
                        flush=True
                    )

                    print(
                        f"CATALOG SIZE: "
                        f"{len(course_catalog)}",
                        flush=True
                    )

                # -------------------------------------
                # NOT AVAILABLE
                # -------------------------------------

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

        except Exception as error:

            print(
                "KAFKA COURSE EVENT PROCESSING ERROR:",
                error,
                flush=True
            )


# =========================================================
# SAVE STUDENT ACTIVITY
# =========================================================

def save_student_activity(
    user_id,
    course_id,
    activity_type,
    activity_at=None
):

    db = SessionLocal()

    try:

        existing = (
            db.query(
                StudentCourseActivity
            )
            .filter(
                StudentCourseActivity.user_id == user_id,
                StudentCourseActivity.course_id == course_id,
                StudentCourseActivity.activity_type == activity_type
            )
            .first()
        )

        if existing is not None:

            print(
                f"ACTIVITY ALREADY EXISTS: "
                f"userId={user_id}, "
                f"courseId={course_id}, "
                f"type={activity_type}",
                flush=True
            )

            return

        activity = StudentCourseActivity(

            user_id=user_id,

            course_id=course_id,

            activity_type=activity_type,

            activity_at=(
                activity_at
                or datetime.utcnow()
            )
        )

        db.add(activity)

        db.commit()

        print(
            "========== STUDENT ACTIVITY SAVED ==========",
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
            f"ACTIVITY: {activity_type}",
            flush=True
        )

        print(
            "=============================================",
            flush=True
        )

    except Exception as error:

        db.rollback()

        print(
            "STUDENT ACTIVITY DATABASE ERROR:",
            error,
            flush=True
        )

    finally:

        db.close()


# =========================================================
# ENROLLMENT CONSUMER
# =========================================================

def consume_enrollment_events():

    print(
        "========== "
        "RECOMMENDATION ENROLLMENT CONSUMER "
        "STARTING ==========",
        flush=True
    )

    consumer = KafkaConsumer(

        ENROLLMENT_TOPIC,

        bootstrap_servers=(
            KAFKA_BOOTSTRAP_SERVERS
        ),

        group_id=ENROLLMENT_GROUP_ID,

        auto_offset_reset="earliest",

        enable_auto_commit=True,

        value_deserializer=lambda value:
            json.loads(
                value.decode(
                    "utf-8"
                )
            )
    )

    print(
        "RECOMMENDATION ENROLLMENT "
        "CONSUMER CONNECTED",
        flush=True
    )

    for message in consumer:

        try:

            event = message.value

            print(
                "========== ENROLLMENT EVENT RECEIVED ==========",
                flush=True
            )

            print(
                event,
                flush=True
            )

            user_id = event.get(
                "userId"
            )

            course_id = event.get(
                "courseId"
            )

            if (
                user_id is None
                or course_id is None
            ):

                print(
                    "ENROLLMENT EVENT ERROR: "
                    "userId/courseId missing",
                    flush=True
                )

                continue

            save_student_activity(

                user_id=int(user_id),

                course_id=int(course_id),

                activity_type="ENROLLED"
            )

        except Exception as error:

            print(
                "KAFKA ENROLLMENT EVENT "
                "PROCESSING ERROR:",
                error,
                flush=True
            )


# =========================================================
# COMPLETION CONSUMER
# =========================================================

def consume_completion_events():

    print(
        "========== "
        "RECOMMENDATION COMPLETION CONSUMER "
        "STARTING ==========",
        flush=True
    )

    consumer = KafkaConsumer(

        COMPLETION_TOPIC,

        bootstrap_servers=(
            KAFKA_BOOTSTRAP_SERVERS
        ),

        group_id=COMPLETION_GROUP_ID,

        auto_offset_reset="earliest",

        enable_auto_commit=True,

        value_deserializer=lambda value:
            json.loads(
                value.decode(
                    "utf-8"
                )
            )
    )

    print(
        "RECOMMENDATION COMPLETION "
        "CONSUMER CONNECTED",
        flush=True
    )

    for message in consumer:

        try:

            event = message.value

            print(
                "========== COURSE COMPLETION EVENT RECEIVED ==========",
                flush=True
            )

            print(
                event,
                flush=True
            )

            user_id = event.get(
                "userId"
            )

            course_id = event.get(
                "courseId"
            )

            completed_at = event.get(
                "completedAt"
            )

            if (
                user_id is None
                or course_id is None
            ):

                print(
                    "COMPLETION EVENT ERROR: "
                    "userId/courseId missing",
                    flush=True
                )

                continue

            # Java LocalDateTime serialized
            # by Jackson normally becomes:
            #
            # [year, month, day, hour, minute, second, nanos]

            activity_at = (
                datetime.utcnow()
            )

            if (
                isinstance(
                    completed_at,
                    list
                )
                and len(completed_at) >= 6
            ):

                try:

                    year = (
                        completed_at[0]
                    )

                    month = (
                        completed_at[1]
                    )

                    day = (
                        completed_at[2]
                    )

                    hour = (
                        completed_at[3]
                    )

                    minute = (
                        completed_at[4]
                    )

                    second = (
                        completed_at[5]
                    )

                    microsecond = 0

                    if len(
                        completed_at
                    ) >= 7:

                        microsecond = (
                            int(
                                completed_at[6]
                            ) // 1000
                        )

                    activity_at = (
                        datetime(
                            year,
                            month,
                            day,
                            hour,
                            minute,
                            second,
                            microsecond
                        )
                    )

                except Exception:

                    activity_at = (
                        datetime.utcnow()
                    )

            save_student_activity(

                user_id=int(user_id),

                course_id=int(course_id),

                activity_type="COMPLETED",

                activity_at=activity_at
            )

        except Exception as error:

            print(
                "KAFKA COMPLETION EVENT "
                "PROCESSING ERROR:",
                error,
                flush=True
            )


# =========================================================
# GET COURSE CATALOG
# =========================================================

def get_course_catalog():

    with catalog_lock:

        return list(
            course_catalog.values()
        )


# =========================================================
# GET STUDENT ACTIVITY
# =========================================================

def get_student_activity(
    user_id
):

    db = SessionLocal()

    try:

        activities = (
            db.query(
                StudentCourseActivity
            )
            .filter(
                StudentCourseActivity.user_id
                == user_id
            )
            .all()
        )

        return activities

    finally:

        db.close()


# =========================================================
# START ALL CONSUMERS
# =========================================================

def start_kafka_consumer():

    load_course_catalog()

    course_thread = threading.Thread(
        target=consume_course_events,
        daemon=True
    )

    enrollment_thread = threading.Thread(
        target=consume_enrollment_events,
        daemon=True
    )

    completion_thread = threading.Thread(
        target=consume_completion_events,
        daemon=True
    )

    course_thread.start()

    enrollment_thread.start()

    completion_thread.start()

    return (
        course_thread,
        enrollment_thread,
        completion_thread
    )