import {
  CommonModule
} from '@angular/common';

import {
  Component,
  OnInit,
  inject,
  signal
} from '@angular/core';

import {
  FormsModule
} from '@angular/forms';

import {
  ActivatedRoute,
  Router
} from '@angular/router';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  CourseService
} from '../../core/services/course.service';

import {
  Course
} from '../../core/models/course.model';


@Component({
  selector: 'app-admin-course-edit',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl:
    './admin-course-edit.component.html'
})
export class AdminCourseEditComponent
  implements OnInit {

  private readonly route =
    inject(ActivatedRoute);

  private readonly router =
    inject(Router);

  private readonly courseService =
    inject(CourseService);


  readonly loading =
    signal(true);

  readonly saving =
    signal(false);

  readonly error =
    signal<string | null>(null);

  readonly success =
    signal<string | null>(null);


  courseId!: number;

  course: Course | null = null;


  title = '';

  description = '';

  category = '';

  published = false;

  active = true;


  ngOnInit(): void {

    const id =
      Number(
        this.route.snapshot.paramMap.get('id')
      );

    if (!id) {

      this.error.set(
        'Invalid course ID.'
      );

      this.loading.set(false);

      return;
    }

    this.courseId = id;

    this.loadCourse();
  }


  loadCourse(): void {

    this.loading.set(true);

    this.error.set(null);


    this.courseService
      .getCourseById(this.courseId)
      .subscribe({

        next: (course: Course) => {

          console.log(
            'ADMIN EDIT COURSE:',
            course
          );

          this.course = course;

          this.title =
            course.title ?? '';

          this.description =
            course.description ?? '';

          this.category =
            course.category ?? '';

          this.published =
            course.published;

          this.active =
            course.active;

          this.loading.set(false);
        },


        error: (
          error: HttpErrorResponse
        ) => {

          console.error(
            'ADMIN EDIT COURSE LOAD ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to load course.'
          );

          this.loading.set(false);
        }

      });
  }


  saveCourse(): void {

    this.error.set(null);

    this.success.set(null);


    if (!this.title.trim()) {

      this.error.set(
        'Course title is required.'
      );

      return;
    }


    if (!this.category.trim()) {

      this.error.set(
        'Course category is required.'
      );

      return;
    }


    this.saving.set(true);


    const request = {

      title:
        this.title.trim(),

      description:
        this.description.trim(),

      category:
        this.category.trim(),

      published:
        this.published,

      active:
        this.active

    };


    this.courseService
      .updateCourse(
        this.courseId,
        request
      )
      .subscribe({

        next: (updatedCourse: Course) => {

          console.log(
            'COURSE UPDATED:',
            updatedCourse
          );

          this.course =
            updatedCourse;

          this.success.set(
            'Course updated successfully.'
          );

          this.saving.set(false);

        },


        error: (
          error: HttpErrorResponse
        ) => {

          console.error(
            'ADMIN UPDATE COURSE ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to update course.'
          );

          this.saving.set(false);
        }

      });
  }


  back(): void {

    this.router.navigate([
      '/admin/courses'
    ]);
  }
}