def update_student_skills(student_skills, completed_course):
    """
    Update student skills after completing a course.

    Skills are capped at 10.
    """

    updated_skills = {
        "python_skill": min(
            10,
            student_skills["python_skill"]
            + completed_course["course_python"]
        ),

        "sql_skill": min(
            10,
            student_skills["sql_skill"]
            + completed_course["course_sql"]
        ),

        "ml_skill": min(
            10,
            student_skills["ml_skill"]
            + completed_course["course_ml"]
        ),

        "web_skill": min(
            10,
            student_skills["web_skill"]
            + completed_course["course_web"]
        ),

        "data_science_skill": min(
            10,
            student_skills["data_science_skill"]
            + completed_course["course_data_science"]
        )
    }

    return updated_skills