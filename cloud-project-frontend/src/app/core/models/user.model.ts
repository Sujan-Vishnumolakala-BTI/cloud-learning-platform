export type UserRole = 'student' | 'instructor' | 'admin';

export interface User {
  id: string;
  fullName: string;
  email: string;
  role: UserRole;

  studentId?: string;

  avatarUrl?: string;

  enrolledCourseIds: string[];
  completedCourseIds: string[];

  createdAt: string;
}

export interface LoginPayload {
  email: string;
  password: string;
  rememberMe: boolean;
}

export interface RegisterPayload {

  firstName: string;

  lastName: string;

  email: string;

  password: string;

  role: 'student' | 'instructor';
}

export interface LoginResponse { accessToken: string; refreshToken: string; }
export interface StoredAuth { accessToken: string; refreshToken: string; user: User; }

export interface SkillsResponse {
  skill: string;
  proficiency: number;
}

export interface UserSkillsResponse {
  userId: number;
  skills: SkillsResponse[];
}

export interface Quiz {
  id: number;
  lessonId: number;
  title: string;
  description: string | null;
  passingScore: number;
  createdAt: string;
  updatedAt: string;
}

export interface QuizQuestion {
  id: number;
  quizId: number;
  questionText: string;
  orderIndex: number;
  createdAt: string;
  updatedAt: string;
}

export interface QuizOption {
  id: number;
  questionId: number;
  optionText: string;
  correct?: boolean;
}

export interface QuizQuestionWithOptions
  extends QuizQuestion {
  options: QuizOption[];
}

export interface QuizAttempt {
  id: number;
  userId: number;
  quizId: number;
  status: string;
  startedAt: string;
  submittedAt: string | null;
  totalQuestions: number;
  correctAnswers: number;
  score: number;
  passed: boolean;
}

export interface QuizAnswer {
  questionId: number;
  optionId: number;
}

export interface SubmitQuizRequest {
  answers: QuizAnswer[];
}