def build_student_skills(
    courses,
    completed_courses
):
    """
    Build a simple student skill profile from
    completed courses.

    Each completed course contributes proficiency
    toward its associated skills.

    Current model:
        completed course skill = proficiency 5
    """

    student_skills = {}

    completed_ids = {
        str(course_id)
        for course_id in completed_courses
    }

    for course in courses:

        course_id = str(
            course["course_id"]
        )

        if course_id not in completed_ids:
            continue

        skills = course.get(
            "skills",
            {}
        )

        for skill in skills:

            skill = str(skill)

            current_level = float(
                student_skills.get(
                    skill,
                    0
                )
            )

            # A completed course gives
            # basic/intermediate proficiency.
            student_skills[skill] = max(
                current_level,
                5.0
            )

    return student_skills