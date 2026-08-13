import {
  Component,
  OnInit,
  inject,
  signal,
} from '@angular/core';

import {
  ActivatedRoute,
  Router,
} from '@angular/router';

import { forkJoin } from 'rxjs';

import { CourseService } from '../../core/services/course.service';

import {
  Quiz,
  QuizAttempt,
  QuizQuestionWithOptions,
  SubmitQuizRequest,
} from '../../core/models/course.model';


@Component({
  selector: 'app-quiz',
  standalone: true,
  imports: [],
  templateUrl: './quiz.component.html',
})
export class QuizComponent implements OnInit {

  private readonly route =
    inject(ActivatedRoute);

  private readonly router =
    inject(Router);

  private readonly courseService =
    inject(CourseService);


  // =========================================================
  // QUIZ
  // =========================================================

  readonly quiz =
    signal<Quiz | null>(null);


  // =========================================================
  // QUESTIONS
  // =========================================================

  readonly questions =
    signal<QuizQuestionWithOptions[]>([]);


  // =========================================================
  // PREVIOUS ATTEMPTS
  // =========================================================

  readonly attempts =
    signal<QuizAttempt[]>([]);


  // =========================================================
  // SELECTED ANSWERS
  // =========================================================

  readonly selectedAnswers =
    signal<Record<number, number>>({});


  // =========================================================
  // CURRENT ATTEMPT
  // =========================================================

  readonly currentAttempt =
    signal<QuizAttempt | null>(null);


  // =========================================================
  // RESULT
  // =========================================================

  readonly result =
    signal<QuizAttempt | null>(null);


  // =========================================================
  // UI STATE
  // =========================================================

  readonly loading =
    signal(true);

  readonly starting =
    signal(false);

  readonly submitting =
    signal(false);


  readonly error =
    signal<string | null>(null);


  // =========================================================
  // ROUTE IDS
  // =========================================================

  readonly courseId =
    signal<number | null>(null);

  readonly lessonId =
    signal<number | null>(null);


  // =========================================================
  // INIT
  // =========================================================

  ngOnInit(): void {

    console.log(
      '========== QUIZ COMPONENT =========='
    );


    const courseId =
      Number(
        this.route.snapshot.paramMap.get(
          'courseId'
        )
      );


    const lessonId =
      Number(
        this.route.snapshot.paramMap.get(
          'lessonId'
        )
      );


    console.log(
      'COURSE ID:',
      courseId
    );

    console.log(
      'LESSON ID:',
      lessonId
    );


    if (
      !courseId ||
      !lessonId ||
      Number.isNaN(courseId) ||
      Number.isNaN(lessonId)
    ) {

      this.error.set(
        'Invalid course or lesson ID.'
      );

      this.loading.set(false);

      return;
    }


    this.courseId.set(
      courseId
    );

    this.lessonId.set(
      lessonId
    );


    this.loadQuiz(
      lessonId
    );
  }


  // =========================================================
  // LOAD QUIZ
  // =========================================================

  private loadQuiz(
    lessonId: number
  ): void {

    this.loading.set(true);

    this.error.set(null);


    console.log(
      '========== LOADING QUIZ =========='
    );

    console.log(
      'LESSON ID:',
      lessonId
    );


    this.courseService
      .getQuizByLesson(lessonId)
      .subscribe({

        next: quiz => {

          console.log(
            'QUIZ RESPONSE:',
            quiz
          );


          this.quiz.set(
            quiz
          );


          /*
           * Load questions
           */

          this.loadQuestions(
            quiz.id
          );


          /*
           * Load previous attempts
           */

          this.loadAttempts(
            quiz.id
          );
        },


        error: error => {

          console.error(
            'QUIZ API ERROR:',
            error
          );


          this.error.set(
            'No quiz is available for this lesson.'
          );


          this.loading.set(false);
        },

      });
  }


  // =========================================================
  // LOAD QUESTIONS + OPTIONS
  // =========================================================

  private loadQuestions(
    quizId: number
  ): void {

    console.log(
      '========== LOADING QUESTIONS =========='
    );

    console.log(
      'QUIZ ID:',
      quizId
    );


    this.courseService
      .getQuizQuestions(quizId)
      .subscribe({

        next: questions => {

          console.log(
            'QUESTIONS:',
            questions
          );


          if (
            questions.length === 0
          ) {

            this.questions.set(
              []
            );

            this.loading.set(false);

            return;
          }


          /*
           * Get options for every question
           */

          const optionRequests =
            questions.map(
              question =>
                this.courseService
                  .getQuestionOptions(
                    question.id
                  )
            );


          forkJoin(
            optionRequests
          ).subscribe({

            next: optionsList => {

              console.log(
                'OPTIONS:',
                optionsList
              );


              const combined:
                QuizQuestionWithOptions[] =
                questions.map(
                  (question, index) => ({

                    ...question,

                    options:
                      optionsList[index],

                  })
                );


              console.log(
                'FINAL QUIZ QUESTIONS:',
                combined
              );


              this.questions.set(
                combined
              );


              this.loading.set(
                false
              );
            },


            error: error => {

              console.error(
                'OPTIONS API ERROR:',
                error
              );


              this.error.set(
                'Unable to load quiz options.'
              );


              this.loading.set(false);
            },

          });
        },


        error: error => {

          console.error(
            'QUESTIONS API ERROR:',
            error
          );


          this.error.set(
            'Unable to load quiz questions.'
          );


          this.loading.set(false);
        },

      });
  }


  // =========================================================
  // LOAD PREVIOUS ATTEMPTS
  // =========================================================

  private loadAttempts(
    quizId: number
  ): void {

    console.log(
      'LOADING MY QUIZ ATTEMPTS:',
      quizId
    );


    this.courseService
      .getMyQuizAttempts(quizId)
      .subscribe({

        next: attempts => {

          console.log(
            'MY QUIZ ATTEMPTS:',
            attempts
          );


          this.attempts.set(
            attempts
          );
        },


        error: error => {

          console.error(
            'ATTEMPTS API ERROR:',
            error
          );

        },

      });
  }


  // =========================================================
  // SELECT ANSWER
  // =========================================================

  selectAnswer(
    questionId: number,
    optionId: number
  ): void {

    /*
     * Don't allow changing answers
     * after quiz submission.
     */

    if (
      this.result()
    ) {

      return;
    }


    this.selectedAnswers.update(
      current => ({

        ...current,

        [questionId]:
          optionId,

      })
    );


    console.log(
      'SELECTED ANSWERS:',
      this.selectedAnswers()
    );
  }


  // =========================================================
  // GET SELECTED ANSWER
  // =========================================================

  getSelectedAnswer(
    questionId: number
  ): number | null {

    return (
      this.selectedAnswers()[
        questionId
      ] ?? null
    );
  }


  // =========================================================
  // START QUIZ
  // =========================================================

  startQuiz(): void {

    const quiz =
      this.quiz();


    if (!quiz) {

      return;
    }


    console.log(
      '========== START QUIZ =========='
    );


    console.log(
      'QUIZ ID:',
      quiz.id
    );


    this.starting.set(
      true
    );

    this.error.set(
      null
    );


    this.courseService
      .startQuizAttempt(
        quiz.id
      )
      .subscribe({

        next: attempt => {

          console.log(
            'QUIZ ATTEMPT STARTED:',
            attempt
          );


          this.currentAttempt.set(
            attempt
          );


          this.starting.set(
            false
          );
        },


        error: error => {

          console.error(
            'START QUIZ ERROR:',
            error
          );


          this.error.set(
            error?.error?.message ??
            'Unable to start quiz.'
          );


          this.starting.set(
            false
          );
        },

      });
  }


  // =========================================================
  // SUBMIT QUIZ
  // =========================================================

  submitQuiz(): void {

    const attempt =
      this.currentAttempt();


    const questions =
      this.questions();


    if (!attempt) {

      this.error.set(
        'Please start the quiz first.'
      );

      return;
    }


    if (
      questions.length === 0
    ) {

      this.error.set(
        'There are no questions in this quiz.'
      );

      return;
    }


    const selected =
      this.selectedAnswers();


    /*
     * Check unanswered questions
     */

    const missing =
      questions.find(
        question =>
          selected[question.id] === undefined
      );


    if (missing) {

      this.error.set(
        `Please answer question ${missing.orderIndex}.`
      );

      return;
    }


    /*
     * Build backend request
     */

    const request:
      SubmitQuizRequest = {

        answers:
          questions.map(
            question => ({

              questionId:
                question.id,

              optionId:
                selected[
                  question.id
                ],

            })
          ),

      };


    console.log(
      '========== SUBMIT QUIZ =========='
    );


    console.log(
      'ATTEMPT ID:',
      attempt.id
    );


    console.log(
      'SUBMIT REQUEST:',
      request
    );


    this.submitting.set(
      true
    );

    this.error.set(
      null
    );


    this.courseService
      .submitQuizAttempt(
        attempt.id,
        request
      )
      .subscribe({

        next: result => {

          console.log(
            'QUIZ RESULT:',
            result
          );


          this.result.set(
            result
          );


          this.currentAttempt.set(
            null
          );


          this.submitting.set(
            false
          );


          /*
           * Refresh previous attempts
           */

          this.loadAttempts(
            result.quizId
          );
        },


        error: error => {

          console.error(
            'SUBMIT QUIZ ERROR:',
            error
          );


          this.error.set(
            error?.error?.message ??
            'Unable to submit quiz.'
          );


          this.submitting.set(
            false
          );
        },

      });
  }


  // =========================================================
  // RETAKE QUIZ
  // =========================================================

  retakeQuiz(): void {

    console.log(
      'RETAKING QUIZ'
    );


    this.selectedAnswers.set(
      {}
    );


    this.result.set(
      null
    );


    this.currentAttempt.set(
      null
    );


    this.error.set(
      null
    );
  }


  // =========================================================
  // BACK TO COURSE LEARNING
  // =========================================================

  backToLearning(): void {

    const courseId =
      this.courseId();


    if (!courseId) {

      return;
    }


    this.router.navigate([
      '/courses',
      courseId,
      'learn',
    ]);
  }


  // =========================================================
  // COMPLETED ATTEMPTS
  // =========================================================

  get completedAttempts(): QuizAttempt[] {

    return this.attempts()
      .filter(
        attempt =>
          attempt.status ===
          'COMPLETED'
      );
  }
}