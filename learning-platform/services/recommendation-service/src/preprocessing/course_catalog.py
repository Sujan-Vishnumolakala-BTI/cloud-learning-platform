import pandas as pd

df = pd.read_csv("dataset/online_learning_dataset.csv")

course_catalog = (
    df[
        [
            "course_id",
            "course_title",
            "category",
            "difficulty",
            "duration_hours",
            "course_python",
            "course_sql",
            "course_ml",
            "course_web",
            "course_data_science"
        ]
    ]
    .drop_duplicates()
    .sort_values("course_id")
)

print("Unique Courses:", len(course_catalog))

print("\nFirst 10 Courses:\n")
print(course_catalog.head(10))

course_catalog.to_csv(
    "dataset/course_catalog.csv",
    index=False
)

print("\ncourse_catalog.csv created successfully")