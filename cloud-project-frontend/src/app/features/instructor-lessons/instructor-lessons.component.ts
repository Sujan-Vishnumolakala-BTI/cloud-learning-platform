// import {
//   Component,
//   OnInit,
//   inject,
//   signal
// } from '@angular/core';

// import {
//   CommonModule
// } from '@angular/common';

// import {
//   FormsModule
// } from '@angular/forms';

// import {
//   ActivatedRoute,
//   Router
// } from '@angular/router';

// import {
//   CourseService
// } from '../../core/services/course.service';

// // import {
// //   Lesson
// // } from '../../core/models/lesson.model';

// import { Lesson } from '../../core/models/lesson.model';

// @Component({
//   selector: 'app-instructor-lessons',
//   standalone: true,

//   imports: [
//     CommonModule,
//     FormsModule
//   ],

//   templateUrl:
//     './instructor-lessons.component.html'
// })
// export class InstructorLessonsComponent
//   implements OnInit {

//   private readonly route =
//     inject(ActivatedRoute);

//   private readonly router =
//     inject(Router);

//   private readonly courseService =
//     inject(CourseService);


//   readonly lessons =
//     signal<Lesson[]>([]);

//   readonly loading =
//     signal(true);

//   readonly saving =
//     signal(false);

//   readonly error =
//     signal<string | null>(null);

//   readonly success =
//     signal<string | null>(null);


//   courseId!: number;


//   showForm = false;

//   editingLessonId:
//     number | null = null;


//   title = '';

//   description = '';

//   contentType = 'VIDEO';

//   contentUrl = '';

//   durationMinutes = 30;

//   orderIndex = 1;


//   ngOnInit(): void {

//     const id =
//       Number(
//         this.route.snapshot.paramMap.get(
//           'courseId'
//         )
//       );

//     if (!id) {

//       this.error.set(
//         'Invalid course ID.'
//       );

//       this.loading.set(false);

//       return;
//     }

//     this.courseId = id;

//     this.loadLessons();
//   }


//   loadLessons(): void {

//     this.loading.set(true);

//     this.courseService
//       .getLessonsByCourse(
//         this.courseId
//       )
//       .subscribe({

//         next: lessons => {

//           this.lessons.set(
//             lessons
//           );

//           this.loading.set(false);
//         },

//         error: error => {

//           console.error(
//             'LESSON LOAD ERROR:',
//             error
//           );

//           this.error.set(
//             error?.error?.message ??
//             'Unable to load lessons.'
//           );

//           this.loading.set(false);
//         }

//       });
//   }


//   openCreateForm(): void {

//     this.editingLessonId = null;

//     this.title = '';

//     this.description = '';

//     this.contentType = 'VIDEO';

//     this.contentUrl = '';

//     this.durationMinutes = 30;

//     this.orderIndex =
//       this.lessons().length + 1;

//     this.showForm = true;

//     this.error.set(null);

//     this.success.set(null);
//   }


//   openEditForm(
//     lesson: Lesson
//   ): void {

//     this.editingLessonId =
//       lesson.id;

//     this.title =
//       lesson.title;

//     this.description =
//       lesson.description ?? '';

//     this.contentType =
//       lesson.contentType;

//     this.contentUrl =
//       lesson.contentUrl ?? '';

//     this.durationMinutes =
//       lesson.durationMinutes ?? 30;

//     this.orderIndex =
//       lesson.orderIndex;

//     this.showForm = true;

//     this.error.set(null);

//     this.success.set(null);
//   }


//   cancelForm(): void {

//     this.showForm = false;

//     this.editingLessonId = null;
//   }


//   saveLesson(): void {

//     if (!this.title.trim()) {

//       this.error.set(
//         'Lesson title is required.'
//       );

//       return;
//     }


//     this.saving.set(true);

//     this.error.set(null);

//     this.success.set(null);


//     const request = {

//       title:
//         this.title.trim(),

//       description:
//         this.description.trim(),

//       contentType:
//         this.contentType,

//       contentUrl:
//         this.contentUrl.trim(),

//       durationMinutes:
//         this.durationMinutes,

//       orderIndex:
//         this.orderIndex

//     };


//     if (
//       this.editingLessonId !== null
//     ) {

//       this.courseService
//         .updateLesson(
//           this.editingLessonId,
//           request
//         )
//         .subscribe({

//           next: () => {

//             this.success.set(
//               'Lesson updated successfully.'
//             );

//             this.saving.set(false);

//             this.showForm = false;

//             this.loadLessons();
//           },

//           error: error => {

//             this.error.set(
//               error?.error?.message ??
//               'Unable to update lesson.'
//             );

//             this.saving.set(false);
//           }

//         });

//       return;
//     }


//     this.courseService
//       .createLesson(
//         this.courseId,
//         request
//       )
//       .subscribe({

//         next: () => {

//           this.success.set(
//             'Lesson created successfully.'
//           );

//           this.saving.set(false);

//           this.showForm = false;

//           this.loadLessons();
//         },

//         error: error => {

//           this.error.set(
//             error?.error?.message ??
//             'Unable to create lesson.'
//           );

//           this.saving.set(false);
//         }

//       });
//   }


//   deleteLesson(
//     lesson: Lesson
//   ): void {

//     if (
//       !window.confirm(
//         `Delete "${lesson.title}"?`
//       )
//     ) {

//       return;
//     }


//     this.courseService
//       .deleteLesson(
//         lesson.id
//       )
//       .subscribe({

//         next: () => {

//           this.success.set(
//             'Lesson deleted successfully.'
//           );

//           this.loadLessons();
//         },

//         error: error => {

//           this.error.set(
//             error?.error?.message ??
//             'Unable to delete lesson.'
//           );
//         }

//       });
//   }


//   back(): void {

//     this.router.navigate([
//       '/instructor/courses',
//       this.courseId
//     ]);
//   }


// }

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
  FormsModule
} from '@angular/forms';

import {
  ActivatedRoute,
  Router
} from '@angular/router';

import {
  CourseService
} from '../../core/services/course.service';

import {
  Lesson
} from '../../core/models/lesson.model';

import {
  CourseLesson
} from '../../core/models/course-lesson.model';

import {
  Module
} from '../../core/models/module.model';

@Component({
  selector: 'app-instructor-lessons',
  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl:
    './instructor-lessons.component.html'
})
export class InstructorLessonsComponent
  implements OnInit {

  private readonly route =
    inject(ActivatedRoute);

  private readonly router =
    inject(Router);

  private readonly courseService =
    inject(CourseService);


  readonly lessons =
    signal<CourseLesson[]>([]);

  readonly modules =
    signal<Module[]>([]);

  readonly loading =
    signal(true);

  readonly saving =
    signal(false);

  readonly error =
    signal<string | null>(null);

  readonly success =
    signal<string | null>(null);


  courseId!: number;

  showForm = false;

  editingLessonId:
    number | null = null;


  title = '';

  description = '';

  contentType = 'VIDEO';

  contentUrl = '';

  durationMinutes = 30;

  orderIndex = 1;

  moduleId:
    number | null = null;

  selectedVideoFile: File | null = null;
  videoUploading = false;


  ngOnInit(): void {

    const id =
      Number(
        this.route.snapshot.paramMap.get(
          'courseId'
        )
      );

    if (!id) {

      this.error.set(
        'Invalid course ID.'
      );

      this.loading.set(false);

      return;
    }

    this.courseId = id;

    this.loadModules();

    this.loadLessons();
  }


  loadModules(): void {

    this.courseService
      .getModulesByCourse(
        this.courseId
      )
      .subscribe({

        next: modules => {

          console.log(
            'MODULES:',
            modules
          );

          this.modules.set(
            modules
          );
        },

        error: error => {

          console.error(
            'MODULE LOAD ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to load modules.'
          );
        }
      });
  }


  loadLessons(): void {

    this.loading.set(true);

    this.courseService
      .getLessonsByCourse(
        this.courseId
      )
      .subscribe({

        next: lessons => {

          console.log(
            'COURSE LESSONS:',
            lessons
          );

          this.lessons.set(
            lessons
          );

          this.loading.set(false);
        },

        error: error => {

          console.error(
            'LESSON LOAD ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to load lessons.'
          );

          this.loading.set(false);
        }
      });
  }


  openCreateForm(): void {

    this.editingLessonId = null;

    this.title = '';

    this.description = '';

    this.contentType = 'VIDEO';

    this.contentUrl = '';

    this.durationMinutes = 30;

    this.orderIndex =
      this.lessons().length + 1;

    this.moduleId = null;

    this.showForm = true;

    this.error.set(null);

    this.success.set(null);
  }


  openEditForm(
    lesson: CourseLesson
  ): void {

    this.error.set(null);

    this.success.set(null);

    this.courseService
      .getLesson(
        lesson.id
      )
      .subscribe({

        next: fullLesson => {

          console.log(
            'FULL LESSON:',
            fullLesson
          );

          this.editingLessonId =
            fullLesson.id;

          this.moduleId =
            fullLesson.moduleId;

          this.title =
            fullLesson.title;

          this.description =
            fullLesson.description ?? '';

          this.contentType =
            fullLesson.contentType;

          this.contentUrl =
            fullLesson.contentUrl ?? '';

          this.durationMinutes =
            fullLesson.durationMinutes ?? 30;

          this.orderIndex =
            fullLesson.orderIndex;

          this.showForm = true;
        },

        error: error => {

          console.error(
            'GET LESSON ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to load lesson.'
          );
        }
      });
  }


  cancelForm(): void {

    this.showForm = false;

    this.editingLessonId = null;
  }


  saveLesson(): void {

    this.error.set(null);

    this.success.set(null);


    if (!this.title.trim()) {

      this.error.set(
        'Lesson title is required.'
      );

      return;
    }


    if (
      this.moduleId === null &&
      this.editingLessonId === null
    ) {

      this.error.set(
        'Please select a module.'
      );

      return;
    }


    this.saving.set(true);


    const request = {

      title:
        this.title.trim(),

      description:
        this.description.trim(),

      contentType:
        this.contentType,

      contentUrl:
        this.contentUrl.trim(),

      durationMinutes:
        this.durationMinutes,

      orderIndex:
        this.orderIndex
    };


    // =========================
    // UPDATE
    // =========================

    if (
      this.editingLessonId !== null
    ) {

      const lessonId = this.editingLessonId;
      console.log(
        'UPDATING LESSON:',
        lessonId,
        request
      );


      this.courseService
        .updateLesson(
          this.editingLessonId,
          request
        )
        .subscribe({

          next: updated => {

            console.log(
              'LESSON UPDATED:',
              updated
            );

            if (
              this.contentType === 'VIDEO' &&
              this.selectedVideoFile
            ) {

              console.log(
                'VIDEO SELECTED DURING UPDATE'
              );

              console.log(
                'STARTING VIDEO UPLOAD FOR EXISTING LESSON:',
                lessonId
              );

              // Keep form/saving state until upload completes
              this.uploadVideo(lessonId);

              return;
            }

            this.success.set(
              'Lesson updated successfully.'
            );

            this.saving.set(false);

            this.showForm = false;

            this.loadLessons();
          },

          error: error => {

            console.error(
              'UPDATE LESSON ERROR:',
              error
            );

            this.error.set(
              error?.error?.message ??
              'Unable to update lesson.'
            );

            this.saving.set(false);
          }
        });

      return;
    }


    // =========================
    // CREATE
    // =========================

    console.log('========== BEFORE CREATE LESSON ==========');
    console.log('this.moduleId =', this.moduleId);
    console.log('request =', request);
    this.courseService
      .createLesson(
        this.moduleId!,
        request
      )
      .subscribe({

        next: created => {

          console.log(
            'LESSON CREATED:',
            created
          );

          if (
            this.contentType === 'VIDEO' &&
            this.selectedVideoFile
          ) {

            console.log(
              'STARTING VIDEO UPLOAD FOR LESSON:',
              created.id
            );

            this.uploadVideo(created.id);

            // this.saving.set(false);
            // this.showForm = false;

            return;
          }

          this.success.set(
            'Lesson created successfully.'
          );

          this.saving.set(false);

          this.showForm = false;

          this.loadLessons();
        },

        error: error => {

          console.error(
            'CREATE LESSON ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to create lesson.'
          );

          this.saving.set(false);
        }
      });
  }


  deleteLesson(
    lesson: CourseLesson
  ): void {

    const confirmed =
      window.confirm(
        `Delete "${lesson.title}"?`
      );

    if (!confirmed) {
      return;
    }


    this.courseService
      .deleteLesson(
        lesson.id
      )
      .subscribe({

        next: () => {

          this.success.set(
            'Lesson deleted successfully.'
          );

          this.loadLessons();
        },

        error: error => {

          console.error(
            'DELETE LESSON ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to delete lesson.'
          );
        }
      });
  }

  onVideoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      this.selectedVideoFile = null;
      return;
    }

    const file = input.files[0];

    if (!file.type.startsWith('video/')) {
      this.error.set('Please select a valid video file.');
      this.selectedVideoFile = null;
      return;
    }

    this.selectedVideoFile = file;

    console.log(
      'VIDEO SELECTED:',
      file.name,
      file.type,
      file.size
    );
  }

  uploadVideo(lessonId: number): void {

    if (!this.selectedVideoFile) {
      this.error.set('Please select a video file.');
      this.saving.set(false);
      return;
    }

    this.videoUploading = true;
    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    console.log(
      'REQUESTING VIDEO UPLOAD URL FOR LESSON:',
      lessonId
    );

    this.courseService
      .generateVideoUploadUrl(lessonId)
      .subscribe({

        next: response => {

          console.log(
            'VIDEO UPLOAD URL:',
            response
          );

          this.courseService
            .uploadVideoToMinio(
              response.uploadUrl,
              this.selectedVideoFile!
            )
            .subscribe({

              next: () => {

                console.log(
                  'VIDEO UPLOADED TO MINIO'
                );

                this.courseService
                  .completeVideoUpload(
                    lessonId,
                    response.objectKey
                  )
                  .subscribe({

                    next: updatedLesson => {

                      console.log(
                        'VIDEO UPLOAD COMPLETED:',
                        updatedLesson
                      );

                      this.videoUploading = false;
                      this.saving.set(false);
                      // this.showForm = false;

                      this.success.set(
                        'Lesson and video uploaded successfully.'
                      );

                      this.selectedVideoFile = null;
                      this.showForm = false;
                      this.loadLessons();
                    },

                    error: error => {

                      console.error(
                        'VIDEO COMPLETE ERROR:',
                        error
                      );

                      this.videoUploading = false;
                      this.saving.set(false);

                      this.error.set(
                        error?.error?.message ??
                        'Video uploaded but could not be completed.'
                      );
                    }

                  });
              },

              error: error => {

                console.error(
                  'MINIO VIDEO UPLOAD ERROR:',
                  error
                );

                this.videoUploading = false;
                this.saving.set(false);
                this.error.set(
                  'Failed to upload video to storage.'
                );
              }

            });
        },

        error: error => {

          console.error(
            'UPLOAD URL ERROR:',
            error
          );

          this.videoUploading = false;
          this.saving.set(false);

          this.error.set(
            error?.error?.message ??
            'Unable to generate video upload URL.'
          );
        }

      });
  }


  back(): void {

    this.router.navigate([
      '/instructor/courses',
      this.courseId
    ]);
  }
}