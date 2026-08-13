# import pandas as pd


# def get_beginner_courses(top_n=7):

#     course_catalog = pd.read_csv(
#         "dataset/course_catalog.csv"
#     )

#     beginner_courses = course_catalog[
#         course_catalog["difficulty"] == "Beginner"
#     ]

#     return beginner_courses.head(top_n)

def get_beginner_courses(
    courses,
    top_n=10
):
    """
    Return beginner courses for
    students without a skill profile.
    """

    beginner_courses = [
        course
        for course in courses
        if course.get("difficulty") == "Beginner"
    ]

    return beginner_courses[:top_n]