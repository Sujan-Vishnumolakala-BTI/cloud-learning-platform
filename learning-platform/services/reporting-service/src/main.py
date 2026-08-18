from contextlib import asynccontextmanager

from fastapi import FastAPI, HTTPException
from sqlalchemy import desc

from .database import Base, SessionLocal, engine
from .models import CoursePopularity, CourseCompletion
from .kafka_consumer import start_kafka_consumer


@asynccontextmanager
async def lifespan(app: FastAPI):

    print(
        "Creating reporting database tables...",
        flush=True
    )

    Base.metadata.create_all(
        bind=engine
    )

    print(
        "Reporting database tables ready",
        flush=True
    )

    start_kafka_consumer()

    print(
        "Kafka consumer thread started",
        flush=True
    )

    yield

    print(
        "Reporting Service shutting down...",
        flush=True
    )


app = FastAPI(
    title="CloudPath Reporting Service",
    version="1.0.0",
    lifespan=lifespan
)


# =========================================================
# HEALTH
# =========================================================

@app.get("/health")
def health():

    return {
        "status": "UP",
        "service": "reporting-service"
    }


# =========================================================
# COURSE POPULARITY
# =========================================================

@app.get("/api/reports/courses/popular")
def get_popular_courses():

    db = SessionLocal()

    try:

        reports = (
            db.query(CoursePopularity)
            .order_by(
                desc(
                    CoursePopularity.enrollment_count
                )
            )
            .all()
        )

        return [
            {
                "courseId": report.course_id,
                "enrollments": report.enrollment_count
            }
            for report in reports
        ]

    finally:

        db.close()


# =========================================================
# ALL COURSE COMPLETIONS
# =========================================================

@app.get("/api/reports/courses/completions")
def get_course_completions():

    db = SessionLocal()

    try:

        reports = (
            db.query(CourseCompletion)
            .order_by(
                desc(
                    CourseCompletion.completed_at
                )
            )
            .all()
        )

        return [
            {
                "id": report.id,
                "userId": report.user_id,
                "courseId": report.course_id,
                "completedAt": report.completed_at
            }
            for report in reports
        ]

    finally:

        db.close()


# =========================================================
# COMPLETIONS BY USER
# =========================================================

@app.get("/api/reports/users/{user_id}/completions")
def get_user_completions(user_id: int):

    db = SessionLocal()

    try:

        reports = (
            db.query(CourseCompletion)
            .filter(
                CourseCompletion.user_id == user_id
            )
            .order_by(
                desc(
                    CourseCompletion.completed_at
                )
            )
            .all()
        )

        return [
            {
                "id": report.id,
                "userId": report.user_id,
                "courseId": report.course_id,
                "completedAt": report.completed_at
            }
            for report in reports
        ]

    finally:

        db.close()


# =========================================================
# COMPLETIONS BY COURSE
# =========================================================

@app.get("/api/reports/courses/{course_id}/completions")
def get_course_completion_report(course_id: int):

    db = SessionLocal()

    try:

        reports = (
            db.query(CourseCompletion)
            .filter(
                CourseCompletion.course_id == course_id
            )
            .order_by(
                desc(
                    CourseCompletion.completed_at
                )
            )
            .all()
        )

        return [
            {
                "id": report.id,
                "userId": report.user_id,
                "courseId": report.course_id,
                "completedAt": report.completed_at
            }
            for report in reports
        ]

    finally:

        db.close()


# =========================================================
# COMPLETION SUMMARY
# =========================================================

@app.get("/api/reports/courses/{course_id}/completion-summary")
def get_course_completion_summary(course_id: int):

    db = SessionLocal()

    try:

        count = (
            db.query(CourseCompletion)
            .filter(
                CourseCompletion.course_id == course_id
            )
            .count()
        )

        return {
            "courseId": course_id,
            "completionCount": count
        }

    finally:

        db.close()