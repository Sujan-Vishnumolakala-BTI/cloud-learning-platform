import {
  Component,
  OnInit,
  inject,
  signal
} from '@angular/core';

// import { ActivatedRoute } from '@angular/router';
import {
  ActivatedRoute,
  RouterLink,
} from '@angular/router';

import { CourseService } from '../../core/services/course.service';

import {
  Course,
  Module,
  CourseProgress,
  LessonProgress
} from '../../core/models/course.model';

import { Lesson } from '../../core/models/lesson.model';

@Component({
  selector: 'app-course-learning',
  standalone: true,
  imports: [
    RouterLink,
  ],
  templateUrl: './course-learning.component.html'
})
export class CourseLearningComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private courseService = inject(CourseService);

  // =========================
  // COURSE
  // =========================

  readonly course =
    signal<Course | null>(null);

  // =========================
  // MODULES
  // =========================

  readonly modules =
    signal<Module[]>([]);

  // =========================
  // LESSONS
  // =========================

  readonly lessons =
    signal<Record<number, Lesson[]>>({});

  // =========================
  // COURSE PROGRESS
  // =========================

  readonly progress =
    signal<CourseProgress | null>(null);

  // =========================
  // LESSON PROGRESS
  // =========================

  readonly lessonProgress =
    signal<LessonProgress[]>([]);

  // =========================
  // CURRENT LESSON
  // =========================

  readonly currentLessonId =
    signal<number | null>(null);

  // =========================
  // LOADING / ERROR
  // =========================

  readonly loading =
    signal(true);

  readonly error =
    signal<string | null>(null);

  // =========================
  // BUTTON STATES
  // =========================

  readonly startingLesson =
    signal<number | null>(null);

  readonly completingLesson =
    signal<number | null>(null);


  // =========================================================
  // INIT
  // =========================================================

  ngOnInit(): void {

    const courseId = Number(
      this.route.snapshot.paramMap.get('id')
    );

    console.log(
      '========== COURSE LEARNING =========='
    );

    console.log(
      'COURSE ID:',
      courseId
    );

    if (!courseId) {

      this.error.set(
        'Invalid course ID.'
      );

      this.loading.set(false);

      return;
    }

    this.loadCourse(courseId);

    this.loadLessonProgress();
  }


  // =========================================================
  // LOAD COURSE
  // =========================================================

  private loadCourse(
    courseId: number
  ): void {

    this.loading.set(true);

    this.courseService
      .getCourseById(courseId)
      .subscribe({

        next: course => {

          console.log(
            'COURSE:',
            course
          );

          this.course.set(course);

          this.loadModules(courseId);

          this.loadProgress(courseId);
        },

        error: error => {

          console.error(
            'COURSE LOAD ERROR:',
            error
          );

          this.error.set(
            'Unable to load course.'
          );

          this.loading.set(false);
        }

      });
  }


  // =========================================================
  // LOAD MODULES
  // =========================================================

  private loadModules(
    courseId: number
  ): void {

    this.courseService
      .getModulesByCourse(courseId)
      .subscribe({

        next: modules => {

          console.log(
            'MODULES:',
            modules
          );

          this.modules.set(modules);

          // Reset lessons
          this.lessons.set({});

          // Load lessons for every module
          modules.forEach(module => {

            this.loadLessons(
              module.id
            );

          });

          this.loading.set(false);
        },

        error: error => {

          console.error(
            'MODULE ERROR:',
            error
          );

          this.error.set(
            'Unable to load course modules.'
          );

          this.loading.set(false);
        }

      });
  }


  // =========================================================
  // LOAD LESSONS
  // =========================================================

  private loadLessons(
    moduleId: number
  ): void {

    this.courseService
      .getLessonsByModule(moduleId)
      .subscribe({

        next: lessons => {

          console.log(
            `LESSONS FOR MODULE ${moduleId}:`,
            lessons
          );

          this.lessons.update(
            current => ({

              ...current,

              [moduleId]: lessons

            })
          );

          /*
           * If there is no current lesson,
           * select the first lesson loaded.
           */
          if (
            this.currentLessonId() === null &&
            lessons.length > 0
          ) {

            this.currentLessonId.set(
              lessons[0].id
            );

          }

        },

        error: error => {

          console.error(
            'LESSON ERROR:',
            error
          );

        }

      });
  }


  // =========================================================
  // LOAD COURSE PROGRESS
  // =========================================================

  private loadProgress(
    courseId: number
  ): void {

    this.courseService
      .getCourseProgress(courseId)
      .subscribe({

        next: progress => {

          console.log(
            'COURSE PROGRESS:',
            progress
          );

          this.progress.set(
            progress
          );

        },

        error: error => {

          console.error(
            'PROGRESS ERROR:',
            error
          );

        }

      });
  }


  // =========================================================
  // LOAD MY LESSON PROGRESS
  // =========================================================

  private loadLessonProgress(): void {

    this.courseService
      .getMyLessonProgress()
      .subscribe({

        next: progress => {

          console.log(
            'MY LESSON PROGRESS:',
            progress
          );

          this.lessonProgress.set(
            progress
          );

        },

        error: error => {

          console.error(
            'LESSON PROGRESS ERROR:',
            error
          );

        }

      });
  }


  // =========================================================
  // GET LESSONS FOR MODULE
  // =========================================================

  getLessons(
    moduleId: number
  ): Lesson[] {

    return this.lessons()[moduleId] ?? [];
  }


  // =========================================================
  // GET ALL LESSONS IN COURSE
  // =========================================================

  getAllLessons(): Lesson[] {

    const allLessons: Lesson[] = [];

    for (
      const module of this.modules()
    ) {

      const moduleLessons =
        this.getLessons(module.id);

      allLessons.push(
        ...moduleLessons
      );
    }

    /*
     * Sort by module order,
     * then lesson order.
     */
    return allLessons.sort(
      (a, b) => {

        const moduleA =
          this.modules().find(
            m => m.id === a.moduleId
          );

        const moduleB =
          this.modules().find(
            m => m.id === b.moduleId
          );

        const moduleOrderDifference =
          (moduleA?.orderIndex ?? 0) -
          (moduleB?.orderIndex ?? 0);

        if (
          moduleOrderDifference !== 0
        ) {

          return moduleOrderDifference;
        }

        return (
          a.orderIndex -
          b.orderIndex
        );
      }
    );
  }


  // =========================================================
  // GET CURRENT LESSON
  // =========================================================

  getCurrentLesson(): Lesson | null {

    const currentId =
      this.currentLessonId();

    if (currentId === null) {

      return null;
    }

    return (
      this.getAllLessons()
        .find(
          lesson =>
            lesson.id === currentId
        ) ?? null
    );
  }


  // =========================================================
  // NEXT LESSON
  // =========================================================

  getNextLesson(
    lessonId: number
  ): Lesson | null {

    const lessons =
      this.getAllLessons();

    const index =
      lessons.findIndex(
        lesson =>
          lesson.id === lessonId
      );

    if (
      index === -1 ||
      index >= lessons.length - 1
    ) {

      return null;
    }

    return lessons[index + 1];
  }


  // =========================================================
  // PREVIOUS LESSON
  // =========================================================

  getPreviousLesson(
    lessonId: number
  ): Lesson | null {

    const lessons =
      this.getAllLessons();

    const index =
      lessons.findIndex(
        lesson =>
          lesson.id === lessonId
      );

    if (
      index <= 0
    ) {

      return null;
    }

    return lessons[index - 1];
  }


  // =========================================================
  // SELECT LESSON
  // =========================================================

  selectLesson(
    lesson: Lesson
  ): void {

    this.currentLessonId.set(
      lesson.id
    );

    console.log(
      'CURRENT LESSON:',
      lesson
    );
  }


  // =========================================================
  // GO TO NEXT LESSON
  // =========================================================

  goToNextLesson(
    lessonId: number
  ): void {

    const nextLesson =
      this.getNextLesson(
        lessonId
      );

    if (!nextLesson) {

      console.log(
        'No next lesson. Course finished.'
      );

      return;
    }

    this.currentLessonId.set(
      nextLesson.id
    );

    console.log(
      'NEXT LESSON:',
      nextLesson
    );
  }


  // =========================================================
  // GO TO PREVIOUS LESSON
  // =========================================================

  goToPreviousLesson(
    lessonId: number
  ): void {

    const previousLesson =
      this.getPreviousLesson(
        lessonId
      );

    if (!previousLesson) {

      return;
    }

    this.currentLessonId.set(
      previousLesson.id
    );

    console.log(
      'PREVIOUS LESSON:',
      previousLesson
    );
  }


  // =========================================================
  // LESSON STARTED?
  // =========================================================

  isLessonStarted(
    lessonId: number
  ): boolean {

    return this.lessonProgress()
      .some(

        progress =>

          progress.lessonId === lessonId &&

          (
            progress.status ===
              'IN_PROGRESS' ||

            progress.status ===
              'COMPLETED'
          )

      );
  }


  // =========================================================
  // LESSON COMPLETED?
  // =========================================================

  isLessonCompleted(
    lessonId: number
  ): boolean {

    return this.lessonProgress()
      .some(

        progress =>

          progress.lessonId === lessonId &&

          progress.status ===
            'COMPLETED'

      );
  }


  // =========================================================
  // START LESSON
  // =========================================================

  startLesson(
    lesson: Lesson
  ): void {

    this.currentLessonId.set(
      lesson.id
    );

    this.startingLesson.set(
      lesson.id
    );

    this.courseService
      .startLesson(
        lesson.id
      )
      .subscribe({

        next: response => {

          console.log(
            'LESSON STARTED:',
            response
          );

          this.startingLesson.set(
            null
          );

          this.loadLessonProgress();
        },

        error: error => {

          console.error(
            'START LESSON ERROR:',
            error
          );

          this.startingLesson.set(
            null
          );
        }

      });
  }


  // =========================================================
  // COMPLETE LESSON
  // =========================================================

  completeLesson(
    lesson: Lesson
  ): void {

    this.completingLesson.set(
      lesson.id
    );

    this.courseService
      .completeLesson(
        lesson.id
      )
      .subscribe({

        next: response => {

          console.log(
            'LESSON COMPLETED:',
            response
          );

          this.completingLesson.set(
            null
          );

          /*
           * Refresh lesson-level progress.
           */
          this.loadLessonProgress();

          /*
           * Refresh course-level progress.
           */
          const courseId =
            this.course()?.id;

          if (courseId) {

            this.loadProgress(
              courseId
            );
          }

        },

        error: error => {

          console.error(
            'COMPLETE LESSON ERROR:',
            error
          );

          this.completingLesson.set(
            null
          );
        }

      });
  }


  // =========================================================
  // COURSE COMPLETED?
  // =========================================================

  isCourseCompleted(): boolean {

    const p =
      this.progress();

    return (
      p !== null &&
      p.progressPercentage >= 100
    );
  }
}