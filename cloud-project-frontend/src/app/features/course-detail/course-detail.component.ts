import {
    Component,
    OnInit,
    inject,
    signal,
} from '@angular/core';

import {
    ActivatedRoute,
    RouterLink,
} from '@angular/router';

import { CourseService } from '../../core/services/course.service';

import {
    Certificate,
    Course,
    CourseProgress,
    Module,
} from '../../core/models/course.model';

@Component({
    selector: 'app-course-detail',
    standalone: true,

    imports: [
        RouterLink,
    ],

    templateUrl: './course-detail.component.html',
})
export class CourseDetailComponent implements OnInit {

    private readonly route = inject(ActivatedRoute);

    private readonly courseService =
        inject(CourseService);


    // =========================
    // COURSE
    // =========================

    readonly course =
        signal<Course | null>(null);

    readonly loading =
        signal(true);

    readonly error =
        signal<string | null>(null);


    // =========================
    // MODULES
    // =========================

    readonly modules =
        signal<Module[]>([]);

    readonly loadingModules =
        signal(true);

    readonly moduleError =
        signal<string | null>(null);


    // =========================
    // PROGRESS
    // =========================

    readonly progress =
        signal<CourseProgress | null>(null);

    readonly loadingProgress =
        signal(true);

    readonly certificate =
        signal<Certificate | null>(null);

    readonly certificateLoading =
        signal(false);

    readonly certificateError =
        signal<string | null>(null);



    // =========================
    // INIT
    // =========================

    ngOnInit(): void {

        const idParam =
            this.route.snapshot.paramMap.get('id');

        console.log(
            '========== COURSE DETAIL =========='
        );

        console.log(
            'COURSE ID FROM URL:',
            idParam
        );


        if (!idParam) {

            this.error.set(
                'Course ID is missing.'
            );

            this.loading.set(false);

            return;
        }


        const courseId =
            Number(idParam);


        if (Number.isNaN(courseId)) {

            this.error.set(
                'Invalid course ID.'
            );

            this.loading.set(false);

            return;
        }


        // =========================
        // LOAD COURSE
        // =========================

        console.log(
            'CALLING COURSE API:',
            `http://127.0.0.1:8082/api/courses/${courseId}`
        );


        this.courseService
            .getCourseById(courseId)
            .subscribe({

                next: (course) => {

                    console.log(
                        'COURSE DETAIL RESPONSE:',
                        course
                    );

                    this.course.set(course);

                    this.loading.set(false);

                    // Load modules after course
                    this.loadModules(courseId);

                    // Load progress
                    this.loadProgress(courseId);
                },


                error: (error) => {

                    console.error(
                        'COURSE DETAIL API ERROR:',
                        error
                    );

                    this.error.set(
                        'Unable to load course details.'
                    );

                    this.loading.set(false);
                },

            });
    }


    // =========================
    // LOAD MODULES
    // =========================

    private loadModules(
        courseId: number
    ): void {

        console.log(
            '========== LOADING MODULES =========='
        );

        console.log(
            'COURSE ID:',
            courseId
        );


        this.loadingModules.set(true);

        this.moduleError.set(null);


        this.courseService
            .getModulesByCourse(courseId)
            .subscribe({

                next: (modules) => {

                    console.log(
                        'MODULES FROM COURSE SERVICE:',
                        modules
                    );

                    this.modules.set(modules);

                    this.loadingModules.set(false);
                },


                error: (error) => {

                    console.error(
                        'MODULE API ERROR:',
                        error
                    );

                    this.moduleError.set(
                        'Unable to load course modules.'
                    );

                    this.loadingModules.set(false);
                },

            });
    }


    // =========================
    // LOAD PROGRESS
    // =========================

    private loadProgress(
        courseId: number
    ): void {

        console.log(
            '========== LOADING PROGRESS =========='
        );

        console.log(
            'COURSE ID:',
            courseId
        );


        this.loadingProgress.set(true);


        this.courseService
            .getCourseProgress(courseId)
            .subscribe({

                next: (progress) => {

                    console.log(
                        'COURSE PROGRESS:',
                        progress
                    );

                    this.progress.set(progress);

                    this.loadingProgress.set(false);
                },


                error: (error) => {

                    console.error(
                        'PROGRESS API ERROR:',
                        error
                    );

                    /*
                     * If the user has not started the course yet,
                     * we can treat it as 0%.
                     */
                    this.progress.set(null);

                    this.loadingProgress.set(false);
                },

            });
    }


    // =========================
    // PROGRESS PERCENTAGE
    // =========================

    get progressPercentage(): number {

        const p =
            this.progress();

        if (!p) {
            return 0;
        }

        return Number(
            p.progressPercentage
        );
    }


    // =========================
    // LEARNING BUTTON TEXT
    // =========================

    get learningButtonText(): string {

        const percentage =
            this.progressPercentage;


        if (percentage >= 100) {

            return 'Completed';
        }


        if (percentage > 0) {

            return 'Continue Learning';
        }


        return 'Start Learning';
    }


    // =========================
    // COMPLETED
    // =========================

    get courseCompleted(): boolean {

        return this.progressPercentage >= 100;
    }


    // =========================
    // LEARNING AVAILABLE
    // =========================

    get canStartLearning(): boolean {

        return !this.courseCompleted;
    }

    generateCertificate(): void {

        const courseId = this.course()?.id;

        if (!courseId) {
            return;
        }

        this.certificateLoading.set(true);
        this.certificateError.set(null);

        this.courseService
            .generateCertificate(courseId)
            .subscribe({

                next: certificate => {

                    console.log(
                        'CERTIFICATE GENERATED:',
                        certificate
                    );

                    this.certificate.set(
                        certificate
                    );

                    this.certificateLoading.set(false);
                },

                error: error => {

                    console.error(
                        'CERTIFICATE ERROR:',
                        error
                    );

                    this.certificateError.set(
                        error?.error?.message ??
                        'Unable to generate certificate.'
                    );

                    this.certificateLoading.set(false);
                }
            });
    }

    downloadCertificate(): void {

        const courseId = this.course()?.id;

        if (!courseId) {
            return;
        }

        this.courseService
            .downloadCertificate(courseId)
            .subscribe({

                next: blob => {

                    const url =
                        window.URL.createObjectURL(blob);

                    const link =
                        document.createElement('a');

                    link.href = url;

                    link.download =
                        `CloudPath-Certificate-${courseId}.pdf`;

                    link.click();

                    window.URL.revokeObjectURL(url);
                },

                error: error => {

                    console.error(
                        'CERTIFICATE DOWNLOAD ERROR:',
                        error
                    );

                    this.certificateError.set(
                        'Unable to download certificate.'
                    );
                }
            });
    }
}