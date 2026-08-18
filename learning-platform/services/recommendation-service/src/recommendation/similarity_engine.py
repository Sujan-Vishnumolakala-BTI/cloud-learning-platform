# # # import numpy as np
# # # from sklearn.metrics.pairwise import cosine_similarity


# # # def calculate_similarity(student_vector, course_vector):
# # #     """
# # #     Calculate similarity between student skills
# # #     and course skills.
# # #     """

# # #     student_vector = np.array(student_vector).reshape(1, -1)
# # #     course_vector = np.array(course_vector).reshape(1, -1)

# # #     similarity = cosine_similarity(
# # #         student_vector,
# # #         course_vector
# # #     )[0][0]

# # #     return float(similarity)

# # import numpy as np
# # from sklearn.metrics.pairwise import cosine_similarity


# # def calculate_similarity(student_vector, course_vector):
# #     """
# #     Calculate cosine similarity between
# #     student skills and course skills.
# #     """

# #     student_vector = np.array(
# #         student_vector,
# #         dtype=float
# #     ).reshape(1, -1)

# #     course_vector = np.array(
# #         course_vector,
# #         dtype=float
# #     ).reshape(1, -1)

# #     similarity = cosine_similarity(
# #         student_vector,
# #         course_vector
# #     )[0][0]

# #     return float(similarity)

# import numpy as np
# from sklearn.metrics.pairwise import cosine_similarity

# from src.recommendation.skills import SKILLS


# def build_skill_vector(skills):
#     """
#     Convert a skill dictionary into a fixed-length vector.

#     Example:

#     {
#         "Python": 7,
#         "SQL": 5,
#         "Docker": 3
#     }

#     becomes a 24-dimensional vector based on SKILLS.
#     """

#     return np.array(
#         [
#             float(skills.get(skill, 0))
#             for skill in SKILLS
#         ],
#         dtype=float
#     )


# def calculate_similarity(
#     student_skills,
#     course_skills
# ):
#     """
#     Calculate cosine similarity between
#     student skills and course skills.
#     """

#     student_vector = build_skill_vector(
#         student_skills
#     )

#     course_vector = build_skill_vector(
#         course_skills
#     )

#     # Avoid cosine similarity problems
#     # when both vectors are zero.
#     if (
#         np.linalg.norm(student_vector) == 0
#         or np.linalg.norm(course_vector) == 0
#     ):
#         return 0.0

#     similarity = cosine_similarity(
#         student_vector.reshape(1, -1),
#         course_vector.reshape(1, -1)
#     )[0][0]

#     return float(similarity)

import numpy as np

from sklearn.metrics.pairwise import cosine_similarity

from src.recommendation.skills import SKILLS


def build_skill_vector(skills):

    return np.array(
        [
            float(
                skills.get(skill, 0)
            )
            for skill in SKILLS
        ],
        dtype=float
    )


def calculate_similarity(
    student_skills,
    course_skills
):

    student_vector = build_skill_vector(
        student_skills
    )

    course_vector = build_skill_vector(
        course_skills
    )

    if (
        np.linalg.norm(student_vector) == 0
        or np.linalg.norm(course_vector) == 0
    ):
        return 0.0

    similarity = cosine_similarity(
        student_vector.reshape(1, -1),
        course_vector.reshape(1, -1)
    )[0][0]

    return float(similarity)