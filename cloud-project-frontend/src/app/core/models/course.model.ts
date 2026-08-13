export interface Course {

  id: number;

  title: string;

  description: string;

  category: string;

  skills: string[];

  published: boolean;

  active: boolean;

  instructorId: number;

  createdAt: string;

  updatedAt: string;
}

export interface EnrolledCourse extends Course {
  progress: number;
  lastAccessed: string;
}

export interface ActivityItem {
  id: string;
  type: 'module' | 'video' | 'quiz' | 'certificate';
  title: string;
  timestamp: string;
}

export interface Testimonial {
  id: string;
  name: string;
  role: string;
  quote: string;
  avatarUrl: string;
  rating: number;
}

// export interface CourseProgress {
//   courseId: number;
//   userId: number;
//   totalLessons: number;
//   completedLessons: number;
//   progressPercentage: number;
//   status: string;
// }

export interface Module {
  id: number;
  courseId: number;
  title: string;
  description: string;
  orderIndex: number;
  createdAt: string;
  updatedAt: string;
}

export interface Lesson {
  id: number;
  moduleId: number;
  title: string;
  description: string;
  orderIndex: number;
  contentType: string;
  contentUrl?: string;
  durationMinutes?: number;
  createdAt: string;
  updatedAt: string;
}

export interface CourseProgress {
  courseId: number;
  userId: number;
  totalLessons: number;
  completedLessons: number;
  progressPercentage: number;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
}

export interface LessonProgress {
  id: number;
  userId: number;
  lessonId: number;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
  startedAt?: string;
  completedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Quiz {
  id: number;
  lessonId: number;
  title: string;
  description?: string;
  passingScore?: number;
  timeLimitMinutes?: number;
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
  correct: boolean;
  // orderIndex: number;
}

export interface QuizQuestionWithOptions extends QuizQuestion {
  options: QuizOption[];
}

export interface QuizAttempt {
  id: number;
  quizId: number;
  userId: number;
  status: 'IN_PROGRESS' | 'COMPLETED';
  score?: number;
  totalQuestions?: number;
  correctAnswers?: number;
  passed?: boolean;
  startedAt?: string;
  completedAt?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface SubmitQuizAnswer {
  questionId: number;
  optionId: number;
}

export interface SubmitQuizRequest {
  answers: SubmitQuizAnswer[];
}

export interface Certificate {
  id: number;
  certificateNumber: string;
  userId: number;
  courseId: number;
  courseTitle: string;
  studentName: string;
  issuedAt: string;
}

export interface CourseLesson {

  id: number;

  moduleId: number;

  title: string;

  orderIndex: number;
}