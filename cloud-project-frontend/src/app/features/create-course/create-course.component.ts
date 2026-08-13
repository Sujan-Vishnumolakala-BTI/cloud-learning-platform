// import {
//     Component,
//     inject,
//     signal
// } from '@angular/core';

// import {
//     FormBuilder,
//     FormArray,
//   ReactiveFormsModule,
//     Validators
// } from '@angular/forms';

// import {
//     Router,
//     RouterLink
// } from '@angular/router';

// import { CommonModule } from '@angular/common';

// import { AuthService } from '../../core/services/auth.service';
// import { CourseService } from '../../core/services/course.service';

// @Component({
//     selector: 'app-create-course',
//     standalone: true,

//     imports: [
//         CommonModule,
//         ReactiveFormsModule,
//         RouterLink
//     ],

//     templateUrl: './create-course.component.html'
// })
// export class CreateCourseComponent {

//     private readonly fb = inject(FormBuilder);
//     private readonly router = inject(Router);
//     private readonly auth = inject(AuthService);
//     private readonly courseService = inject(CourseService);

//     readonly currentUser =
//         this.auth.currentUser;

//     readonly loading =
//         signal(false);

//     readonly error =
//         signal<string | null>(null);

//     readonly success =
//         signal<string | null>(null);

//     readonly courseForm =
//         this.fb.nonNullable.group({

//             title: [
//                 '',
//                 [
//                     Validators.required,
//                     Validators.maxLength(255)
//                 ]
//             ],

//             description: [
//                 '',
//                 [
//                     Validators.required,
//                     Validators.maxLength(2000)
//                 ]
//             ],

//             category: [
//                 '',
//                 Validators.required
//             ]

//         });

//     readonly availableSkills = [
//         'Java',
//         'Python',
//         'SQL',
//         'JavaScript',
//         'TypeScript',
//         'Angular',
//         'React',
//         'Spring Boot',
//         'REST API',
//         'API Testing',
//         'Docker',
//         'Kubernetes',
//         'AWS',
//         'Azure',
//         'Machine Learning',
//         'Data Science',
//         'Git',
//         'GitHub'
//     ];

//     readonly selectedSkills =
//         signal<string[]>([]);

//     createCourse(): void {

//         console.log('🔥 CREATE COURSE CLICKED');

//         this.error.set(null);
//         this.success.set(null);

//         if (this.courseForm.invalid) {

//             this.courseForm.markAllAsTouched();

//             this.error.set(
//                 'Please fill in all required fields.'
//             );

//             return;
//         }

//         const user =
//             this.currentUser();

//         if (!user) {

//             this.error.set(
//                 'You must be logged in.'
//             );

//             return;
//         }

//         const request = {

//             title:
//                 this.courseForm.controls.title
//                     .value.trim(),

//             description:
//                 this.courseForm.controls.description
//                     .value.trim(),

//             category:
//                 this.courseForm.controls.category
//                     .value.trim(),
//             skills:
//                 this.selectedSkills(),

//             instructorId:
//                 Number(user.id)

//         };

//         console.log(
//             'CREATE COURSE REQUEST:',
//             request
//         );

//         this.loading.set(true);

//         this.courseService
//             .createCourse(request)
//             .subscribe({

//                 next: course => {

//                     console.log(
//                         'COURSE CREATED:',
//                         course
//                     );

//                     this.loading.set(false);

//                     this.success.set(
//                         'Course created successfully.'
//                     );

//                     setTimeout(() => {

//                         this.router.navigate([
//                             '/instructor'
//                         ]);

//                     }, 800);

//                 },

//                 error: error => {

//                     console.error(
//                         'CREATE COURSE ERROR:',
//                         error
//                     );

//                     this.loading.set(false);

//                     this.error.set(
//                         error?.error?.message ??
//                         'Unable to create course.'
//                     );

//                 }

//             });
//     }

//     toggleSkill(skill: string): void {

//         this.selectedSkills.update(
//             skills => {

//                 if (skills.includes(skill)) {

//                     return skills.filter(
//                         existing => existing !== skill
//                     );
//                 }

//                 return [
//                     ...skills,
//                     skill
//                 ];

//             }
//         );
//     }
// }


import {
  Component,
  inject,
  signal
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  Router,
  RouterLink
} from '@angular/router';

import { CommonModule } from '@angular/common';

import { AuthService } from '../../core/services/auth.service';
import { CourseService } from '../../core/services/course.service';

@Component({
  selector: 'app-create-course',
  standalone: true,

  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],

  templateUrl: './create-course.component.html'
})
export class CreateCourseComponent {

  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);
  private readonly courseService = inject(CourseService);

  readonly currentUser = this.auth.currentUser;

  readonly loading = signal(false);

  readonly error = signal<string | null>(null);

  readonly success = signal<string | null>(null);

  /*
   * Skills supported by the course recommendation system.
   */
  readonly availableSkills = [
    'Java',
    'Python',
    'JavaScript',
    'TypeScript',

    'HTML',
    'CSS',

    'Angular',
    'React',

    'Spring Boot',

    'SQL',
    'MySQL',
    'PostgreSQL',

    'REST API',
    'API Testing',

    'Git',
    'GitHub',

    'Docker',
    'Kubernetes',

    'Linux',

    'AWS',
    'Azure',

    'DevOps',

    'Machine Learning',
    'Data Science'
  ];

  readonly selectedSkills = signal<string[]>([]);

  readonly courseForm = this.fb.nonNullable.group({

    title: [
      '',
      [
        Validators.required,
        Validators.maxLength(255)
      ]
    ],

    description: [
      '',
      [
        Validators.required,
        Validators.maxLength(2000)
      ]
    ],

    category: [
      '',
      Validators.required
    ]

  });

  toggleSkill(skill: string): void {

    this.selectedSkills.update(skills => {

      if (skills.includes(skill)) {

        return skills.filter(
          existing => existing !== skill
        );
      }

      return [
        ...skills,
        skill
      ];
    });
  }

  createCourse(): void {

    console.log('🔥 CREATE COURSE CLICKED');

    this.error.set(null);
    this.success.set(null);

    /*
     * Validate normal form fields.
     */
    if (this.courseForm.invalid) {

      this.courseForm.markAllAsTouched();

      this.error.set(
        'Please fill in all required fields.'
      );

      return;
    }

    /*
     * Skills are required because the
     * recommendation system uses them.
     */
    if (this.selectedSkills().length === 0) {

      this.error.set(
        'Please select at least one skill students will learn.'
      );

      return;
    }

    /*
     * Get logged-in instructor.
     */
    const user = this.currentUser();

    if (!user) {

      this.error.set(
        'You must be logged in.'
      );

      return;
    }

    /*
     * Build backend request.
     */
    const request = {

      title:
        this.courseForm.controls.title
          .value.trim(),

      description:
        this.courseForm.controls.description
          .value.trim(),

      category:
        this.courseForm.controls.category
          .value.trim(),

      skills:
        this.selectedSkills(),

      instructorId:
        Number(user.id)

    };

    console.log(
      'CREATE COURSE REQUEST:',
      request
    );

    this.loading.set(true);

    this.courseService
      .createCourse(request)
      .subscribe({

        next: course => {

          console.log(
            'COURSE CREATED:',
            course
          );

          this.loading.set(false);

          this.success.set(
            'Course created successfully.'
          );

          setTimeout(() => {

            this.router.navigate([
              '/instructor'
            ]);

          }, 800);
        },

        error: error => {

          console.error(
            'CREATE COURSE ERROR:',
            error
          );

          this.loading.set(false);

          this.error.set(
            error?.error?.message ??
            'Unable to create course.'
          );
        }

      });
  }
}