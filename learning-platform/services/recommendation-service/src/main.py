from contextlib import asynccontextmanager
from typing import Dict, List

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

from src.recommendation.recommender import recommend_courses
from src.kafka_consumer import start_kafka_consumer


@asynccontextmanager
async def lifespan(app: FastAPI):

    start_kafka_consumer()

    print(
        "Recommendation Kafka consumer thread started",
        flush=True
    )

    yield


app = FastAPI(
    title="CloudPath Recommendation Service",
    version="1.0.0",
    lifespan=lifespan
)


app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:4200",
        "http://127.0.0.1:4200"
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class Course(BaseModel):

    course_id: str
    course_title: str
    difficulty: str

    skills: Dict[str, float] = Field(
        default_factory=dict
    )


class RecommendationRequest(BaseModel):

    student_skills: Dict[str, float]

    courses: List[Course]

    completed_courses: List[str] = Field(
        default_factory=list
    )

    enrolled_courses: List[str] = Field(
        default_factory=list
    )

    top_n: int = 10


@app.get("/health")
def health():

    return {
        "status": "UP",
        "service": "recommendation-service"
    }


@app.post("/api/recommendations")
def get_recommendations(
    request: RecommendationRequest
):

    courses = [
        course.model_dump()
        for course in request.courses
    ]

    recommendations = recommend_courses(
        student_skills=request.student_skills,
        courses=courses,
        completed_courses=request.completed_courses,
        enrolled_courses=request.enrolled_courses,
        top_n=request.top_n
    )

    return {
        "recommendations": recommendations
    }