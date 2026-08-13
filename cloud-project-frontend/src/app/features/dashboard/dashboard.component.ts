import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { CourseService } from '../../core/services/course.service';
import { ActivityItem, Course, EnrolledCourse } from '../../core/models/course.model';
import { CourseCardComponent } from '../../shared/components/course-card/course-card.component';
import { EnrollmentService } from '../../core/services/enrollment.service';
import { RecommendationCourse, RecommendationService } from '../../core/services/recommendation.service';
import { forkJoin } from 'rxjs';
import {
  Router,
} from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CourseCardComponent],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  // 1. Inject dependencies directly into class properties
  private auth = inject(AuthService);
  private courseService = inject(CourseService);
  private enrollmentService = inject(EnrollmentService);
  private readonly recommendationService = inject(RecommendationService);
  private router = inject(Router);
  // 2. This can now safely reference 'this.auth' during initialization
  readonly user = this.auth.currentUser;

  readonly enrolledCourses = signal<EnrolledCourse[]>([]);
  readonly loadingEnrolled = signal(true);
  readonly recommended = signal<Course[]>([]);
  readonly loadingRecommended = signal(true);
  readonly activity = signal<ActivityItem[]>([]);
  readonly loadingActivity = signal(true);
  readonly skeletonArray = Array.from({ length: 3 });
  readonly recommendations =
    signal<RecommendationCourse[]>([]);

  readonly recommendationsLoading =
    signal(false);

  readonly recommendationsError =
    signal<string | null>(null);

  // readonly completionPercent = computed(() => {
  //   const u = this.user();
  //   if (!u || u.enrolledCourseIds.length === 0) return 0;
  //   return Math.round((u.completedCourseIds.length / u.enrolledCourseIds.length) * 100);
  // });

  readonly enrolledCount = computed(() =>
    this.enrolledCourses().length
  );

  readonly completedCount = computed(() =>
    this.enrolledCourses()
      .filter(course => course.progress >= 100)
      .length
  );

  readonly completionPercent = computed(() => {

    const courses = this.enrolledCourses();

    if (courses.length === 0) {
      return 0;
    }

    const totalProgress =
      courses.reduce(
        (sum, course) =>
          sum + (course.progress ?? 0),
        0
      );

    return Math.round(
      totalProgress / courses.length
    );
  });

  readonly activityIcon: Record<ActivityItem['type'], string> = {
    module: 'M9 5H5a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-4M9 5V3a2 2 0 0 1 2-2h9v9h-9a2 2 0 0 1-2-2V5Z',
    video: 'M4 6h16v12H4zM10 9l5 3-5 3V9Z',
    quiz: 'M9 11l3 3 6-6M5 5h14v14H5z',
    certificate: 'M12 3a4 4 0 1 1 0 8 4 4 0 0 1 0-8ZM6.5 14l-1.8 6.5L9 19l2 2 1.9-6M17.5 14l1.8 6.5L15 19l-2 2-1.9-6',
  };

  // 3. Clear constructor parameters
  constructor() { }

  // ngOnInit(): void {
  //   const u = this.user();
  //   if (!u) return;

  //   this.courseService.getEnrolledCourses(u.enrolledCourseIds, u.completedCourseIds).subscribe((courses) => {
  //     this.enrolledCourses.set(courses);
  //     this.loadingEnrolled.set(false);
  //   });

  //   this.courseService.getRecommendedCourses(u.enrolledCourseIds).subscribe((courses) => {
  //     this.recommended.set(courses);
  //     this.loadingRecommended.set(false);
  //   });

  //   this.courseService.getRecentActivity().subscribe((items) => {
  //     this.activity.set(items);
  //     this.loadingActivity.set(false);
  //   });
  // }

  // ngOnInit(): void {
  //   const u = this.user();
  //   if (!u) return;

  //   this.courseService.getEnrolledCourses(
  //     u.enrolledCourseIds,
  //     u.completedCourseIds
  //   ).subscribe((courses) => {
  //     this.enrolledCourses.set(courses);
  //     this.loadingEnrolled.set(false);
  //   });

  //   this.courseService.getRecommendedCourses(
  //     u.enrolledCourseIds
  //   ).subscribe((courses) => {
  //     this.recommended.set(courses);
  //     this.loadingRecommended.set(false);
  //   });

  //   this.courseService.getRecentActivity().subscribe((items) => {
  //     this.activity.set(items);
  //     this.loadingActivity.set(false);
  //   });
  // }



  // ngOnInit(): void {

  //   console.log('========== DASHBOARD TEST ==========');

  //   const u = this.user();

  //   console.log('CURRENT USER:', u);

  //   if (!u) {
  //     console.error('NO CURRENT USER - RETURNING');
  //     return;
  //   }

  //   console.log('USER EXISTS');
  //   console.log('USER ID:', u.id);
  //   console.log('USER EMAIL:', u.email);
  //   console.log('USER ROLE:', u.role);

  //   console.log('CALLING /api/enrollments/my');

  //   this.enrollmentService.getMyEnrollments().subscribe({
  //     next: (enrollments) => {

  //       console.log('ENROLLMENTS FROM BACKEND:', enrollments);

  //       if (enrollments.length === 0) {
  //         console.log('NO ENROLLMENTS');
  //         this.enrolledCourses.set([]);
  //         this.loadingEnrolled.set(false);
  //         return;
  //       }

  //       console.log(
  //         'COURSE IDS:',
  //         enrollments.map(e => e.courseId)
  //       );

  //       const requests = enrollments.map(enrollment => {

  //         console.log(
  //           'CALLING COURSE API FOR:',
  //           enrollment.courseId
  //         );

  //         return this.courseService.getCourseById(
  //           enrollment.courseId
  //         );
  //       });

  //       forkJoin(requests).subscribe({
  //         next: (courses) => {

  //           console.log(
  //             'COURSES FROM COURSE SERVICE:',
  //             courses
  //           );

  //           const enrolledCourses: EnrolledCourse[] =
  //             courses.map((course, index) => {

  //               const enrollment = enrollments[index];

  //               return {
  //                 ...course,
  //                 progress:
  //                   enrollment.status === 'COMPLETED'
  //                     ? 100
  //                     : 0,
  //                 lastAccessed:
  //                   enrollment.updatedAt
  //               };
  //             });

  //           console.log(
  //             'FINAL MY LEARNING COURSES:',
  //             enrolledCourses
  //           );

  //           this.enrolledCourses.set(
  //             enrolledCourses
  //           );

  //           this.loadingEnrolled.set(false);
  //         },

  //         error: (error) => {
  //           console.error(
  //             'COURSE API ERROR:',
  //             error
  //           );

  //           this.loadingEnrolled.set(false);
  //         }
  //       });
  //     },

  //     error: (error) => {
  //       console.error(
  //         'ENROLLMENT API ERROR:',
  //         error
  //       );

  //       this.loadingEnrolled.set(false);
  //     }
  //   });
  // }

  loadRecommendations(): void {

    this.recommendationsLoading.set(true);
    this.recommendationsError.set(null);

    this.auth.getMySkills().subscribe({

      next: userSkillsResponse => {

        const studentSkills: Record<string, number> = {};

        for (const item of userSkillsResponse.skills) {

          studentSkills[item.skill] =
            item.proficiency;
        }

        console.log(
          'STUDENT SKILLS FOR ML:',
          studentSkills
        );

        this.courseService.getCourses().subscribe({

          next: courses => {

            console.log(
              'COURSES FOR ML:',
              courses
            );

            const recommendationCourses =
              courses.map(course => {

                const skills: Record<string, number> = {};

                for (const skill of course.skills ?? []) {

                  /*
                   * Every skill currently has
                   * equal course importance.
                   *
                   * We can improve this later.
                   */
                  skills[skill] = 5;
                }

                return {
                  course_id: String(course.id),
                  course_title: course.title,
                  difficulty: 'Intermediate',
                  skills
                };
              });

            const request = {

              student_skills: studentSkills,

              courses: recommendationCourses,

              completed_courses: [],

              enrolled_courses: [],

              top_n: 10
            };

            console.log(
              'RECOMMENDATION REQUEST:',
              request
            );

            this.recommendationService
              .getRecommendations(request)
              .subscribe({

                next: response => {

                  console.log(
                    'RECOMMENDATIONS:',
                    response
                  );

                  this.recommendations.set(
                    response.recommendations
                  );

                  this.recommendationsLoading
                    .set(false);
                },

                error: error => {

                  console.error(
                    'RECOMMENDATION API ERROR:',
                    error
                  );

                  this.recommendationsLoading
                    .set(false);

                  this.recommendationsError.set(
                    'Unable to load recommendations.'
                  );
                }
              });
          },

          error: error => {

            console.error(
              'COURSE API ERROR:',
              error
            );

            this.recommendationsLoading
              .set(false);

            this.recommendationsError.set(
              'Unable to load courses.'
            );
          }
        });
      },

      error: error => {

        console.error(
          'USER SKILLS API ERROR:',
          error
        );

        this.recommendationsLoading
          .set(false);

        this.recommendationsError.set(
          'Unable to load your skills.'
        );
      }
    });
  }

  openCourse(courseId: string): void {

    this.router.navigate([
      '/courses',
      courseId
    ]);

  }

  ngOnInit(): void {

    const u = this.user();

    if (!u) {
      return;
    }

    // ==========================================
    // 1. GET MY ENROLLMENTS
    // ==========================================

    this.enrollmentService.getMyEnrollments().subscribe({

      next: (enrollments) => {

        console.log(
          'ENROLLMENTS FROM BACKEND:',
          enrollments
        );

        if (enrollments.length === 0) {

          this.enrolledCourses.set([]);

          this.loadingEnrolled.set(false);

          return;
        }

        // ========================================
        // 2. GET COURSE DETAILS
        // ========================================

        const courseRequests = enrollments.map(
          enrollment =>
            this.courseService.getCourseById(
              enrollment.courseId
            )
        );

        forkJoin(courseRequests).subscribe({

          next: (courses) => {

            console.log(
              'COURSES FROM COURSE SERVICE:',
              courses
            );

            // ======================================
            // 3. GET PROGRESS FOR EACH COURSE
            // ======================================

            const progressRequests =
              enrollments.map(
                enrollment =>
                  this.courseService.getCourseProgress(
                    enrollment.courseId
                  )
              );

            forkJoin(progressRequests).subscribe({

              next: (progressList) => {

                console.log(
                  'COURSE PROGRESS:',
                  progressList
                );

                // ==================================
                // 4. COMBINE EVERYTHING
                // ==================================

                const enrolledCourses:
                  EnrolledCourse[] =
                  courses.map(
                    (course, index) => {

                      const enrollment =
                        enrollments[index];

                      const progress =
                        progressList[index];

                      return {
                        ...course,

                        progress:
                          progress.progressPercentage,

                        lastAccessed:
                          enrollment.updatedAt,
                      };
                    }
                  );

                console.log(
                  'FINAL MY LEARNING COURSES:',
                  enrolledCourses
                );

                // ==================================
                // 5. SEND DATA TO UI
                // ==================================

                this.enrolledCourses.set(
                  enrolledCourses
                );

                this.loadingEnrolled.set(
                  false
                );
              },

              error: (error) => {

                console.error(
                  'PROGRESS API ERROR:',
                  error
                );

                this.loadingEnrolled.set(
                  false
                );
              }

            });
          },

          error: (error) => {

            console.error(
              'COURSE API ERROR:',
              error
            );

            this.loadingEnrolled.set(
              false
            );
          }

        });
      },

      error: (error) => {

        console.error(
          'ENROLLMENT API ERROR:',
          error
        );

        this.loadingEnrolled.set(
          false
        );
      }



    });

    this.loadRecommendations();
  }
}
