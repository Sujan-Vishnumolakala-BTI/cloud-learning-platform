from sqlalchemy import (
    Column,
    Integer,
    DateTime,
    UniqueConstraint
)

from .database import Base


class CoursePopularity(Base):

    __tablename__ = "course_popularity"

    course_id = Column(
        Integer,
        primary_key=True
    )

    enrollment_count = Column(
        Integer,
        nullable=False,
        default=0
    )


class CourseCompletion(Base):

    __tablename__ = "course_completions"

    id = Column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    user_id = Column(
        Integer,
        nullable=False,
        index=True
    )

    course_id = Column(
        Integer,
        nullable=False,
        index=True
    )

    completed_at = Column(
        DateTime,
        nullable=False
    )

    __table_args__ = (
        UniqueConstraint(
            "user_id",
            "course_id",
            name="uq_user_course_completion"
        ),
    )