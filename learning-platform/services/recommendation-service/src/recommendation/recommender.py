# # from src.recommendation.similarity_engine import calculate_similarity
# # from src.recommendation.cold_start import get_beginner_courses
# # from src.recommendation.skills import SKILLS


# # def calculate_skill_gap_score(
# #     student_skills,
# #     course_skills
# # ):
# #     """
# #     Higher score means the course teaches
# #     skills where the student has a lower proficiency.

# #     Student proficiency:
# #         0 = no knowledge
# #         10 = expert

# #     Course skill value:
# #         1 = basic
# #         10 = advanced
# #     """

# #     relevant_skills = [
# #         skill
# #         for skill in SKILLS
# #         if float(course_skills.get(skill, 0)) > 0
# #     ]

# #     if not relevant_skills:
# #         return 0.0

# #     total_gap = 0.0

# #     for skill in relevant_skills:

# #         student_level = float(
# #             student_skills.get(skill, 0)
# #         )

# #         gap = max(
# #             0.0,
# #             10.0 - student_level
# #         )

# #         total_gap += gap

# #     maximum_gap = 10.0 * len(relevant_skills)

# #     if maximum_gap == 0:
# #         return 0.0

# #     return total_gap / maximum_gap


# # def recommend_courses(
# #     student_skills,
# #     courses,
# #     completed_courses=None,
# #     enrolled_courses=None,
# #     top_n=10
# # ):
# #     """
# #     Generate personalized course recommendations.
# #     """

# #     if completed_courses is None:
# #         completed_courses = []

# #     if enrolled_courses is None:
# #         enrolled_courses = []

# #     # =========================================
# #     # COLD START
# #     # =========================================

# #     total_skill = sum(
# #         float(value)
# #         for value in student_skills.values()
# #     )

# #     if total_skill == 0:

# #         beginner_courses = get_beginner_courses(
# #             courses,
# #             top_n
# #         )

# #         return [
# #             {
# #                 "course_id": course["course_id"],
# #                 "course_title": course["course_title"],
# #                 "difficulty": course.get(
# #                     "difficulty",
# #                     "Beginner"
# #                 ),
# #                 "score": 1.0,
# #                 "skills": list(
# #                     course.get("skills", {}).keys()
# #                 )
# #             }
# #             for course in beginner_courses
# #         ]

# #     # =========================================
# #     # PERSONALIZED RECOMMENDATIONS
# #     # =========================================

# #     recommendations = []

# #     for course in courses:

# #         course_id = str(
# #             course["course_id"]
# #         )

# #         # -------------------------------------
# #         # Skip completed courses
# #         # -------------------------------------

# #         if course_id in [
# #             str(course)
# #             for course in completed_courses
# #         ]:
# #             continue

# #         # -------------------------------------
# #         # Skip enrolled courses
# #         # -------------------------------------

# #         if course_id in [
# #             str(course)
# #             for course in enrolled_courses
# #         ]:
# #             continue

# #         course_skills = course.get(
# #             "skills",
# #             {}
# #         )

# #         # -------------------------------------
# #         # Similarity
# #         # -------------------------------------

# #         similarity_score = calculate_similarity(
# #             student_skills,
# #             course_skills
# #         )

# #         # -------------------------------------
# #         # Skill gap
# #         # -------------------------------------

# #         skill_gap_score = calculate_skill_gap_score(
# #             student_skills,
# #             course_skills
# #         )

# #         # -------------------------------------
# #         # Combined recommendation score
# #         # -------------------------------------

# #         score = (
# #             0.40 * similarity_score
# #             +
# #             0.60 * skill_gap_score
# #         )

# #         recommendations.append({

# #             "course_id": course_id,

# #             "course_title":
# #                 course.get(
# #                     "course_title",
# #                     course.get("title", "")
# #                 ),

# #             "difficulty":
# #                 course.get(
# #                     "difficulty",
# #                     "Unknown"
# #                 ),

# #             "score":
# #                 float(score),

# #             "similarity_score":
# #                 float(similarity_score),

# #             "skill_gap_score":
# #                 float(skill_gap_score),

# #             "skills":
# #                 list(course_skills.keys())
# #         })

# #     # =========================================
# #     # SORT
# #     # =========================================

# #     recommendations.sort(
# #         key=lambda item: item["score"],
# #         reverse=True
# #     )

# #     return recommendations[:top_n]


# from src.recommendation.similarity_engine import (
#     calculate_similarity
# )

# from src.recommendation.cold_start import (
#     get_beginner_courses
# )

# from src.recommendation.skills import SKILLS


# # =========================================================
# # SKILL GAP
# # =========================================================

# def calculate_skill_gap_score(
#     student_skills,
#     course_skills
# ):

#     relevant_skills = [
#         skill
#         for skill in SKILLS
#         if float(
#             course_skills.get(skill, 0)
#         ) > 0
#     ]

#     if not relevant_skills:
#         return 0.0

#     total_gap = 0.0

#     for skill in relevant_skills:

#         student_level = float(
#             student_skills.get(
#                 skill,
#                 0
#             )
#         )

#         gap = max(
#             0.0,
#             10.0 - student_level
#         )

#         total_gap += gap

#     maximum_gap = (
#         10.0 *
#         len(relevant_skills)
#     )

#     if maximum_gap == 0:
#         return 0.0

#     return (
#         total_gap /
#         maximum_gap
#     )


# # =========================================================
# # RECOMMENDER
# # =========================================================

# def recommend_courses(
#     student_skills,
#     courses,
#     completed_courses=None,
#     enrolled_courses=None,
#     top_n=10
# ):

#     if completed_courses is None:
#         completed_courses = []

#     if enrolled_courses is None:
#         enrolled_courses = []

#     completed_ids = {
#         str(course_id)
#         for course_id in completed_courses
#     }

#     enrolled_ids = {
#         str(course_id)
#         for course_id in enrolled_courses
#     }

#     # =====================================================
#     # COLD START
#     # =====================================================

#     total_skill = sum(
#         float(value)
#         for value in student_skills.values()
#     )

#     if total_skill == 0:

#         beginner_courses = (
#             get_beginner_courses(
#                 courses,
#                 top_n
#             )
#         )

#         return [
#             {
#                 "course_id":
#                     course["course_id"],

#                 "course_title":
#                     course["course_title"],

#                 "difficulty":
#                     course.get(
#                         "difficulty",
#                         "Beginner"
#                     ),

#                 "score": 1.0,

#                 "skills":
#                     list(
#                         course.get(
#                             "skills",
#                             {}
#                         ).keys()
#                     )
#             }
#             for course
#             in beginner_courses
#         ]

#     # =====================================================
#     # PERSONALIZED
#     # =====================================================

#     recommendations = []

#     for course in courses:

#         course_id = str(
#             course["course_id"]
#         )

#         # -----------------------------------------------
#         # Skip completed
#         # -----------------------------------------------

#         if course_id in completed_ids:
#             continue

#         # -----------------------------------------------
#         # Skip enrolled
#         # -----------------------------------------------

#         if course_id in enrolled_ids:
#             continue

#         course_skills = course.get(
#             "skills",
#             {}
#         )

#         # -----------------------------------------------
#         # Similarity
#         # -----------------------------------------------

#         similarity_score = (
#             calculate_similarity(
#                 student_skills,
#                 course_skills
#             )
#         )

#         # -----------------------------------------------
#         # Skill gap
#         # -----------------------------------------------

#         skill_gap_score = (
#             calculate_skill_gap_score(
#                 student_skills,
#                 course_skills
#             )
#         )

#         # -----------------------------------------------
#         # Combined score
#         # -----------------------------------------------

#         score = (
#             0.40 *
#             similarity_score
#             +
#             0.60 *
#             skill_gap_score
#         )

#         recommendations.append({

#             "course_id":
#                 course_id,

#             "course_title":
#                 course.get(
#                     "course_title",
#                     course.get(
#                         "title",
#                         ""
#                     )
#                 ),

#             "difficulty":
#                 course.get(
#                     "difficulty",
#                     "Unknown"
#                 ),

#             "score":
#                 float(score),

#             "similarity_score":
#                 float(
#                     similarity_score
#                 ),

#             "skill_gap_score":
#                 float(
#                     skill_gap_score
#                 ),

#             "skills":
#                 list(
#                     course_skills.keys()
#                 )
#         })

#     # =====================================================
#     # SORT
#     # =====================================================

#     recommendations.sort(
#         key=lambda item:
#             item["score"],
#         reverse=True
#     )

#     return recommendations[:top_n]

from src.recommendation.similarity_engine import calculate_similarity
from src.recommendation.skills import SKILLS


def calculate_skill_gap_score(
    student_skills,
    course_skills
):
    """
    Higher score means the course teaches
    skills where the student has lower proficiency.

    Student proficiency:
        0 = no knowledge
        10 = expert
    """

    relevant_skills = [
        skill
        for skill in SKILLS
        if float(course_skills.get(skill, 0)) > 0
    ]

    if not relevant_skills:
        return 0.0

    total_gap = 0.0

    for skill in relevant_skills:

        student_level = float(
            student_skills.get(skill, 0)
        )

        gap = max(
            0.0,
            10.0 - student_level
        )

        total_gap += gap

    maximum_gap = 10.0 * len(relevant_skills)

    return total_gap / maximum_gap


def recommend_courses(
    student_skills,
    courses,
    completed_courses=None,
    enrolled_courses=None,
    top_n=10
):

    if completed_courses is None:
        completed_courses = []

    if enrolled_courses is None:
        enrolled_courses = []

    completed_ids = {
        str(course_id)
        for course_id in completed_courses
    }

    enrolled_ids = {
        str(course_id)
        for course_id in enrolled_courses
    }

    recommendations = []

    # =====================================================
    # COLD START
    # =====================================================

    total_skill = sum(
        float(value)
        for value in student_skills.values()
    )

    if total_skill == 0:

        for course in courses:

            course_id = str(
                course["course_id"]
            )

            # Never recommend completed courses
            if course_id in completed_ids:
                continue

            # Never recommend already enrolled courses
            if course_id in enrolled_ids:
                continue

            course_skills = course.get(
                "skills",
                {}
            )

            recommendations.append({

                "course_id":
                    course_id,

                "course_title":
                    course.get(
                        "course_title",
                        ""
                    ),

                "difficulty":
                    course.get(
                        "difficulty",
                        "Unknown"
                    ),

                "score":
                    1.0,

                "similarity_score":
                    0.0,

                "skill_gap_score":
                    1.0,

                "skills":
                    list(
                        course_skills.keys()
                    )
            })

        return recommendations[:top_n]

    # =====================================================
    # PERSONALIZED RECOMMENDATIONS
    # =====================================================

    for course in courses:

        course_id = str(
            course["course_id"]
        )

        # -------------------------------------------------
        # Skip completed
        # -------------------------------------------------

        if course_id in completed_ids:
            continue

        # -------------------------------------------------
        # Skip enrolled
        # -------------------------------------------------

        if course_id in enrolled_ids:
            continue

        course_skills = course.get(
            "skills",
            {}
        )

        # -------------------------------------------------
        # Similarity
        # -------------------------------------------------

        similarity_score = calculate_similarity(
            student_skills,
            course_skills
        )

        # -------------------------------------------------
        # Skill gap
        # -------------------------------------------------

        skill_gap_score = calculate_skill_gap_score(
            student_skills,
            course_skills
        )

        # -------------------------------------------------
        # Combined score
        # -------------------------------------------------

        score = (
            0.40 * similarity_score
            +
            0.60 * skill_gap_score
        )

        recommendations.append({

            "course_id":
                course_id,

            "course_title":
                course.get(
                    "course_title",
                    course.get(
                        "title",
                        ""
                    )
                ),

            "difficulty":
                course.get(
                    "difficulty",
                    "Unknown"
                ),

            "score":
                float(score),

            "similarity_score":
                float(similarity_score),

            "skill_gap_score":
                float(skill_gap_score),

            "skills":
                list(
                    course_skills.keys()
                )
        })

    # =====================================================
    # SORT
    # =====================================================

    recommendations.sort(
        key=lambda item:
            item["score"],
        reverse=True
    )

    return recommendations[:top_n]