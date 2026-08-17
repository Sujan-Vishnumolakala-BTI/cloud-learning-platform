from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker
import os


DATABASE_URL = os.getenv(
    "DATABASE_URL",
    "postgresql://learning_admin:learning_password@localhost:5432/learning_platform_reporting"
)

engine = create_engine(
    DATABASE_URL,
    pool_pre_ping=True
)

SessionLocal = sessionmaker(
    autocommit=False,
    autoflush=False,
    bind=engine
)

Base = declarative_base()