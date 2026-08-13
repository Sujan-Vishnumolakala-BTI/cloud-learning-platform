import {
  Component,
  computed,
  inject,
  OnInit,
  signal,
} from '@angular/core';

import { CommonModule } from '@angular/common';
import {
  RouterLink,
} from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

import {
  Course,
} from '../../core/models/course.model';

import {
  CourseService,
  InstructorStudent,
  InstructorCourseProgress,
} from '../../core/services/course.service';

import { Router } from '@angular/router';

interface InstructorCourse extends Course {

  students: number;

  progress: number;

  status: string;

  studentProgress: InstructorCourseProgress[];

}

@Component({
  selector: 'app-instructor',
  standalone: true,
  imports: [
    CommonModule,
  ],
  templateUrl: './instructor.component.html',
})
export class InstructorComponent implements OnInit {

  private readonly auth =
    inject(AuthService);

  private readonly courseService =
    inject(CourseService);


  readonly currentUser =
    this.auth.currentUser;
  private router = inject(Router);


  readonly courses =
    signal<InstructorCourse[]>([]);


  readonly loading =
    signal(true);


  readonly error =
    signal<string | null>(null);


  readonly totalCourses =
    computed(() =>
      this.courses().length
    );


  readonly totalStudents =
    computed(() => {

      return this.courses()
        .reduce(
          (total, course) =>
            total + course.students,
          0
        );

    });


  readonly averageProgress =
    computed(() => {

      const courses =
        this.courses();

      if (courses.length === 0) {
        return 0;
      }

      const total =
        courses.reduce(
          (sum, course) =>
            sum + course.progress,
          0
        );

      return Math.round(
        total / courses.length
      );

    });


  ngOnInit(): void {

    this.loadInstructorDashboard();

  }


  loadInstructorDashboard(): void {

    this.loading.set(true);

    this.error.set(null);


    this.courseService
      .getInstructorCourses()
      .subscribe({

        next: courses => {

          console.log(
            'INSTRUCTOR COURSES:',
            courses
          );


          if (courses.length === 0) {

            this.courses.set([]);

            this.loading.set(false);

            return;

          }


          const requests =
            courses.map(course => {

              return this.courseService
                .getInstructorCourseProgress(
                  course.id
                );

            });


          import('rxjs').then(
            ({ forkJoin }) => {

              forkJoin(requests)
                .subscribe({

                  next: progressResults => {

                    console.log(
                      'INSTRUCTOR PROGRESS:',
                      progressResults
                    );


                    const instructorCourses =
                      courses.map(
                        (course, index) => {

                          const progress =
                            progressResults[index] ?? [];


                          const studentIds =
                            new Set(
                              progress.map(
                                item =>
                                  item.userId
                              )
                            );


                          const averageProgress =
                            progress.length === 0
                              ? 0
                              : Math.round(
                                progress.reduce(
                                  (
                                    sum,
                                    item
                                  ) =>
                                    sum +
                                    item.progressPercentage,
                                  0
                                ) /
                                progress.length
                              );


                          return {

                            ...course,

                            students:
                              studentIds.size,

                            progress:
                              averageProgress,

                            status:
                              course.published
                                ? 'Published'
                                : 'Draft',

                            studentProgress:
                              progress,

                          };

                        }
                      );


                    console.log(
                      'INSTRUCTOR DASHBOARD DATA:',
                      instructorCourses
                    );


                    this.courses.set(
                      instructorCourses
                    );

                    this.loading.set(false);

                  },


                  error: error => {

                    console.error(
                      'INSTRUCTOR PROGRESS ERROR:',
                      error
                    );

                    this.error.set(
                      'Unable to load instructor course progress.'
                    );

                    this.loading.set(false);

                  },

                });

            }
          );

        },


        error: error => {

          console.error(
            'INSTRUCTOR COURSES ERROR:',
            error
          );

          this.error.set(
            'Unable to load your courses.'
          );

          this.loading.set(false);

        },

      });

  }


  getCourseStatus(
    course: InstructorCourse
  ): string {

    if (!course.active) {
      return 'Inactive';
    }

    if (!course.published) {
      return 'Draft';
    }

    return 'Published';

  }


  getStatusClasses(
    course: InstructorCourse
  ): string {

    if (!course.active) {

      return 'bg-red-100 text-red-700';

    }

    if (!course.published) {

      return 'bg-yellow-100 text-yellow-700';

    }

    return 'bg-green-100 text-green-700';

  }

  createCourse(): void {
    this.router.navigate(['/instructor/courses/create']);
  }

  viewCourse(courseId: number): void {
    this.router.navigate([
      '/courses',
      courseId
    ]);
  }

  manageCourse(courseId: number): void {
    this.router.navigate([
      '/instructor/courses',
      courseId
    ]);
  }

}