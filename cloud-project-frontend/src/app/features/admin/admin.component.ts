import {
  CommonModule
} from '@angular/common';

import {
  Component,
  computed,
  inject,
  OnInit,
  signal
} from '@angular/core';

import {
  Router
} from '@angular/router';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  AuthService
} from '../../core/services/auth.service';

import {
  AdminService
} from '../../core/services/admin.service';

import {
  AdminCourse,
  AdminStats,
  AdminUser
} from '../../core/models/admin.model';


@Component({
  selector: 'app-admin',

  standalone: true,

  imports: [
    CommonModule
  ],

  templateUrl:
    './admin.component.html'
})
export class AdminComponent
  implements OnInit {


  /*
   * =================================
   * SERVICES
   * =================================
   */

  private readonly auth =
    inject(AuthService);

  private readonly adminService =
    inject(AdminService);

  private readonly router =
    inject(Router);


  /*
   * =================================
   * CURRENT USER
   * =================================
   */

  readonly currentUser =
    this.auth.currentUser;


  /*
   * =================================
   * ADMIN DATA
   * =================================
   */

  readonly stats =
    signal<AdminStats | null>(
      null
    );

  readonly users =
    signal<AdminUser[]>(
      []
    );

  readonly courses =
    signal<AdminCourse[]>(
      []
    );


  /*
   * =================================
   * FILTERS
   * =================================
   */

  readonly searchTerm =
    signal('');

  readonly selectedRole =
    signal<
      'all' |
      'student' |
      'instructor' |
      'admin'
    >('all');


  /*
   * =================================
   * LOADING / ERROR
   * =================================
   */

  readonly loading =
    signal(true);

  readonly error =
    signal<string | null>(
      null
    );


  /*
   * =================================
   * FILTERED USERS
   * =================================
   */

  readonly filteredUsers =
    computed(() => {

      const search =
        this.searchTerm()
          .toLowerCase()
          .trim();

      const role =
        this.selectedRole();


      return this.users()
        .filter(user => {

          const matchesSearch =
            !search ||

            user.name
              .toLowerCase()
              .includes(search) ||

            user.email
              .toLowerCase()
              .includes(search);


          const matchesRole =
            role === 'all' ||
            user.role === role;


          return (
            matchesSearch &&
            matchesRole
          );

        });

    });


  /*
   * =================================
   * INITIALIZATION
   * =================================
   */

  ngOnInit(): void {

    /*
     * Only ADMIN can access
     * this dashboard.
     */

    if (
      this.currentUser()?.role !==
      'admin'
    ) {

      this.router.navigate([
        '/dashboard'
      ]);

      return;
    }


    this.loadAdminData();
  }


  /*
   * =================================
   * LOAD ADMIN DATA
   * =================================
   */

  private loadAdminData(): void {

    this.loading.set(true);

    this.error.set(null);


    /*
     * =================================
     * LOAD USERS
     * =================================
     */

    this.adminService
      .getUsers()
      .subscribe({

        next: users => {

          console.log(
            'ADMIN REAL USERS:',
            users
          );

          this.users.set(
            users
          );

          if (
            this.courses().length > 0
          ) {

            this.loadCourseEnrollmentData();

          }

        },

        error: (
          error: HttpErrorResponse
        ) => {

          console.error(
            'ADMIN USERS ERROR:',
            error
          );

          this.error.set(
            'Unable to load users.'
          );

        }

      });


    /*
     * =================================
     * LOAD COURSES
     * =================================
     */

    this.adminService
      .getCourses()
      .subscribe({

        next: courses => {

          console.log(
            'ADMIN REAL COURSES:',
            courses
          );

          /*
           * First set the real courses.
           */
          this.courses.set(
            courses
          );


          /*
           * Then load enrollment data
           * for every course.
           */
          this.loadCourseEnrollmentData();

        },

        error: (
          error: HttpErrorResponse
        ) => {

          console.error(
            'ADMIN COURSES ERROR:',
            error
          );

          this.error.set(
            'Unable to load courses.'
          );

        }

      });


    /*
     * =================================
     * LOAD DASHBOARD STATS
     * =================================
     */

    this.adminService
      .getStats()
      .subscribe({

        next: stats => {

          console.log(
            'ADMIN REAL STATS:',
            stats
          );

          this.stats.set(
            stats
          );

          this.loading.set(
            false
          );

        },

        error: (
          error: HttpErrorResponse
        ) => {

          console.error(
            'ADMIN STATS ERROR:',
            error
          );

          this.error.set(
            'Unable to load dashboard statistics.'
          );

          this.loading.set(
            false
          );

        }

      });

  }


  /*
   * =================================
   * LOAD ENROLLMENT DATA
   * FOR EACH COURSE
   * =================================
   */

  private loadCourseEnrollmentData(): void {

    const currentCourses =
      this.courses();

    const currentUsers =
      this.users();


    if (
      currentCourses.length === 0
    ) {

      return;
    }



    /*
     * Load enrollment information
     * for every course.
     */

    currentCourses.forEach(course => {

      const courseId =
        Number(course.id);


      if (
        Number.isNaN(courseId)
      ) {

        console.warn(
          'INVALID COURSE ID:',
          course.id
        );

        return;
      }


      console.log(
        'LOADING STUDENTS FOR COURSE:',
        courseId
      );


      this.adminService
        .getStudentsByCourse(courseId)
        .subscribe({

          next: students => {

            console.log(
              `COURSE ${courseId} STUDENTS:`,
              students
            );


            /*
             * Exclude cancelled enrollments.
             */

            const validStudents =
              students.filter(
                student =>
                  String(
                    student.status
                  ).toUpperCase() !==
                  'CANCELLED'
              );


            /*
             * ACTIVE + COMPLETED =
             * currently enrolled/participating.
             */

            const enrolledStudents =
              validStudents.length;


            /*
             * COMPLETED enrollments.
             */

            const completedStudents =
              validStudents.filter(
                student =>
                  String(
                    student.status
                  ).toUpperCase() ===
                  'COMPLETED'
              ).length;


            /*
             * Course completion rate.
             */

            const completionRate =
              enrolledStudents > 0
                ? Math.round(
                  (
                    completedStudents /
                    enrolledStudents
                  ) * 100
                )
                : 0;


            /*
             * Resolve instructor name.
             *
             * CourseResponse gives us
             * instructorId.
             */

            /*
             * We need the original course
             * object because AdminCourse only
             * contains instructor name.
             */

            const originalCourse =
              currentCourses.find(
                c =>
                  Number(c.id) ===
                  courseId
              );


            /*
             * The easiest reliable way:
             * find the instructor by matching
             * the course's instructorId from
             * the raw course API.
             */

            this.adminService
              .getRawCourses()
              .subscribe({

                next: rawCourses => {

                  const rawCourse =
                    rawCourses.find(
                      raw =>
                        Number(raw.id) ===
                        courseId
                    );


                  const instructor =
                    currentUsers.find(
                      user =>
                        String(user.id) ===
                        String(
                          rawCourse?.instructorId
                        )
                    );


                  const instructorName =
                    instructor?.name ??
                    'Unknown';


                  /*
                   * Update course card.
                   */

                  this.courses.update(
                    courses =>

                      courses.map(
                        existingCourse => {

                          if (
                            Number(
                              existingCourse.id
                            ) !== courseId
                          ) {

                            return existingCourse;
                          }


                          return {

                            ...existingCourse,

                            instructor:
                              instructorName,

                            enrolledStudents:
                              enrolledStudents,

                            completionRate:
                              completionRate

                          };

                        }
                      )

                  );


                  /*
                   * Update users.
                   */

                  this.updateUserEnrollmentData(
                    courseId,
                    validStudents
                  );

                },

                error: error => {

                  console.error(
                    'COURSE DETAIL ERROR:',
                    error
                  );

                }

              });

          },

          error: (
            error: HttpErrorResponse
          ) => {

            console.error(
              `COURSE ${courseId} STUDENTS ERROR:`,
              error
            );

          }

        });

    });

  }

  private updateUserEnrollmentData(
    courseId: number,
    students: any[]
  ): void {

    this.users.update(
      users =>

        users.map(user => {

          /*
           * Find this user's enrollment
           * for the current course.
           */

          const enrollment =
            students.find(
              student =>
                String(
                  student.userId
                ) ===
                String(user.id)
            );


          /*
           * User is not enrolled
           * in this course.
           */

          if (!enrollment) {

            return user;

          }


          /*
           * Count courses from the
           * existing value.
           */

          const enrolledCourses =
            user.enrolledCourses + 1;


          /*
           * COMPLETED course.
           */

          const isCompleted =
            String(
              enrollment.status
            ).toUpperCase() ===
            'COMPLETED';


          const completedCourses =
            user.completedCourses +
            (
              isCompleted
                ? 1
                : 0
            );


          /*
           * Overall course completion.
           */

          const progress =
            enrolledCourses > 0
              ? Math.round(
                (
                  completedCourses /
                  enrolledCourses
                ) * 100
              )
              : 0;


          return {

            ...user,

            enrolledCourses,

            completedCourses,

            progress

          };

        })

    );
  }
  /*
   * =================================
   * SEARCH
   * =================================
   */

  updateSearch(
    value: string
  ): void {

    this.searchTerm.set(
      value
    );

  }


  /*
   * =================================
   * ROLE FILTER
   * =================================
   */

  updateRole(
    value: string
  ): void {

    this.selectedRole.set(

      value as
      'all' |
      'student' |
      'instructor' |
      'admin'

    );

  }


  /*
   * =================================
   * ROLE LABEL
   * =================================
   */

  roleLabel(
    role: AdminUser['role']
  ): string {

    return (
      role
        .charAt(0)
        .toUpperCase() +
      role.slice(1)
    );

  }


  /*
   * =================================
   * ROLE BADGE
   * =================================
   */

  roleBadgeClass(
    role: AdminUser['role']
  ): string {

    switch (role) {

      case 'admin':

        return (
          'bg-purple-100 ' +
          'text-purple-700'
        );


      case 'instructor':

        return (
          'bg-blue-100 ' +
          'text-blue-700'
        );


      case 'student':

      default:

        return (
          'bg-emerald-100 ' +
          'text-emerald-700'
        );

    }

  }


  /*
   * =================================
   * PROGRESS BAR
   * =================================
   */

  progressClass(
    progress: number
  ): string {

    if (
      progress >= 80
    ) {

      return 'bg-emerald-500';
    }


    if (
      progress >= 50
    ) {

      return 'bg-blue-500';
    }


    return 'bg-amber-500';

  }

  /*
   * =================================
   * LOGOUT
   * =================================
   */

  manageCourses(): void {
    console.log('MANAGE COURSES CLICKED');

    this.router.navigate(['/admin/courses'])
      .then(success => {
        console.log(
          'NAVIGATION RESULT:',
          success
        );
      })
      .catch(error => {
        console.error(
          'NAVIGATION ERROR:',
          error
        );
      });
  }
  
  logout(): void {

    this.auth.logout();

    this.router.navigate([
      '/'
    ]);

  }

}

function manageCourses() {
  throw new Error('Function not implemented.');
}
