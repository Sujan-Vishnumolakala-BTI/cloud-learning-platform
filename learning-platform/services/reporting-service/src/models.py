from sqlalchemy import Column, Integer
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