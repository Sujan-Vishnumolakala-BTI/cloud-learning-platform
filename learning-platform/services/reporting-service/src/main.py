from contextlib import asynccontextmanager

from fastapi import FastAPI
from sqlalchemy import desc

from .database import Base, SessionLocal, engine
from .models import CoursePopularity
from .kafka_consumer import start_kafka_consumer


@asynccontextmanager
async def lifespan(app: FastAPI):

    print("Creating reporting database tables...",flush=True)

    Base.metadata.create_all(
        bind=engine
    )

    print("Reporting database tables ready",flush=True)

    start_kafka_consumer()


    print(
        "Kafka consumer thread started",
        flush=True
    )

    yield

    print("Reporting Service shutting down...",flush=True)


app = FastAPI(
    title="CloudPath Reporting Service",
    version="1.0.0",
    lifespan=lifespan
)


@app.get("/health")
def health():

    return {
        "status": "UP",
        "service": "reporting-service"
    }


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