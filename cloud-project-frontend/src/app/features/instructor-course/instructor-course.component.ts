import {
    Component,
    OnInit,
    inject,
    signal,
} from '@angular/core';

import {
    CommonModule,
} from '@angular/common';

import {
    ActivatedRoute,
    Router,
} from '@angular/router';

import {
    FormsModule,
} from '@angular/forms';

import {
    Course,
} from '../../core/models/course.model';

import {
    CourseService,
} from '../../core/services/course.service';


@Component({
    selector: 'app-instructor-course',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
    ],
    templateUrl: './instructor-course.component.html',
})
export class InstructorCourseComponent implements OnInit {

    private readonly route =
        inject(ActivatedRoute);

    private readonly router =
        inject(Router);

    private readonly courseService =
        inject(CourseService);


    readonly course =
        signal<Course | null>(null);

    readonly loading =
        signal(true);

    readonly saving =
        signal(false);

    readonly deleting =
        signal(false);

    readonly error =
        signal<string | null>(null);

    readonly success =
        signal<string | null>(null);
    
        courseId!: number;


    title = '';

    description = '';

    category = '';


    ngOnInit(): void {

        const courseId =
            Number(
                this.route.snapshot.paramMap.get(
                    'courseId'
                )
            );

        if (!courseId) {

            this.error.set(
                'Invalid course ID.'
            );

            this.loading.set(false);

            

            return;
        }

        this.courseId = courseId;

        this.loadCourse(courseId);
    }


    private loadCourse(
        courseId: number
    ): void {

        this.loading.set(true);

        this.courseService
            .getCourseById(courseId)
            .subscribe({

                next: course => {

                    console.log(
                        'INSTRUCTOR COURSE:',
                        course
                    );

                    this.course.set(course);

                    this.title = course.title;

                    this.description =
                        course.description;

                    this.category =
                        course.category;

                    this.loading.set(false);
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
                },
            });
    }


    saveCourse(): void {

        const course = this.course();

        if (!course) {
            return;
        }

        if (!this.title.trim()) {

            this.error.set(
                'Course title is required.'
            );

            return;
        }

        this.saving.set(true);

        this.error.set(null);

        this.success.set(null);

        this.courseService
            .updateCourse(
                course.id,
                {
                    title: this.title.trim(),
                    description:
                        this.description.trim(),
                    category:
                        this.category.trim(),
                }
            )
            .subscribe({

                next: updatedCourse => {

                    this.course.set(
                        updatedCourse
                    );

                    this.saving.set(false);

                    this.success.set(
                        'Course updated successfully.'
                    );
                },

                error: error => {

                    console.error(
                        'UPDATE COURSE ERROR:',
                        error
                    );

                    this.error.set(
                        error?.error?.message ??
                        'Unable to update course.'
                    );

                    this.saving.set(false);
                },
            });
    }


    publish(): void {

        const course = this.course();

        if (!course) {
            return;
        }

        this.courseService
            .publishCourse(course.id)
            .subscribe({

                next: updated => {

                    this.course.set(updated);

                    this.success.set(
                        'Course published successfully.'
                    );
                },

                error: error => {

                    console.error(
                        'PUBLISH ERROR:',
                        error
                    );

                    this.error.set(
                        error?.error?.message ??
                        'Unable to publish course.'
                    );
                },
            });
    }


    unpublish(): void {

        const course = this.course();

        if (!course) {
            return;
        }

        this.courseService
            .unpublishCourse(course.id)
            .subscribe({

                next: updated => {

                    this.course.set(updated);

                    this.success.set(
                        'Course unpublished.'
                    );
                },

                error: error => {

                    console.error(
                        'UNPUBLISH ERROR:',
                        error
                    );

                    this.error.set(
                        error?.error?.message ??
                        'Unable to unpublish course.'
                    );
                },
            });
    }


    activate(): void {

        const course = this.course();

        if (!course) {
            return;
        }

        this.courseService
            .activateCourse(course.id)
            .subscribe({

                next: updated => {

                    this.course.set(updated);

                    this.success.set(
                        'Course activated.'
                    );
                },

                error: error => {

                    console.error(
                        'ACTIVATE ERROR:',
                        error
                    );

                    this.error.set(
                        error?.error?.message ??
                        'Unable to activate course.'
                    );
                },
            });
    }


    deactivate(): void {

        const course = this.course();

        if (!course) {
            return;
        }

        this.courseService
            .deactivateCourse(course.id)
            .subscribe({

                next: updated => {

                    this.course.set(updated);

                    this.success.set(
                        'Course deactivated.'
                    );
                },

                error: error => {

                    console.error(
                        'DEACTIVATE ERROR:',
                        error
                    );

                    this.error.set(
                        error?.error?.message ??
                        'Unable to deactivate course.'
                    );
                },
            });
    }


    deleteCourse(): void {

        const course = this.course();

        if (!course) {
            return;
        }

        const confirmed =
            window.confirm(
                `Are you sure you want to delete "${course.title}"?`
            );

        if (!confirmed) {
            return;
        }

        this.deleting.set(true);

        this.courseService
            .deleteCourse(course.id)
            .subscribe({

                next: () => {

                    this.router.navigate([
                        '/instructor',
                    ]);
                },

                error: error => {

                    console.error(
                        'DELETE COURSE ERROR:',
                        error
                    );

                    this.error.set(
                        error?.error?.message ??
                        'Unable to delete course.'
                    );

                    this.deleting.set(false);
                },
            });
    }

    manageModules(): void {

        console.log('MANAGE MODULES CLICKED');

        alert('Manage Modules clicked');

        this.router.navigate([
            '/instructor/courses',
            this.courseId,
            'modules'
        ]);
    }

    manageLessons(): void {

        console.log('MANAGE LESSONS CLICKED');

        alert('Manage Lessons clicked');

        this.router.navigate([
            '/instructor/courses',
            this.courseId,
            'lessons'
        ]);
    }

    manageQuizzes(): void {

        console.log('MANAGE QUIZZES CLICKED');

        alert('Manage Quizzes clicked');

        this.router.navigate([
            '/instructor/courses',
            this.courseId,
            'quizzes'
        ]);
    }


    back(): void {

        this.router.navigate([
            '/instructor',
        ]);
    }
}