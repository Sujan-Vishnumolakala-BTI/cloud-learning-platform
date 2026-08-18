from datetime import datetime

from src.database import SessionLocal

from src.models import (
    StudentRecommendation,
    StudentCourseActivity
)

from src.recommendation.recommender import (
    recommend_courses
)

from src.kafka_consumer import (
    get_course_catalog
)


# =========================================================
# GET USER COURSE ACTIVITY
# =========================================================

def get_user_activity(
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

        completed_courses = []

        enrolled_courses = []

        for activity in activities:

            if (
                activity.activity_type
                == "COMPLETED"
            ):

                completed_courses.append(
                    str(
                        activity.course_id
                    )
                )

            elif (
                activity.activity_type
                == "ENROLLED"
            ):

                enrolled_courses.append(
                    str(
                        activity.course_id
                    )
                )

        return (
            completed_courses,
            enrolled_courses
        )

    finally:

        db.close()


# =========================================================
# GENERATE RECOMMENDATIONS
# =========================================================

def generate_recommendations(
    user_id,
    student_skills,
    top_n=10
):

    courses = get_course_catalog()

    (
        completed_courses,
        enrolled_courses
    ) = get_user_activity(
        user_id
    )

    recommendations = (
        recommend_courses(

            student_skills=student_skills,

            courses=courses,

            completed_courses=(
                completed_courses
            ),

            enrolled_courses=(
                enrolled_courses
            ),

            top_n=top_n
        )
    )

    save_recommendations(
        user_id,
        recommendations
    )

    return recommendations


# =========================================================
# SAVE RECOMMENDATIONS
# =========================================================

def save_recommendations(
    user_id,
    recommendations
):

    db = SessionLocal()

    try:

        # Remove old recommendations
        (
            db.query(
                StudentRecommendation
            )
            .filter(
                StudentRecommendation.user_id
                == user_id
            )
            .delete(
                synchronize_session=False
            )
        )

        for recommendation in (
            recommendations
        ):

            record = StudentRecommendation(

                user_id=user_id,

                course_id=int(
                    recommendation[
                        "course_id"
                    ]
                ),

                course_title=(
                    recommendation[
                        "course_title"
                    ]
                ),

                difficulty=(
                    recommendation.get(
                        "difficulty",
                        "Unknown"
                    )
                ),

                score=float(
                    recommendation[
                        "score"
                    ]
                ),

                similarity_score=float(
                    recommendation.get(
                        "similarity_score",
                        0
                    )
                ),

                skill_gap_score=float(
                    recommendation.get(
                        "skill_gap_score",
                        0
                    )
                ),

                skills=(
                    recommendation.get(
                        "skills",
                        []
                    )
                ),

                generated_at=(
                    datetime.utcnow()
                )
            )

            db.add(record)

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

    except Exception as error:

        db.rollback()

        print(
            "RECOMMENDATION SAVE ERROR:",
            error,
            flush=True
        )

        raise

    finally:

        db.close()


# =========================================================
# GET SAVED RECOMMENDATIONS
# =========================================================

def get_saved_recommendations(
    user_id
):

    db = SessionLocal()

    try:

        records = (
            db.query(
                StudentRecommendation
            )
            .filter(
                StudentRecommendation.user_id
                == user_id
            )
            .order_by(
                StudentRecommendation.score.desc()
            )
            .all()
        )

        return [

            {
                "course_id":
                    str(
                        record.course_id
                    ),

                "course_title":
                    record.course_title,

                "difficulty":
                    record.difficulty,

                "score":
                    record.score,

                "similarity_score":
                    record.similarity_score,

                "skill_gap_score":
                    record.skill_gap_score,

                "skills":
                    record.skills,

                "generated_at":
                    record.generated_at
            }

            for record in records
        ]

    finally:

        db.close()