import pandas as pd

df = pd.read_csv("dataset/online_learning_dataset.csv")

print("\n========== DATASET OVERVIEW ==========\n")

print(f"Rows: {len(df)}")
print(f"Columns: {len(df.columns)}")

print("\nColumns:")
for col in df.columns:
    print("-", col)

print("\n========== STUDENTS & COURSES ==========\n")

print("Unique Students:", df["student_id"].nunique())
print("Unique Courses:", df["course_id"].nunique())

print("\n========== MISSING VALUES ==========\n")

print(df.isnull().sum())

print("\n========== DUPLICATES ==========\n")

print("Duplicate Rows:", df.duplicated().sum())

print("\n========== DATA TYPES ==========\n")

print(df.dtypes)
print("\n========== STUDENT SKILLS ==========\n")

skill_columns = [
    "python_skill",
    "sql_skill",
    "ml_skill",
    "web_skill"
]

print(df[skill_columns].describe())

print("\n========== STUDENT INTERESTS ==========\n")

interest_columns = [
    "data_interest",
    "ai_interest",
    "web_interest",
    "programming_interest"
]

print(df[interest_columns].describe())

print("\n========== COURSE FEATURES ==========\n")

course_features = [
    "course_python",
    "course_sql",
    "course_ml",
    "course_web",
    "course_data_science"
]

print(df[course_features].describe())

print("\n========== DIFFICULTY DISTRIBUTION ==========\n")

print(df["difficulty"].value_counts())


print("\n========== COMPLETION DISTRIBUTION ==========\n")

print(df["completed"].value_counts())


print("\n========== RATINGS ==========\n")

print(df["rating"].describe())

print("\n========== COURSE CONSISTENCY CHECK ==========\n")

course_counts = df.groupby("course_id")["course_title"].nunique()

inconsistent = course_counts[course_counts > 1]

print("Inconsistent Courses:", len(inconsistent))

if len(inconsistent) > 0:
    print(inconsistent)
else:
    print("All course_ids map to exactly one title.")