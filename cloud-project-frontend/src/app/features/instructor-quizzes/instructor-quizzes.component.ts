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
    Quiz,
    CourseLesson
} from '../../core/models/course.model';

import { Lesson } from '../../core/models/lesson.model';

@Component({
    selector: 'app-instructor-quizzes',
    standalone: true,

    imports: [
        CommonModule,
        FormsModule
    ],

    templateUrl:
        './instructor-quizzes.component.html'
})
export class InstructorQuizzesComponent
    implements OnInit {

    private readonly route =
        inject(ActivatedRoute);

    private readonly router =
        inject(Router);

    private readonly courseService =
        inject(CourseService);


    readonly quizzes =
        signal<Quiz[]>([]);

    readonly loading =
        signal(true);

    readonly saving =
        signal(false);

    readonly error =
        signal<string | null>(null);

    readonly success =
        signal<string | null>(null);

    readonly lessons = signal<CourseLesson[]>([]);


    courseId!: number;

    showForm = false;

    editingQuizId:
        number | null = null;

    title = '';

    description = '';

    passingScore = 70;

    lessonId:
        number | null = null;


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

        this.loadLessons();

        this.loadQuizzes();
    }


    loadQuizzes(): void {

        this.loading.set(true);

        this.courseService
            .getQuizzesByCourse(
                this.courseId
            )
            .subscribe({

                next: quizzes => {

                    console.log(
                        'COURSE QUIZZES:',
                        quizzes
                    );

                    this.quizzes.set(
                        quizzes
                    );

                    this.loading.set(false);
                },

                error: error => {

                    console.error(
                        'QUIZ LOAD ERROR:',
                        error
                    );

                    this.error.set(
                        error?.error?.message ??
                        'Unable to load quizzes.'
                    );

                    this.loading.set(false);
                }

            });
    }

    loadLessons(): void {

        this.courseService
            .getLessonsByCourse(
                this.courseId
            )
            .subscribe({

                next: lessons => {

                    this.lessons.set(
                        lessons
                    );

                    console.log(
                        'COURSE LESSONS:',
                        lessons
                    );
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
                }

            });
    }


    openCreateForm(): void {

        this.editingQuizId = null;

        this.title = '';

        this.description = '';

        this.passingScore = 70;

        this.lessonId = null;

        this.showForm = true;

        this.error.set(null);

        this.success.set(null);
    }


    openEditForm(
        quiz: Quiz
    ): void {

        this.editingQuizId =
            quiz.id;

        this.title =
            quiz.title;

        this.description =
            quiz.description ?? '';

        this.passingScore =
            quiz.passingScore ?? 70;

        this.lessonId =
            quiz.lessonId;

        this.showForm = true;

        this.error.set(null);

        this.success.set(null);
    }


    cancelForm(): void {

        this.showForm = false;

        this.editingQuizId = null;
    }


    saveQuiz(): void {

        this.error.set(null);

        this.success.set(null);


        if (!this.title.trim()) {

            this.error.set(
                'Quiz title is required.'
            );

            return;
        }


        if (
            this.passingScore < 1 ||
            this.passingScore > 100
        ) {

            this.error.set(
                'Passing score must be between 1 and 100.'
            );

            return;
        }


        if (!this.lessonId) {

            this.error.set(
                'Lesson ID is required.'
            );

            return;
        }


        this.saving.set(true);


        const request = {

            lessonId:
                this.lessonId,

            title:
                this.title.trim(),

            description:
                this.description.trim(),

            passingScore:
                this.passingScore
        };


        if (
            this.editingQuizId !== null
        ) {

            this.courseService
                .updateQuiz(
                    this.editingQuizId,
                    request
                )
                .subscribe({

                    next: () => {

                        this.success.set(
                            'Quiz updated successfully.'
                        );

                        this.saving.set(false);

                        this.showForm = false;

                        this.loadQuizzes();
                    },

                    error: error => {

                        console.error(
                            'UPDATE QUIZ ERROR:',
                            error
                        );

                        this.error.set(
                            error?.error?.message ??
                            'Unable to update quiz.'
                        );

                        this.saving.set(false);
                    }

                });

            return;
        }


        this.courseService
            .createQuiz(
                this.lessonId,
                request
            )
            .subscribe({

                next: () => {

                    this.success.set(
                        'Quiz created successfully.'
                    );

                    this.saving.set(false);

                    this.showForm = false;

                    this.loadQuizzes();
                },

                error: error => {

                    console.error(
                        'CREATE QUIZ ERROR:',
                        error
                    );

                    this.error.set(
                        error?.error?.message ??
                        'Unable to create quiz.'
                    );

                    this.saving.set(false);
                }

            });
    }


    deleteQuiz(
        quiz: Quiz
    ): void {

        if (
            !window.confirm(
                `Delete "${quiz.title}"?`
            )
        ) {

            return;
        }


        this.courseService
            .deleteQuiz(
                quiz.id
            )
            .subscribe({

                next: () => {

                    this.success.set(
                        'Quiz deleted successfully.'
                    );

                    this.loadQuizzes();
                },

                error: error => {

                    console.error(
                        'DELETE QUIZ ERROR:',
                        error
                    );

                    this.error.set(
                        error?.error?.message ??
                        'Unable to delete quiz.'
                    );
                }

            });
    }


    manageQuestions(
        quiz: Quiz
    ): void {

        this.router.navigate([
            '/instructor/courses',
            this.courseId,
            'quizzes',
            quiz.id,
            'questions'
        ]);
    }

    



    back(): void {

        this.router.navigate([
            '/instructor/courses',
            this.courseId
        ]);
    }
}