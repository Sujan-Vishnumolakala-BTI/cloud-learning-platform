// import { Injectable, inject } from '@angular/core';
// import { HttpClient, HttpParams } from '@angular/common/http';
// import { Observable, of } from 'rxjs';

// import {
//   ActivityItem,
//   Course,
//   CourseProgress,
//   EnrolledCourse,
//   Lesson,
//   LessonProgress,
//   Module,
//   Testimonial,
// } from '../models/course.model';

// export interface PageResponse<T> {
//   content: T[];
//   page: number;
//   size: number;
//   totalElements: number;
//   totalPages: number;
//   first: boolean;
//   last: boolean;
// }

// @Injectable({
//   providedIn: 'root',
// })
// export class CourseService {

//   private readonly http = inject(HttpClient);

//   // =========================
//   // COURSE SERVICE
//   // =========================

//   private readonly courseApiUrl =
//     'http://localhost:8080/api';

//   // =========================
//   // PROGRESS SERVICE
//   // =========================

//   private readonly progressApiUrl =
//     'http://localhost:8080/api/progress';

//   // =========================================================
//   // COURSES
//   // =========================================================

//   /**
//    * GET ALL COURSES
//    *
//    * GET /api/courses
//    */
//   getCourses(): Observable<Course[]> {

//     console.log(
//       'CourseService: GET courses',
//       `${this.courseApiUrl}/courses`
//     );

//     return this.http.get<Course[]>(
//       `${this.courseApiUrl}/courses`
//     );
//   }

//   /**
//    * GET FEATURED COURSES
//    */
//   getFeaturedCourses(
//     limit = 3
//   ): Observable<Course[]> {

//     return new Observable<Course[]>(subscriber => {

//       this.getCourses().subscribe({

//         next: courses => {

//           subscriber.next(
//             courses.slice(0, limit)
//           );

//           subscriber.complete();
//         },

//         error: error => {
//           subscriber.error(error);
//         },

//       });

//     });
//   }

//   /**
//    * GET COURSE BY ID
//    *
//    * GET /api/courses/{id}
//    */
//   getCourseById(
//     id: number
//   ): Observable<Course> {

//     console.log(
//       'CourseService: GET course',
//       `${this.courseApiUrl}/courses/${id}`
//     );

//     return this.http.get<Course>(
//       `${this.courseApiUrl}/courses/${id}`
//     );
//   }

//   /**
//    * SEARCH COURSES
//    *
//    * GET /api/courses/search
//    */
//   searchCourses(
//     title?: string,
//     category?: string
//   ): Observable<Course[]> {

//     let params = new HttpParams();

//     if (title?.trim()) {

//       params = params.set(
//         'title',
//         title.trim()
//       );
//     }

//     if (category?.trim()) {

//       params = params.set(
//         'category',
//         category.trim()
//       );
//     }

//     return this.http.get<Course[]>(
//       `${this.courseApiUrl}/courses/search`,
//       { params }
//     );
//   }

//   /**
//    * PAGINATED COURSES
//    *
//    * GET /api/courses/paged
//    */
//   getCoursesPaged(
//     page = 0,
//     size = 10,
//     sortBy = 'createdAt',
//     direction = 'desc'
//   ): Observable<PageResponse<Course>> {

//     const params = new HttpParams()
//       .set('page', page)
//       .set('size', size)
//       .set('sortBy', sortBy)
//       .set('direction', direction);

//     return this.http.get<PageResponse<Course>>(
//       `${this.courseApiUrl}/courses/paged`,
//       { params }
//     );
//   }

//   /**
//    * PAGINATED SEARCH
//    *
//    * GET /api/courses/search/paged
//    */
//   searchCoursesPaged(
//     title?: string,
//     category?: string,
//     page = 0,
//     size = 10,
//     sortBy = 'createdAt',
//     direction = 'desc'
//   ): Observable<PageResponse<Course>> {

//     let params = new HttpParams()
//       .set('page', page)
//       .set('size', size)
//       .set('sortBy', sortBy)
//       .set('direction', direction);

//     if (title?.trim()) {

//       params = params.set(
//         'title',
//         title.trim()
//       );
//     }

//     if (category?.trim()) {

//       params = params.set(
//         'category',
//         category.trim()
//       );
//     }

//     return this.http.get<PageResponse<Course>>(
//       `${this.courseApiUrl}/courses/search/paged`,
//       { params }
//     );
//   }

//   // =========================================================
//   // MODULES
//   // =========================================================

//   /**
//    * GET MODULES FOR COURSE
//    *
//    * GET /api/courses/{courseId}/modules
//    */
//   getModulesByCourse(
//     courseId: number
//   ): Observable<Module[]> {

//     console.log(
//       'CourseService: GET modules',
//       `${this.courseApiUrl}/courses/${courseId}/modules`
//     );

//     return this.http.get<Module[]>(
//       `${this.courseApiUrl}/courses/${courseId}/modules`
//     );
//   }

//   // =========================================================
//   // LESSONS
//   // =========================================================

//   /**
//    * GET LESSONS FOR MODULE
//    *
//    * GET /api/modules/{moduleId}/lessons
//    */
//   getLessonsByModule(
//     moduleId: number
//   ): Observable<Lesson[]> {

//     console.log(
//       'CourseService: GET lessons',
//       `${this.courseApiUrl}/modules/${moduleId}/lessons`
//     );

//     return this.http.get<Lesson[]>(
//       `${this.courseApiUrl}/modules/${moduleId}/lessons`
//     );
//   }

//   /**
//    * GET ALL LESSONS FOR COURSE
//    *
//    * GET /api/courses/{courseId}/lessons
//    */
//   getLessonsByCourse(
//     courseId: number
//   ): Observable<Lesson[]> {

//     console.log(
//       'CourseService: GET course lessons',
//       `${this.courseApiUrl}/courses/${courseId}/lessons`
//     );

//     return this.http.get<Lesson[]>(
//       `${this.courseApiUrl}/courses/${courseId}/lessons`
//     );
//   }

//   // =========================================================
//   // PROGRESS
//   // =========================================================

//   /**
//    * GET COURSE PROGRESS
//    *
//    * GET /api/progress/courses/{courseId}
//    */
//   getCourseProgress(
//     courseId: number
//   ): Observable<CourseProgress> {

//     console.log(
//       'CourseService: GET progress',
//       `${this.progressApiUrl}/courses/${courseId}`
//     );

//     return this.http.get<CourseProgress>(
//       `${this.progressApiUrl}/courses/${courseId}`
//     );
//   }

//   /**
//    * START LESSON
//    *
//    * POST /api/progress/lessons/{lessonId}/start
//    */
//   startLesson(
//     lessonId: number
//   ): Observable<LessonProgress> {

//     console.log(
//       'CourseService: START lesson',
//       `${this.progressApiUrl}/lessons/${lessonId}/start`
//     );

//     return this.http.post<LessonProgress>(
//       `${this.progressApiUrl}/lessons/${lessonId}/start`,
//       {}
//     );
//   }

//   /**
//    * COMPLETE LESSON
//    *
//    * POST /api/progress/lessons/{lessonId}/complete
//    */
//   completeLesson(
//     lessonId: number
//   ): Observable<LessonProgress> {

//     console.log(
//       'CourseService: COMPLETE lesson',
//       `${this.progressApiUrl}/lessons/${lessonId}/complete`
//     );

//     return this.http.post<LessonProgress>(
//       `${this.progressApiUrl}/lessons/${lessonId}/complete`,
//       {}
//     );
//   }

//   /**
//    * GET MY LESSON PROGRESS
//    *
//    * GET /api/progress/my
//    */
//   getMyProgress(): Observable<LessonProgress[]> {

//     console.log(
//       'CourseService: GET my progress',
//       `${this.progressApiUrl}/my`
//     );

//     return this.http.get<LessonProgress[]>(
//       `${this.progressApiUrl}/my`
//     );
//   }

//   // =========================================================
//   // DASHBOARD - TEMPORARY
//   // =========================================================

//   getEnrolledCourses(
//     _enrolledIds: string[],
//     _completedIds: string[]
//   ): Observable<EnrolledCourse[]> {

//     return of([]);
//   }

//   getRecommendedCourses(
//     _excludeIds: string[]
//   ): Observable<Course[]> {

//     return this.getCourses();
//   }

//   getTestimonials(): Observable<Testimonial[]> {

//     return of([]);
//   }

//   getRecentActivity(): Observable<ActivityItem[]> {

//     return of([]);
//   }
// }

import { Injectable, inject } from '@angular/core';
import {
  HttpClient,
  HttpParams
} from '@angular/common/http';

import {
  Observable,
  of,
  forkJoin
} from 'rxjs';

import {
  ActivityItem,
  Course,
  CourseProgress,
  EnrolledCourse,
  LessonProgress,
  Quiz,
  QuizAttempt,
  QuizOption,
  QuizQuestion,
  SubmitQuizRequest,
  Testimonial,
  Certificate,
} from '../models/course.model';

import { Lesson } from '../models/lesson.model';

import { CourseLesson } from '../models/course-lesson.model';

import {
  Module,
  CreateModuleRequest,
  UpdateModuleRequest
} from '../models/module.model';
// import { Question, QuestionOption } from '../models/question.model';

import {
  Question,
  QuestionOption,
  CreateQuestionRequest,
  CreateQuestionOptionRequest
} from '../models/question.model';
// import { Lesson } from '../models/lesson.model';

export interface PageResponse<T> {

  content: T[];

  page: number;

  size: number;

  totalElements: number;

  totalPages: number;

  first: boolean;

  last: boolean;
}

export interface InstructorStudent {
  userId: number;
  courseId: number;
  status: string;
  enrolledAt: string;
  completedAt: string | null;
}

export interface InstructorCourseProgress {
  userId: number;
  courseId: number;
  totalLessons: number;
  completedLessons: number;
  progressPercentage: number;
  status: string;
}


@Injectable({
  providedIn: 'root'
})
export class CourseService {

  private readonly http =
    inject(HttpClient);


  /*
   * ==========================================
   * COURSE SERVICE
   * ==========================================
   */

  private readonly courseApiUrl =
    'http://localhost:8080/api/courses';

  private readonly apiUrl =
    'http://localhost:8080/api';

  /*
   * ==========================================
   * ENROLLMENT / PROGRESS SERVICE
   * ==========================================
   */

  private readonly progressApiUrl =
    'http://localhost:8080/api/progress';

  private readonly quizApiUrl =
    'http://localhost:8080/api';

  private readonly quizAttemptApiUrl =
    'http://localhost:8080/api/quiz-attempts';

  private readonly API_URL = 'http://localhost:8080/api';

  /*
   * ==========================================
   * GET ALL COURSES
   * ==========================================
   */

  getCourses(): Observable<Course[]> {

    console.log(
      'CourseService: GET all courses',
      this.courseApiUrl
    );

    return this.http.get<Course[]>(
      this.courseApiUrl
    );
  }


  /*
   * ==========================================
   * FEATURED COURSES
   * ==========================================
   */

  getFeaturedCourses(
    limit = 3
  ): Observable<Course[]> {

    return new Observable<Course[]>(
      subscriber => {

        this.getCourses().subscribe({

          next: courses => {

            subscriber.next(
              courses.slice(0, limit)
            );

            subscriber.complete();
          },

          error: error => {

            subscriber.error(error);
          }

        });

      }
    );
  }


  /*
   * ==========================================
   * GET COURSE BY ID
   *
   * GET /api/courses/{id}
   * ==========================================
   */

  getCourseById(
    id: number
  ): Observable<Course> {

    const url =
      `${this.courseApiUrl}/${id}`;

    console.log(
      'CourseService: GET course',
      url
    );

    return this.http.get<Course>(
      url
    );
  }


  /*
   * ==========================================
   * SEARCH COURSES
   *
   * GET /api/courses/search
   * ==========================================
   */

  searchCourses(
    title?: string,
    category?: string
  ): Observable<Course[]> {

    let params =
      new HttpParams();

    if (title?.trim()) {

      params = params.set(
        'title',
        title.trim()
      );
    }

    if (category?.trim()) {

      params = params.set(
        'category',
        category.trim()
      );
    }

    return this.http.get<Course[]>(
      `${this.courseApiUrl}/search`,
      {
        params
      }
    );
  }


  /*
   * ==========================================
   * PAGINATED COURSES
   *
   * GET /api/courses/paged
   * ==========================================
   */

  getCoursesPaged(
    page = 0,
    size = 10,
    sortBy = 'createdAt',
    direction = 'desc'
  ): Observable<PageResponse<Course>> {

    const params =
      new HttpParams()
        .set('page', page)
        .set('size', size)
        .set('sortBy', sortBy)
        .set('direction', direction);

    return this.http.get<PageResponse<Course>>(
      `${this.courseApiUrl}/paged`,
      {
        params
      }
    );
  }


  /*
   * ==========================================
   * PAGINATED SEARCH
   *
   * GET /api/courses/search/paged
   * ==========================================
   */

  searchCoursesPaged(
    title?: string,
    category?: string,
    page = 0,
    size = 10,
    sortBy = 'createdAt',
    direction = 'desc'
  ): Observable<PageResponse<Course>> {

    let params =
      new HttpParams()
        .set('page', page)
        .set('size', size)
        .set('sortBy', sortBy)
        .set('direction', direction);

    if (title?.trim()) {

      params = params.set(
        'title',
        title.trim()
      );
    }

    if (category?.trim()) {

      params = params.set(
        'category',
        category.trim()
      );
    }

    return this.http.get<PageResponse<Course>>(
      `${this.courseApiUrl}/search/paged`,
      {
        params
      }
    );
  }


  /*
   * ==========================================
   * ENROLLED COURSES
   *
   * Kept temporarily because dashboard
   * may still use this method.
   * ==========================================
   */

  getEnrolledCourses(
    _enrolledIds: string[],
    _completedIds: string[]
  ): Observable<EnrolledCourse[]> {

    return of([]);
  }


  /*
   * ==========================================
   * RECOMMENDED COURSES
   * ==========================================
   */

  getRecommendedCourses(
    _excludeIds: string[]
  ): Observable<Course[]> {

    return this.getCourses();
  }


  /*
   * ==========================================
   * TESTIMONIALS
   * ==========================================
   */

  getTestimonials():
    Observable<Testimonial[]> {

    return of([]);
  }


  /*
   * ==========================================
   * RECENT ACTIVITY
   * ==========================================
   */

  getRecentActivity():
    Observable<ActivityItem[]> {

    return of([]);
  }


  /*
   * ==========================================
   * MODULES
   *
   * GET
   * /api/courses/{courseId}/modules
   *
   * Course Service = 8080
   * ==========================================
   */

  // getModulesByCourse(
  //   courseId: number
  // ): Observable<Module[]> {

  //   const url =
  //     `${this.courseApiUrl}/${courseId}/modules`;

  //   console.log(
  //     'CourseService: GET modules',
  //     url
  //   );

  //   return this.http.get<Module[]>(
  //     url
  //   );
  // }


  /*
   * ==========================================
   * LESSONS BY MODULE
   *
   * GET
   * /api/modules/{moduleId}/lessons
   *
   * Course Service = 8080
   * ==========================================
   */

  getLessonsByModule(
    moduleId: number
  ): Observable<Lesson[]> {

    const url =
      `http://localhost:8080/api/modules/${moduleId}/lessons`;

    console.log(
      'CourseService: GET lessons',
      url
    );

    return this.http.get<Lesson[]>(
      url
    );
  }

  getLesson(
    lessonId: number
  ): Observable<Lesson> {

    return this.http.get<Lesson>(
      `${this.API_URL}/lessons/${lessonId}`
    );
  }

  getLessonsByCourse(
    courseId: number
  ): Observable<CourseLesson[]> {

    const url =
      `${this.API_URL}/courses/${courseId}/lessons`;

    console.log(
      'GET COURSE LESSONS:',
      url
    );

    return this.http.get<CourseLesson[]>(
      url
    );
  }

  // createLesson(
  //   courseId: number,
  //   request: {
  //     title: string;
  //     description: string;
  //     contentType: string;
  //     contentUrl: string;
  //     durationMinutes: number;
  //     orderIndex: number;
  //   }
  // ) {

  //   return this.http.post<Lesson>(
  //     `${this.API_URL}/courses/${courseId}/lessons`,
  //     request
  //   );
  // }

  createLesson(
    moduleId: number,
    request: {
      title: string;
      description: string;
      contentType: string;
      contentUrl: string;
      durationMinutes: number;
      orderIndex: number;
    }
  ): Observable<Lesson> {

    const url =
      `${this.apiUrl}/modules/${moduleId}/lessons`;

    console.log(
      'CourseService: CREATE LESSON',
      url,
      request
    );

    return this.http.post<Lesson>(
      url,
      request
    );
  }


  updateLesson(
    lessonId: number,
    request: {
      title: string;
      description: string;
      contentType: string;
      contentUrl: string;
      durationMinutes: number;
      orderIndex: number;
    }
  ) {

    return this.http.put<Lesson>(
      `${this.API_URL}/lessons/${lessonId}`,
      request
    );
  }


  deleteLesson(
    lessonId: number
  ) {

    return this.http.delete<void>(
      `${this.API_URL}/lessons/${lessonId}`
    );
  }
  /*
   * ==========================================
   * COURSE PROGRESS
   *
   * GET
   * /api/progress/courses/{courseId}
   *
   * Enrollment Service = 8080
   * ==========================================
   */

  getCourseProgress(
    courseId: number
  ): Observable<CourseProgress> {

    const url =
      `${this.progressApiUrl}/courses/${courseId}`;

    console.log(
      'CourseService: GET course progress',
      url
    );

    return this.http.get<CourseProgress>(
      url
    );
  }


  /*
   * ==========================================
   * START LESSON
   *
   * POST
   * /api/progress/lessons/{lessonId}/start
   *
   * Enrollment Service = 8080
   * ==========================================
   */

  startLesson(
    lessonId: number
  ): Observable<LessonProgress> {

    const url =
      `${this.progressApiUrl}/lessons/${lessonId}/start`;

    console.log(
      'CourseService: START lesson',
      url
    );

    return this.http.post<LessonProgress>(
      url,
      {}
    );
  }


  /*
   * ==========================================
   * COMPLETE LESSON
   *
   * POST
   * /api/progress/lessons/{lessonId}/complete
   *
   * Enrollment Service = 8080
   * ==========================================
   */

  completeLesson(
    lessonId: number
  ): Observable<LessonProgress> {

    const url =
      `${this.progressApiUrl}/lessons/${lessonId}/complete`;

    console.log(
      'CourseService: COMPLETE lesson',
      url
    );

    return this.http.post<LessonProgress>(
      url,
      {}
    );
  }

  // getModulesByCourse(
  //   courseId: number
  // ): Observable<Module[]> {

  //   console.log(
  //     'CourseService: GET modules',
  //     `http://localhost:8080/api/courses/${courseId}/modules`
  //   );

  //   return this.http.get<Module[]>(
  //     `${this.courseApiUrl}/${courseId}/modules`
  //   );
  // }

  getMyLessonProgress(): Observable<LessonProgress[]> {

    console.log(
      'CourseService: GET my lesson progress',
      `${this.progressApiUrl}/my`
    );

    return this.http.get<LessonProgress[]>(
      `${this.progressApiUrl}/my`
    );
  }

  /*
 * ==========================================
 * QUIZ
 * ==========================================
 */

  /*
   * GET QUIZ FOR LESSON
   *
   * Course Service:
   * GET /api/lessons/{lessonId}/quiz
   */
  getQuizByLesson(
    lessonId: number
  ): Observable<Quiz> {

    console.log(
      'CourseService: GET quiz',
      `${this.quizApiUrl}/lessons/${lessonId}/quiz`
    );

    return this.http.get<Quiz>(
      `${this.quizApiUrl}/lessons/${lessonId}/quiz`
    );
  }


  /*
   * GET QUIZ BY ID
   *
   * Course Service:
   * GET /api/quizzes/{quizId}
   */
  getQuizById(
    quizId: number
  ): Observable<Quiz> {

    return this.http.get<Quiz>(
      `${this.quizApiUrl}/quizzes/${quizId}`
    );
  }


  /*
   * GET QUESTIONS
   *
   * Course Service:
   * GET /api/quizzes/{quizId}/questions
   */
  getQuizQuestions(
    quizId: number
  ): Observable<QuizQuestion[]> {

    console.log(
      'CourseService: GET questions',
      `${this.quizApiUrl}/quizzes/${quizId}/questions`
    );

    return this.http.get<QuizQuestion[]>(
      `${this.quizApiUrl}/quizzes/${quizId}/questions`
    );
  }


  /*
   * GET OPTIONS
   *
   * Course Service:
   * GET /api/questions/{questionId}/options
   */
  // getQuestionOptions(
  //   questionId: number
  // ): Observable<QuizOption[]> {

  //   return this.http.get<QuizOption[]>(
  //     `${this.quizApiUrl}/questions/${questionId}/options`
  //   );
  // }


  /*
   * START QUIZ ATTEMPT
   *
   * Enrollment Service:
   * POST /api/quiz-attempts
   *
   * Body:
   * {
   *   quizId: 1
   * }
   */
  startQuizAttempt(
    quizId: number
  ): Observable<QuizAttempt> {

    console.log(
      'CourseService: START QUIZ',
      this.quizAttemptApiUrl
    );

    return this.http.post<QuizAttempt>(
      this.quizAttemptApiUrl,
      {
        quizId
      }
    );
  }


  /*
   * SUBMIT QUIZ
   *
   * Enrollment Service:
   * POST /api/quiz-attempts/{attemptId}/submit
   */
  submitQuizAttempt(
    attemptId: number,
    request: SubmitQuizRequest
  ): Observable<QuizAttempt> {

    console.log(
      'CourseService: SUBMIT QUIZ',
      `${this.quizAttemptApiUrl}/${attemptId}/submit`
    );

    return this.http.post<QuizAttempt>(
      `${this.quizAttemptApiUrl}/${attemptId}/submit`,
      request
    );
  }


  /*
   * GET MY QUIZ ATTEMPTS
   *
   * Enrollment Service:
   * GET /api/quiz-attempts/quiz/{quizId}/my
   */
  getMyQuizAttempts(
    quizId: number
  ): Observable<QuizAttempt[]> {

    return this.http.get<QuizAttempt[]>(
      `${this.quizAttemptApiUrl}/quiz/${quizId}/my`
    );
  }

  getQuizzesByCourse(
    courseId: number
  ) {

    return this.http.get<Quiz[]>(
      `${this.API_URL}/courses/${courseId}/quizzes`
    );
  }


  createQuiz(
    lessonId: number,
    request: {
      title: string;
      description: string;
      passingScore: number;
    }
  ): Observable<Quiz> {

    const url =
      `${this.apiUrl}/lessons/${lessonId}/quiz`;

    console.log(
      'CREATE QUIZ URL:',
      url
    );

    console.log(
      'CREATE QUIZ REQUEST:',
      request
    );

    return this.http.post<Quiz>(
      url,
      request
    );
  }


  updateQuiz(
    quizId: number,
    request: {
      lessonId: number;
      title: string;
      description: string;
      passingScore: number;
    }
  ) {

    return this.http.put<Quiz>(
      `${this.API_URL}/quizzes/${quizId}`,
      request
    );
  }


  deleteQuiz(
    quizId: number
  ) {

    return this.http.delete<void>(
      `${this.API_URL}/quizzes/${quizId}`
    );
  }

  /**
   * GET MY CERTIFICATE
   *
   * Backend:
   * GET /api/certificates/courses/{courseId}
   */
  getCertificate(
    courseId: number
  ): Observable<Certificate> {

    console.log(
      'CourseService: GET certificate',
      `http://localhost:8080/api/certificates/courses/${courseId}`
    );

    return this.http.get<Certificate>(
      `http://localhost:8080/api/certificates/courses/${courseId}`
    );
  }


  /**
   * GENERATE / GET CERTIFICATE
   *
   * Backend:
   * POST /api/certificates/courses/{courseId}
   */
  generateCertificate(
    courseId: number
  ): Observable<Certificate> {

    console.log(
      'CourseService: GENERATE certificate',
      `http://localhost:8080/api/certificates/courses/${courseId}`
    );

    return this.http.post<Certificate>(
      `http://localhost:8080/api/certificates/courses/${courseId}`,
      {}
    );


  }


  /**
   * DOWNLOAD CERTIFICATE PDF
   *
   * Backend:
   * GET /api/certificates/courses/{courseId}/download
   */
  downloadCertificate(
    courseId: number
  ): Observable<Blob> {

    console.log(
      'CourseService: DOWNLOAD certificate',
      `http://localhost:8080/api/certificates/courses/${courseId}/download`
    );

    return this.http.get(
      `http://localhost:8080/api/certificates/courses/${courseId}/download`,
      {
        responseType: 'blob'
      }
    );
  }

  // =========================================================
  // INSTRUCTOR
  // =========================================================

  /**
   * GET COURSES OWNED BY CURRENT INSTRUCTOR
   *
   * GET /api/instructor/courses
   *
   * Course Service = 8080
   */
  getInstructorCourses(): Observable<Course[]> {

    const url =
      'http://localhost:8080/api/instructor/courses';

    console.log(
      'CourseService: GET instructor courses',
      url
    );

    return this.http.get<Course[]>(url);
  }


  /**
   * GET STUDENTS FOR INSTRUCTOR COURSE
   *
   * GET /api/instructor/courses/{courseId}/students
   */
  getInstructorCourseStudents(
    courseId: number
  ): Observable<InstructorStudent[]> {

    const url =
      `http://localhost:8080/api/instructor/courses/${courseId}/students`;

    console.log(
      'CourseService: GET instructor course students',
      url
    );

    return this.http.get<InstructorStudent[]>(url);
  }


  /**
   * GET STUDENT PROGRESS FOR INSTRUCTOR COURSE
   *
   * GET /api/instructor/courses/{courseId}/progress
   */
  getInstructorCourseProgress(
    courseId: number
  ): Observable<InstructorCourseProgress[]> {

    const url =
      `http://localhost:8080/api/instructor/courses/${courseId}/progress`;

    console.log(
      'CourseService: GET instructor course progress',
      url
    );

    return this.http.get<InstructorCourseProgress[]>(url);
  }

  // =========================================================
  // INSTRUCTOR COURSE MANAGEMENT
  // =========================================================

  publishCourse(
    courseId: number
  ): Observable<Course> {

    return this.http.post<Course>(
      `${this.courseApiUrl}/${courseId}/publish`,
      {}
    );
  }


  unpublishCourse(
    courseId: number
  ): Observable<Course> {

    return this.http.post<Course>(
      `${this.courseApiUrl}/${courseId}/unpublish`,
      {}
    );
  }


  activateCourse(
    courseId: number
  ): Observable<Course> {

    return this.http.post<Course>(
      `${this.courseApiUrl}/${courseId}/activate`,
      {}
    );
  }


  deactivateCourse(
    courseId: number
  ): Observable<Course> {

    return this.http.post<Course>(
      `${this.courseApiUrl}/${courseId}/deactivate`,
      {}
    );
  }


  // updateCourse(
  //   courseId: number,
  //   request: {
  //     title: string;
  //     description: string;
  //     category: string;
  //   }
  // ): Observable<Course> {

  //   return this.http.put<Course>(
  //     `${this.courseApiUrl}/${courseId}`,
  //     request
  //   );
  // }
  updateCourse(
    courseId: number,
    request: {
      title?: string;
      description?: string;
      category?: string;
      skills?: string[];
      published?: boolean;
      active?: boolean;
    }
  ): Observable<Course> {

    return this.http.put<Course>(
      `${this.courseApiUrl}/${courseId}`,
      request
    );
  }

  deleteCourse(
    courseId: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.courseApiUrl}/${courseId}`
    );
  }

  createCourse(
    request: {
      title: string;
      description: string;
      category: string;
      skills: string[];
      instructorId: number;
    }
  ): Observable<Course> {

    console.log(
      'CourseService: CREATE course',
      this.courseApiUrl,
      request
    );

    return this.http.post<Course>(
      this.courseApiUrl,
      request
    );
  }
  getModulesByCourse(courseId: number) {
    return this.http.get<Module[]>(
      `${this.API_URL}/courses/${courseId}/modules`
    );
  }

  createModule(
    courseId: number,
    request: CreateModuleRequest
  ) {
    return this.http.post<Module>(
      `${this.API_URL}/courses/${courseId}/modules`,
      request
    );
  }

  updateModule(
    moduleId: number,
    request: UpdateModuleRequest
  ) {
    return this.http.put<Module>(
      `${this.API_URL}/modules/${moduleId}`,
      request
    );
  }

  deleteModule(moduleId: number) {
    return this.http.delete<void>(
      `${this.API_URL}/modules/${moduleId}`
    );
  }

  // =========================================================
  // QUESTIONS
  // =========================================================

  // getQuizQuestions(
  //   quizId: number
  // ) {

  //   return this.http.get<Question[]>(
  //     `${this.API_URL}/quizzes/${quizId}/questions`
  //   );
  // }




  // =========================================================
  // CREATE QUESTION
  // =========================================================

  createQuestion(
    quizId: number,
    request: CreateQuestionRequest
  ) {

    const url =
      `${this.apiUrl}/quizzes/${quizId}/questions`;

    console.log(
      'CREATE QUESTION:',
      url,
      request
    );

    return this.http.post<Question>(
      url,
      request
    );
  }


  // =========================================================
  // GET OPTIONS
  // =========================================================

  getQuestionOptions(
    questionId: number
  ) {

    const url =
      `${this.apiUrl}/questions/${questionId}/options`;

    console.log(
      'GET OPTIONS:',
      url
    );

    return this.http.get<QuestionOption[]>(
      url
    );
  }


  getQuestionsByQuiz(
    quizId: number
  ) {

    const url =
      `${this.apiUrl}/quizzes/${quizId}/questions`;

    console.log(
      'GET QUESTIONS:',
      url
    );

    return this.http.get<Question[]>(
      url
    );
  }

  // =========================================================
  // CREATE OPTION
  // =========================================================

  createQuestionOption(
    questionId: number,
    request: CreateQuestionOptionRequest
  ) {

    const url =
      `${this.apiUrl}/questions/${questionId}/options`;

    console.log(
      'CREATE OPTION:',
      url,
      request
    );

    return this.http.post<QuestionOption>(
      url,
      request
    );
  }


}