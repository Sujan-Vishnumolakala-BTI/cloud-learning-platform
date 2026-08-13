import {
  Component,
  OnInit,
  inject,
  signal
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  Router
} from '@angular/router';

import { Observable } from 'rxjs';

import {
  CourseService
} from '../../core/services/course.service';

import {
  Course
} from '../../core/models/course.model';

@Component({
  selector: 'app-admin-courses',
  standalone: true,

  imports: [
    CommonModule
  ],

  templateUrl:
    './admin-courses.component.html'
})
export class AdminCoursesComponent
  implements OnInit {

  private readonly courseService =
    inject(CourseService);

  private readonly router =
    inject(Router);

  readonly courses =
    signal<Course[]>([]);

  readonly loading =
    signal(true);

  readonly error =
    signal<string | null>(null);

  readonly actionLoading =
    signal<number | null>(null);

  readonly success =
    signal<string | null>(null);


  ngOnInit(): void {

    this.loadCourses();

  }


  loadCourses(): void {

    this.loading.set(true);

    this.error.set(null);

    this.courseService
      .getCourses()
      .subscribe({

        next: courses => {

          console.log(
            'ADMIN COURSES:',
            courses
          );

          this.courses.set(courses);

          this.loading.set(false);

        },

        error: error => {

          console.error(
            'ADMIN COURSE LOAD ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to load courses.'
          );

          this.loading.set(false);

        }

      });

  }


  editCourse(
    course: Course
  ): void {

    this.router.navigate([
      '/admin/courses',
      course.id
    ]);

  }


  publishCourse(
    course: Course
  ): void {

    this.runCourseAction(
      course.id,
      'publish'
    );

  }


  unpublishCourse(
    course: Course
  ): void {

    this.runCourseAction(
      course.id,
      'unpublish'
    );

  }


  activateCourse(
    course: Course
  ): void {

    this.runCourseAction(
      course.id,
      'activate'
    );

  }


  deactivateCourse(
    course: Course
  ): void {

    this.runCourseAction(
      course.id,
      'deactivate'
    );

  }


  deleteCourse(
    course: Course
  ): void {

    const confirmed =
      window.confirm(
        `Are you sure you want to delete "${course.title}"?`
      );

    if (!confirmed) {
      return;
    }

    this.actionLoading.set(
      course.id
    );

    this.error.set(null);

    this.success.set(null);

    this.courseService
      .deleteCourse(course.id)
      .subscribe({

        next: () => {

          this.success.set(
            'Course deleted successfully.'
          );

          this.actionLoading.set(null);

          this.loadCourses();

        },

        error: error => {

          console.error(
            'ADMIN DELETE COURSE ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to delete course.'
          );

          this.actionLoading.set(null);

        }

      });

  }


  private runCourseAction(
    courseId: number,
    action:
      'publish'
      | 'unpublish'
      | 'activate'
      | 'deactivate'
  ): void {

    this.actionLoading.set(
      courseId
    );

    this.error.set(null);

    this.success.set(null);


    let request$: Observable<Course>;


    switch (action) {

      case 'publish':

        request$ =
          this.courseService
            .publishCourse(courseId);

        break;


      case 'unpublish':

        request$ =
          this.courseService
            .unpublishCourse(courseId);

        break;


      case 'activate':

        request$ =
          this.courseService
            .activateCourse(courseId);

        break;


      case 'deactivate':

        request$ =
          this.courseService
            .deactivateCourse(courseId);

        break;

    }


    request$.subscribe({

      next: updatedCourse => {

        this.success.set(
          `Course ${action}ed successfully.`
        );

        this.actionLoading.set(null);

        this.courses.update(
          courses =>
            courses.map(course =>
              course.id === updatedCourse.id
                ? updatedCourse
                : course
            )
        );

      },

      error: error => {

        console.error(
          `ADMIN ${action.toUpperCase()} COURSE ERROR:`,
          error
        );

        this.error.set(
          error?.error?.message ??
          `Unable to ${action} course.`
        );

        this.actionLoading.set(null);

      }

    });

  }


  back(): void {

    this.router.navigate([
      '/admin'
    ]);

  }

}