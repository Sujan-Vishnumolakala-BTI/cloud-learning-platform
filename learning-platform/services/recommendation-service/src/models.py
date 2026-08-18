from datetime import datetime

from sqlalchemy import (
    Boolean,
    DateTime,
    Float,
    Integer,
    String,
    Text,
    UniqueConstraint
)

from sqlalchemy.dialects.postgresql import JSONB

from sqlalchemy.orm import Mapped, mapped_column

from .database import Base


# =========================================================
# RECOMMENDATION COURSE
# =========================================================

class RecommendationCourse(Base):

    __tablename__ = "recommendation_courses"

    course_id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True
    )

    course_title: Mapped[str] = mapped_column(
        String(255),
        nullable=False
    )

    description: Mapped[str | None] = mapped_column(
        Text,
        nullable=True
    )

    category: Mapped[str | None] = mapped_column(
        String(100),
        nullable=True
    )

    difficulty: Mapped[str] = mapped_column(
        String(50),
        nullable=False,
        default="Unknown"
    )

    skills: Mapped[dict] = mapped_column(
        JSONB,
        nullable=False,
        default=dict
    )

    instructor_id: Mapped[int | None] = mapped_column(
        Integer,
        nullable=True
    )

    published: Mapped[bool] = mapped_column(
        Boolean,
        nullable=False,
        default=False
    )

    active: Mapped[bool] = mapped_column(
        Boolean,
        nullable=False,
        default=True
    )

    updated_at: Mapped[datetime] = mapped_column(
        DateTime,
        nullable=False,
        default=datetime.utcnow,
        onupdate=datetime.utcnow
    )


# =========================================================
# STUDENT RECOMMENDATION
# =========================================================

class StudentRecommendation(Base):

    __tablename__ = "student_recommendations"

    id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    user_id: Mapped[int] = mapped_column(
        Integer,
        nullable=False,
        index=True
    )

    course_id: Mapped[int] = mapped_column(
        Integer,
        nullable=False,
        index=True
    )

    course_title: Mapped[str] = mapped_column(
        String(255),
        nullable=False
    )

    difficulty: Mapped[str] = mapped_column(
        String(50),
        nullable=False,
        default="Unknown"
    )

    score: Mapped[float] = mapped_column(
        Float,
        nullable=False
    )

    similarity_score: Mapped[float] = mapped_column(
        Float,
        nullable=False
    )

    skill_gap_score: Mapped[float] = mapped_column(
        Float,
        nullable=False
    )

    skills: Mapped[list] = mapped_column(
        JSONB,
        nullable=False,
        default=list
    )

    generated_at: Mapped[datetime] = mapped_column(
        DateTime,
        nullable=False,
        default=datetime.utcnow
    )


# =========================================================
# STUDENT COURSE ACTIVITY
# =========================================================

class StudentCourseActivity(Base):

    __tablename__ = "student_course_activity"

    id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    user_id: Mapped[int] = mapped_column(
        Integer,
        nullable=False,
        index=True
    )

    course_id: Mapped[int] = mapped_column(
        Integer,
        nullable=False,
        index=True
    )

    activity_type: Mapped[str] = mapped_column(
        String(50),
        nullable=False
    )

    activity_at: Mapped[datetime] = mapped_column(
        DateTime,
        nullable=False,
        default=datetime.utcnow
    )

    __table_args__ = (
        UniqueConstraint(
            "user_id",
            "course_id",
            "activity_type",
            name="uq_student_course_activity"
        ),
    )