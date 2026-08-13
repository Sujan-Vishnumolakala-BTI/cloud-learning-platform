// import { Component, Input } from '@angular/core';
// import { RouterLink } from '@angular/router';
// import { Course, EnrolledCourse } from '../../../core/models/course.model';

// @Component({
//   selector: 'app-course-card',
//   standalone: true,
//   imports: [RouterLink],
//   templateUrl: './course-card.component.html',
// })
// export class CourseCardComponent {
//   @Input({ required: true }) course!: Course | EnrolledCourse;
//   @Input() showProgress = false;

//   get enrolled(): EnrolledCourse | null {
//     return this.showProgress ? (this.course as EnrolledCourse) : null;
//   }

//   readonly categoryColors: Record<string, string> = {
//     AWS: 'bg-orange-50 text-orange-600',
//     Azure: 'bg-sky-50 text-sky-600',
//     'Google Cloud': 'bg-blue-50 text-blue-600',
//     Docker: 'bg-cyan-50 text-cyan-600',
//     Kubernetes: 'bg-indigo-50 text-indigo-600',
//     DevOps: 'bg-teal-50 text-teal-600',
//   };
// }

import { Component, Input, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
  Course,
  EnrolledCourse,
} from '../../../core/models/course.model';

import { EnrollmentService } from '../../../core/services/enrollment.service';

@Component({
  selector: 'app-course-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './course-card.component.html',
})
export class CourseCardComponent {

  private readonly enrollmentService =
    inject(EnrollmentService);

  @Input({ required: true })
  course!: Course | EnrolledCourse;

  @Input()
  showProgress = false;

  readonly enrolling = signal(false);

  readonly enrollmentError =
    signal<string | null>(null);

  readonly enrolledSuccessfully =
    signal(false);

  get enrolled(): EnrolledCourse | null {
    return this.showProgress
      ? (this.course as EnrolledCourse)
      : null;
  }

  readonly categoryColors: Record<string, string> = {
    AWS: 'bg-orange-50 text-orange-600',
    Azure: 'bg-sky-50 text-sky-600',
    'Google Cloud': 'bg-blue-50 text-blue-600',
    Docker: 'bg-cyan-50 text-cyan-600',
    Kubernetes: 'bg-indigo-50 text-indigo-600',
    DevOps: 'bg-teal-50 text-teal-600',
  };

  enroll(): void {

    if (this.enrolling()) {
      return;
    }

    this.enrollmentError.set(null);
    this.enrolling.set(true);

    const courseId = Number(this.course.id);

    this.enrollmentService
      .enroll(courseId)
      .subscribe({

        next: (response) => {

          console.log(
            'Enrollment successful:',
            response
          );

          this.enrolling.set(false);
          this.enrolledSuccessfully.set(true);
        },

        error: (error) => {

          console.error(
            'Enrollment failed:',
            error
          );

          this.enrolling.set(false);

          this.enrollmentError.set(
            error?.error?.message ||
            'Unable to enroll in this course.'
          );
        },
      });
  }
}