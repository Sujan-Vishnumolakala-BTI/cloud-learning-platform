import {
  Injectable,
  inject
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable,
  forkJoin,
  map,
  of,
  switchMap
} from 'rxjs';

import {
  AdminCourse,
  AdminStats,
  AdminUser
} from '../models/admin.model';


@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private readonly http =
    inject(HttpClient);


  // =========================================
  // BACKEND URLs
  // =========================================

  private readonly userApiUrl =
    'http://localhost:8080/api/admin/users';

  private readonly courseApiUrl =
    'http://localhost:8080/api/courses';

  private readonly enrollmentApiUrl =
    'http://localhost:8080/api/admin/enrollments';

  private readonly internalEnrollmentApiUrl =
    'http://localhost:8080/api/internal';


  // =========================================
  // GET USERS
  // =========================================

  getUsers(): Observable<AdminUser[]> {

    return this.http
      .get<any[]>(
        this.userApiUrl
      )
      .pipe(

        map(users =>

          users.map(user => ({

            id:
              String(user.id),

            name:
              `${user.firstName ?? ''} ${user.lastName ?? ''}`
                .trim(),

            email:
              user.email,

            role:
              String(
                user.role
              ).toLowerCase() as
              'student' |
              'instructor' |
              'admin',

            enrolledCourses:
              0,

            completedCourses:
              0,

            progress:
              0,

            lastActive:
              'Unknown',

            status:
              user.enabled
                ? 'Active'
                : 'Inactive',

            joinedDate:
              user.createdAt
                ? new Date(
                  user.createdAt
                ).toLocaleDateString(
                  'en-IN'
                )
                : 'Unknown'

          }))

        )

      );
  }


  // =========================================
  // GET COURSES
  // =========================================

  getCourses(): Observable<AdminCourse[]> {

    return this.http
      .get<any[]>(
        this.courseApiUrl
      )
      .pipe(

        map(courses =>

          courses.map(course => ({

            id:
              String(course.id),

            title:
              course.title,

            instructor:
              course.instructorName ??
              course.instructor ??
              'Unknown',

            enrolledStudents:
              0,

            completionRate:
              0,

            status:
              course.published
                ? 'Published'
                : 'Draft'

          }))

        )

      );
  }


  // =========================================
  // GET RAW COURSES
  //
  // Needed because we need instructorId
  // =========================================

  getRawCourses(): Observable<any[]> {

    return this.http.get<any[]>(
      this.courseApiUrl
    );
  }


  // =========================================
  // GET ENROLLMENT STATS
  // =========================================

  getEnrollmentStats(): Observable<{

    totalEnrollments: number;

    activeEnrollments: number;

    completedEnrollments: number;

    cancelledEnrollments: number;

  }> {

    return this.http.get<{

      totalEnrollments: number;

      activeEnrollments: number;

      completedEnrollments: number;

      cancelledEnrollments: number;

    }>(
      `${this.enrollmentApiUrl}/stats`
    );
  }


  // =========================================
  // GET STUDENTS FOR COURSE
  // =========================================

  getStudentsByCourse(
    courseId: number
  ): Observable<any[]> {

    return this.http.get<any[]>(
      `${this.internalEnrollmentApiUrl}/courses/${courseId}/students`
    );
  }


  // =========================================
  // GET COURSES WITH REAL ENROLLMENT DATA
  // =========================================

  getCoursesWithDetails(): Observable<AdminCourse[]> {

    return forkJoin({

      users:
        this.getUsers(),

      courses:
        this.http.get<any[]>(
          this.courseApiUrl
        )

    }).pipe(

      switchMap(({ users, courses }) => {

        /*
         * No courses
         */
        if (courses.length === 0) {

          return of(
            [] as AdminCourse[]
          );
        }


        /*
         * Load enrollment information
         * for every course.
         */
        const courseRequests =
          courses.map(course =>

            this.getStudentsByCourse(
              Number(course.id)
            ).pipe(

              map(students => {

                /*
                 * Ignore cancelled enrollments.
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
                 * Currently enrolled students.
                 */
                const enrolledStudents =
                  validStudents.length;


                /*
                 * Completed enrollments.
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
                 * Completion rate.
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
                 * Find instructor.
                 */
                const instructor =
                  users.find(
                    user =>
                      String(user.id) ===
                      String(
                        course.instructorId
                      )
                  );


                const instructorName =
                  instructor?.name ??
                  'Unknown';


                return {

                  id:
                    String(course.id),

                  title:
                    course.title,

                  instructor:
                    instructorName,

                  enrolledStudents:
                    enrolledStudents,

                  completionRate:
                    completionRate,

                  status:
                    course.published
                      ? 'Published'
                      : 'Draft'

                } as AdminCourse;

              })

            )

          );


        /*
         * forkJoin waits for all courses.
         *
         * Result:
         * Observable<AdminCourse[]>
         */
        return forkJoin(
          courseRequests
        );

      })

    );
  }  // =========================================
  // GET REAL USER ENROLLMENT DATA
  // =========================================

  getUsersWithEnrollmentData(): Observable<AdminUser[]> {

    return forkJoin({

      users:
        this.getUsers(),

      courses:
        this.getRawCourses()

    }).pipe(

      map(({ users, courses }) => {

        const requests =
          courses.map(course =>

            this.getStudentsByCourse(
              Number(course.id)
            ).pipe(

              map(students => ({
                courseId: Number(course.id),
                students
              }))

            )

          );

        return {
          users,
          requests
        };

      }),

      /*
       * forkJoin all course enrollment
       * requests.
       */

      map(data =>

        forkJoin(data.requests).pipe(

          map(courseEnrollments =>

            data.users.map(user => {

              const userEnrollments =
                courseEnrollments
                  .flatMap(
                    course =>
                      course.students.filter(
                        student =>
                          String(
                            student.userId
                          ) ===
                          String(user.id)
                      )
                  );


              const validEnrollments =
                userEnrollments.filter(
                  enrollment =>
                    String(
                      enrollment.status
                    ).toUpperCase() !==
                    'CANCELLED'
                );


              const enrolledCourses =
                validEnrollments.length;


              const completedCourses =
                validEnrollments.filter(
                  enrollment =>
                    String(
                      enrollment.status
                    ).toUpperCase() ===
                    'COMPLETED'
                ).length;


              const progress =
                enrolledCourses > 0
                  ? Math.round(
                    completedCourses /
                    enrolledCourses *
                    100
                  )
                  : 0;


              return {

                ...user,

                enrolledCourses,

                completedCourses,

                progress

              };

            })

          )

        )

      ),

      /*
       * Flatten nested observable.
       */

      map(observable => observable)

    ) as unknown as Observable<AdminUser[]>;
  }
  // =========================================
  // ADMIN DASHBOARD STATS
  // =========================================

  getStats(): Observable<AdminStats> {

    return forkJoin({

      users:
        this.getUsers(),

      courses:
        this.getCourses(),

      enrollmentStats:
        this.getEnrollmentStats()

    }).pipe(

      map(({
        users,
        courses,
        enrollmentStats
      }) => {

        const totalUsers =
          users.length;


        const totalStudents =
          users.filter(
            user =>
              user.role === 'student'
          ).length;


        const totalInstructors =
          users.filter(
            user =>
              user.role === 'instructor'
          ).length;


        const activeStudents =
          users.filter(
            user =>
              user.role === 'student' &&
              user.status === 'Active'
          ).length;


        const totalCourses =
          courses.length;


        return {

          totalUsers,

          totalStudents,

          totalInstructors,

          totalCourses,

          totalEnrollments:
            enrollmentStats.totalEnrollments,

          activeStudents,

          /*
           * True lesson progress is not
           * available from the current
           * admin enrollment endpoints.
           */

          averageCompletion:
            0

        };

      })

    );
  }

}