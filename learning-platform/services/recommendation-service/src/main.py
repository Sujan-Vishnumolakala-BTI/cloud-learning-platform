# from contextlib import asynccontextmanager
# from typing import Dict, List

# from fastapi import FastAPI
# from fastapi.middleware.cors import CORSMiddleware
# from pydantic import BaseModel, Field

# from src.database import Base, engine

# from src.recommendation.recommender import recommend_courses


# from src.kafka_consumer import (
#     start_kafka_consumer,
#     get_course_catalog
# )

# from datetime import datetime

# from src.database import (
#     Base,
#     SessionLocal,
#     engine
# )

# from src.models import StudentRecommendation

# @asynccontextmanager
# async def lifespan(app: FastAPI):

#     print(
#         "Creating recommendation database tables...",
#         flush=True
#     )

#     Base.metadata.create_all(
#         bind=engine
#     )

#     print(
#         "Recommendation database tables ready",
#         flush=True
#     )

#     start_kafka_consumer()

#     print(
#         "Recommendation Kafka consumer thread started",
#         flush=True
#     )

#     yield

# app = FastAPI(
#     title="CloudPath Recommendation Service",
#     version="1.0.0",
#     lifespan=lifespan
# )


# app.add_middleware(
#     CORSMiddleware,
#     allow_origins=[
#         "http://localhost:4200",
#         "http://127.0.0.1:4200"
#     ],
#     allow_credentials=True,
#     allow_methods=["*"],
#     allow_headers=["*"],
# )


# class RecommendationRequest(BaseModel):

#     user_id: int

#     student_skills: Dict[str, float]

#     completed_courses: List[str] = Field(
#         default_factory=list
#     )

#     enrolled_courses: List[str] = Field(
#         default_factory=list
#     )

#     top_n: int = 10


# @app.get("/health")
# def health():

#     return {
#         "status": "UP",
#         "service": "recommendation-service"
#     }

# @app.get("/api/recommendations/{user_id}")
# def get_student_recommendations(
#     user_id: int
# ):

#     db = SessionLocal()

#     try:

#         recommendations = (
#             db.query(StudentRecommendation)
#             .filter(
#                 StudentRecommendation.user_id
#                 == user_id
#             )
#             .order_by(
#                 StudentRecommendation.score.desc()
#             )
#             .all()
#         )

#         return {
#             "userId": user_id,
#             "recommendations": [
#                 {
#                     "course_id": r.course_id,
#                     "course_title": r.course_title,
#                     "difficulty": r.difficulty,
#                     "score": r.score,
#                     "similarity_score":
#                         r.similarity_score,
#                     "skill_gap_score":
#                         r.skill_gap_score,
#                     "skills": r.skills,
#                     "generated_at":
#                         r.generated_at
#                 }
#                 for r in recommendations
#             ]
#         }

#     finally:

#         db.close()


# @app.post("/api/recommendations")
# def get_recommendations(
#     request: RecommendationRequest
# ):

#     courses = get_course_catalog()

#     recommendations = recommend_courses(
#         student_skills=request.student_skills,
#         courses=courses,
#         completed_courses=request.completed_courses,
#         enrolled_courses=request.enrolled_courses,
#         top_n=request.top_n
#     )

#     db = SessionLocal()

#     try:

#         # Remove previous recommendations
#         db.query(
#             StudentRecommendation
#         ).filter(
#             StudentRecommendation.user_id
#             == request.user_id
#         ).delete()

#         # Save new recommendations
#         for recommendation in recommendations:

#             db.add(
#                 StudentRecommendation(

#                     user_id=request.user_id,

#                     course_id=int(
#                         recommendation["course_id"]
#                     ),

#                     course_title=
#                         recommendation["course_title"],

#                     difficulty=
#                         recommendation["difficulty"],

#                     score=
#                         recommendation["score"],

#                     similarity_score=
#                         recommendation["similarity_score"],

#                     skill_gap_score=
#                         recommendation["skill_gap_score"],

#                     skills=
#                         recommendation["skills"],

#                     generated_at=datetime.utcnow()
#                 )
#             )

#         db.commit()

#     except Exception:

#         db.rollback()
#         raise

#     finally:

#         db.close()

#     return {
#         "userId": request.user_id,
#         "recommendations": recommendations
#     }

from contextlib import asynccontextmanager

from typing import Dict

from fastapi import (
    FastAPI,
    HTTPException
)

from fastapi.middleware.cors import (
    CORSMiddleware
)

from pydantic import (
    BaseModel,
    Field
)

from src.database import (
    Base,
    engine
)

from src.recommendation_service import (
    generate_recommendations,
    get_saved_recommendations
)

from src.kafka_consumer import (
    start_kafka_consumer
)

from contextlib import asynccontextmanager
from typing import Dict, List

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import desc

from src.database import Base, SessionLocal, engine

from src.models import (
    RecommendationCourse,
    StudentRecommendation,
    StudentCourseActivity
)

from src.recommendation.recommender import recommend_courses

from src.recommendation.student_profile import (
    build_student_skills
)

from src.kafka_consumer import (
    start_kafka_consumer,
    get_course_catalog
)
# =========================================================
# APPLICATION LIFESPAN
# =========================================================

@asynccontextmanager
async def lifespan(app: FastAPI):

    print(
        "Creating recommendation database tables...",
        flush=True
    )

    Base.metadata.create_all(
        bind=engine
    )

    print(
        "Recommendation database tables ready",
        flush=True
    )

    start_kafka_consumer()

    print(
        "Recommendation Kafka consumers started",
        flush=True
    )

    yield

    print(
        "Recommendation Service shutting down...",
        flush=True
    )


# =========================================================
# FASTAPI
# =========================================================

app = FastAPI(

    title=(
        "CloudPath Recommendation Service"
    ),

    version="1.0.0",

    lifespan=lifespan
)


# =========================================================
# CORS
# =========================================================

# app.add_middleware(

#     CORSMiddleware,

#     allow_origins=[
#         "http://localhost:4200",
#         "http://127.0.0.1:4200"
#     ],

#     allow_credentials=True,

#     allow_methods=["*"],

#     allow_headers=["*"]
# )


# =========================================================
# REQUEST MODEL
# =========================================================

class RecommendationRequest(
    BaseModel
):

    user_id: int

    student_skills: Dict[
        str,
        float
    ]

    top_n: int = Field(
        default=10,
        ge=1,
        le=50
    )


# =========================================================
# HEALTH
# =========================================================

@app.get("/health")
def health():

    return {

        "status": "UP",

        "service":
            "recommendation-service"
    }


# =========================================================
# GENERATE RECOMMENDATIONS
# =========================================================

@app.post(
    "/api/recommendations"
)
def generate_user_recommendations(
    request: RecommendationRequest
):

    try:

        recommendations = (
            generate_recommendations(

                user_id=request.user_id,

                student_skills=(
                    request.student_skills
                ),

                top_n=request.top_n
            )
        )

        return {

            "userId":
                request.user_id,

            "recommendations":
                recommendations
        }

    except Exception as error:

        print(
            "RECOMMENDATION API ERROR:",
            error,
            flush=True
        )

        raise HTTPException(

            status_code=500,

            detail=(
                "Failed to generate recommendations"
            )
        )


# =========================================================
# GET SAVED RECOMMENDATIONS
# =========================================================

@app.get("/api/recommendations/{user_id}")
def get_saved_recommendations(
    user_id: int
):

    db = SessionLocal()

    try:

        # =================================================
        # GET USER ACTIVITY
        # =================================================

        activities = (
            db.query(StudentCourseActivity)
            .filter(
                StudentCourseActivity.user_id == user_id
            )
            .all()
        )

        completed_courses = [
            str(activity.course_id)
            for activity in activities
            if activity.activity_type == "COMPLETED"
        ]

        enrolled_courses = [
            str(activity.course_id)
            for activity in activities
            if activity.activity_type == "ENROLLED"
        ]

        # =================================================
        # GET AVAILABLE COURSES
        # =================================================

        db_courses = (
            db.query(RecommendationCourse)
            .filter(
                RecommendationCourse.published.is_(True),
                RecommendationCourse.active.is_(True)
            )
            .all()
        )

        courses = [
            {
                "course_id": str(course.course_id),

                "course_title":
                    course.course_title,

                "difficulty":
                    course.difficulty,

                "skills":
                    course.skills or {}
            }
            for course in db_courses
        ]

        # =================================================
        # BUILD STUDENT PROFILE
        # =================================================

        student_skills = build_student_skills(
            courses,
            completed_courses
        )

        # =================================================
        # GENERATE RECOMMENDATIONS
        # =================================================

        recommendations = recommend_courses(

            student_skills=student_skills,

            courses=courses,

            completed_courses=
                completed_courses,

            enrolled_courses=
                enrolled_courses,

            top_n=10
        )

        # =================================================
        # DELETE OLD RECOMMENDATIONS
        # =================================================

        (
            db.query(StudentRecommendation)
            .filter(
                StudentRecommendation.user_id == user_id
            )
            .delete(
                synchronize_session=False
            )
        )

        # =================================================
        # SAVE NEW RECOMMENDATIONS
        # =================================================

        for recommendation in recommendations:

            db_recommendation = (
                StudentRecommendation(

                    user_id=user_id,

                    course_id=int(
                        recommendation[
                            "course_id"
                        ]
                    ),

                    course_title=
                        recommendation[
                            "course_title"
                        ],

                    difficulty=
                        recommendation[
                            "difficulty"
                        ],

                    score=
                        recommendation[
                            "score"
                        ],

                    similarity_score=
                        recommendation[
                            "similarity_score"
                        ],

                    skill_gap_score=
                        recommendation[
                            "skill_gap_score"
                        ],

                    skills=
                        recommendation[
                            "skills"
                        ]
                )
            )

            db.add(
                db_recommendation
            )

        db.commit()

        print(
            "========== RECOMMENDATIONS SAVED ==========",
            flush=True
        )

        print(
            f"USER ID: {user_id}",
            flush=True
        )

        print(
            f"COUNT: {len(recommendations)}",
            flush=True
        )

        print(
            "============================================",
            flush=True
        )

        # =================================================
        # RETURN RESULT
        # =================================================

        saved = (
            db.query(StudentRecommendation)
            .filter(
                StudentRecommendation.user_id == user_id
            )
            .order_by(
                desc(
                    StudentRecommendation.score
                )
            )
            .all()
        )

        return {
            "userId": user_id,
            "recommendations": [
                {
                    "course_id":
                        str(item.course_id),

                    "course_title":
                        item.course_title,

                    "difficulty":
                        item.difficulty,

                    "score":
                        item.score,

                    "similarity_score":
                        item.similarity_score,

                    "skill_gap_score":
                        item.skill_gap_score,

                    "skills":
                        item.skills,

                    "generated_at":
                        item.generated_at
                }
                for item in saved
            ]
        }

    finally:

        db.close()