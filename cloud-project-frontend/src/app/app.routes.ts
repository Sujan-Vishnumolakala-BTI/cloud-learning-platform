import { Routes } from '@angular/router';

import {
  authGuard,
  guestGuard,
  roleGuard,
} from './core/guards/auth.guard';
import { SkillsComponent } from './features/skills/skills.component';

export const routes: Routes = [

  // =========================
  // LANDING
  // =========================

  {
    path: '',
    loadComponent: () =>
      import('./features/landing/landing.component')
        .then(m => m.LandingComponent),
    title: 'CloudPath — Master Cloud & DevOps Skills',
  },


  // =========================
  // LOGIN
  // =========================

  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component')
        .then(m => m.LoginComponent),
    canActivate: [guestGuard],
    title: 'Log in — CloudPath',
  },


  // =========================
  // REGISTER
  // =========================

  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component')
        .then(m => m.RegisterComponent),
    canActivate: [guestGuard],
    title: 'Create account — CloudPath',
  },


  // =========================
  // VERIFY OTP
  // =========================

  {
    path: 'verify-otp',
    loadComponent: () =>
      import('./features/auth/verify-otp/verify-otp.component')
        .then(m => m.VerifyOtpComponent),
    title: 'Verify email — CloudPath',
  },


  // =========================
  // STUDENT DASHBOARD
  // =========================

  {
    path: 'dashboard',
    loadComponent: () =>
      import('./features/dashboard/dashboard.component')
        .then(m => m.DashboardComponent),

    canActivate: [
      authGuard,
      roleGuard('student'),
    ],

    title: 'Dashboard — CloudPath',
  },


  // =========================
  // COURSE DETAILS
  // /courses/2
  // =========================




  // =========================
  // COURSE LEARNING
  // /courses/2/learn
  // =========================




  // =========================
  // ADMIN
  // =========================

  {
    path: 'admin',

    loadComponent: () =>
      import('./features/admin/admin.component')
        .then(m => m.AdminComponent),

    canActivate: [
      authGuard,
      roleGuard('admin'),
    ],

    title: 'Admin Dashboard — CloudPath',
  },


  // =========================
  // INSTRUCTOR
  // =========================

  {
    path: 'instructor',

    loadComponent: () =>
      import('./features/instructor/instructor.component')
        .then(m => m.InstructorComponent),

    canActivate: [
      authGuard,
      roleGuard('instructor'),
    ],

    title: 'Instructor Dashboard — CloudPath',
  },


  // =========================
  // ABOUT
  // =========================

  {
    path: 'about',

    loadComponent: () =>
      import('./features/about/about.component')
        .then(m => m.AboutComponent),

    title: 'About — CloudPath',
  },


  // =========================
  // CONTACT
  // =========================

  {
    path: 'contact',

    loadComponent: () =>
      import('./features/contact/contact.component')
        .then(m => m.ContactComponent),

    title: 'Contact — CloudPath',
  },
  {
    path: 'courses/:id',
    loadComponent: () =>
      import('./features/course-detail/course-detail.component')
        .then(m => m.CourseDetailComponent),
    canActivate: [authGuard],
    title: 'Course Details — CloudPath',
  },

  {
    path: 'courses/:id/learn',
    loadComponent: () =>
      import('./features/course-learning/course-learning.component')
        .then(m => m.CourseLearningComponent),
    canActivate: [
      authGuard,
      roleGuard('student'),
    ],
    title: 'Course Learning — CloudPath',
  },

  {
    path: 'courses/:courseId/lessons/:lessonId/quiz',
    loadComponent: () =>
      import('./features/quiz/quiz.component')
        .then(m => m.QuizComponent),

    canActivate: [
      authGuard,
      roleGuard('student'),
    ],

    title: 'Quiz — CloudPath',
  },

  {
    path: 'instructor/courses/create',

    loadComponent: () =>
      import(
        './features/create-course/create-course.component'
      ).then(
        m => m.CreateCourseComponent
      ),

    canActivate: [
      authGuard,
      roleGuard('instructor'),
    ],

    title: 'Create Course — CloudPath',
  },

  {
    path: 'instructor/courses/:courseId/modules',
    loadComponent: () =>
      import(
        './features/instructor-modules/instructor-modules.component'
      ).then(
        m => m.InstructorModulesComponent
      ),
    canActivate: [
      authGuard,
      roleGuard('instructor'),
    ],
  },
  {
    path: 'instructor/courses/:courseId/lessons',

    loadComponent: () =>
      import(
        './features/instructor-lessons/instructor-lessons.component'
      ).then(
        m => m.InstructorLessonsComponent
      ),

    canActivate: [
      authGuard,
      roleGuard('instructor')
    ],

    title: 'Manage Lessons — CloudPath'
  },

  {
    path:
      'instructor/courses/:courseId/quizzes/:quizId/questions',

    loadComponent: () =>
      import(
        './features/instructor-questions/instructor-questions.component'
      ).then(
        m =>
          m.InstructorQuestionsComponent
      ),

    canActivate: [
      authGuard,
      roleGuard('instructor')
    ]
  },

  {
    path: 'instructor/courses/:courseId/quizzes',
    loadComponent: () =>
      import(
        './features/instructor-quizzes/instructor-quizzes.component'
      ).then(
        m => m.InstructorQuizzesComponent
      ),
    canActivate: [
      authGuard,
      roleGuard('instructor'),
    ],
    title: 'Manage Quizzes — CloudPath',
  },
  {
    path: 'instructor/courses/:courseId',
    loadComponent: () =>
      import(
        './features/instructor-course/instructor-course.component'
      ).then(
        m => m.InstructorCourseComponent
      ),
  },

  {
    path: 'admin/courses',

    loadComponent: () =>
      import(
        './features/admin-courses/admin-courses.component'
      ).then(
        m => m.AdminCoursesComponent
      ),

    canActivate: [
      authGuard,
      roleGuard('admin')
    ]
  },

  {
    path: 'admin/courses/:id',

    loadComponent: () =>
      import(
        './features/admin-course-edit/admin-course-edit.component'
      ).then(
        m => m.AdminCourseEditComponent
      ),

    canActivate: [
      authGuard,
      roleGuard('admin')
    ],

    title: 'Edit Course — CloudPath'
  },

  {
    path: 'skill-assessment',

    loadComponent: () =>
      import(
        './features/skill-assessment/skill-assessment.component'
      ).then(
        m => m.SkillAssessmentComponent
      ),

    canActivate: [
      authGuard,
    ],

    title: 'Skill Assessment — CloudPath',
  },

  {
    path: 'skills',
    component: SkillsComponent
  },


  // =========================
  // 404
  // =========================

  {
    path: '**',

    loadComponent: () =>
      import('./shared/components/not-found/not-found.component')
        .then(m => m.NotFoundComponent),

    title: 'Page not found — CloudPath',
  },


];