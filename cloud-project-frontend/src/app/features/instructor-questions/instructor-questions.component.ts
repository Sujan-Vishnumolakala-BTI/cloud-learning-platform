import {
  Component,
  OnInit,
  inject
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
  HttpErrorResponse
} from '@angular/common/http';

import {
  CourseService
} from '../../core/services/course.service';

import {
  Question,
  QuestionOption
} from '../../core/models/question.model';


interface QuestionWithOptions {

  id: number;

  quizId: number;

  questionText: string;

  orderIndex: number;

  createdAt: string;

  updatedAt: string;

  options: QuestionOption[];
}


interface NewOption {

  optionText: string;

  correct: boolean;
}


@Component({
  selector:
    'app-instructor-questions',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule
  ],

  templateUrl:
    './instructor-questions.component.html'
})
export class InstructorQuestionsComponent
  implements OnInit {


  // =========================================================
  // DEPENDENCIES
  // =========================================================

  private readonly route =
    inject(ActivatedRoute);

  private readonly router =
    inject(Router);

  private readonly courseService =
    inject(CourseService);


  // =========================================================
  // ROUTE IDs
  // =========================================================

  courseId!: number;

  quizId!: number;


  // =========================================================
  // DATA
  // =========================================================

  questions:
    QuestionWithOptions[] = [];


  // =========================================================
  // STATE
  // =========================================================

  loading = true;

  saving = false;

  errorMessage = '';

  successMessage = '';


  // =========================================================
  // QUESTION FORM
  // =========================================================

  showQuestionForm = false;

  questionText = '';

  orderIndex = 1;


  // =========================================================
  // OPTIONS
  // =========================================================

  options: NewOption[] = [

    {
      optionText: '',
      correct: false
    },

    {
      optionText: '',
      correct: false
    },

    {
      optionText: '',
      correct: false
    },

    {
      optionText: '',
      correct: false
    }

  ];


  // =========================================================
  // INIT
  // =========================================================

  ngOnInit(): void {

    this.courseId =
      Number(
        this.route.snapshot.paramMap.get(
          'courseId'
        )
      );

    this.quizId =
      Number(
        this.route.snapshot.paramMap.get(
          'quizId'
        )
      );


    console.log(
      '================================'
    );

    console.log(
      'INSTRUCTOR QUESTIONS'
    );

    console.log(
      'COURSE ID:',
      this.courseId
    );

    console.log(
      'QUIZ ID:',
      this.quizId
    );

    console.log(
      '================================'
    );


    if (
      !this.courseId ||
      !this.quizId
    ) {

      this.errorMessage =
        'Invalid course or quiz ID.';

      this.loading = false;

      return;
    }


    this.loadQuestions();
  }


  // =========================================================
  // LOAD QUESTIONS
  // =========================================================

  loadQuestions(): void {

    this.loading = true;

    this.errorMessage = '';


    this.courseService
      .getQuestionsByQuiz(
        this.quizId
      )
      .subscribe({

        next: (
          questions: Question[]
        ) => {

          console.log(
            'QUESTIONS:',
            questions
          );


          if (
            !questions ||
            questions.length === 0
          ) {

            this.questions = [];

            this.loading = false;

            return;
          }


          /*
           * Load options for every question.
           */

          let completed = 0;


          const result:
            QuestionWithOptions[] =
              questions.map(
                question => ({

                  ...question,

                  options: []

                })
              );


          questions.forEach(
            (
              question,
              index
            ) => {

              this.courseService
                .getQuestionOptions(
                  question.id
                )
                .subscribe({

                  next: (
                    options:
                      QuestionOption[]
                  ) => {

                    result[index].options =
                      options;

                    completed++;


                    if (
                      completed ===
                      questions.length
                    ) {

                      this.questions =
                        result;

                      this.loading =
                        false;
                    }

                  },

                  error: (
                    error:
                      HttpErrorResponse
                  ) => {

                    console.error(
                      'OPTIONS LOAD ERROR:',
                      error
                    );

                    /*
                     * Keep question even if
                     * options fail.
                     */

                    result[index].options =
                      [];

                    completed++;


                    if (
                      completed ===
                      questions.length
                    ) {

                      this.questions =
                        result;

                      this.loading =
                        false;
                    }

                  }

                });

            }
          );

        },

        error: (
          error:
            HttpErrorResponse
        ) => {

          console.error(
            'QUESTION LOAD ERROR:',
            error
          );

          this.errorMessage =
            error?.error?.message ??
            'Unable to load questions.';

          this.loading = false;
        }

      });
  }


  // =========================================================
  // OPEN CREATE FORM
  // =========================================================

  openQuestionForm(): void {

    this.showQuestionForm = true;

    this.questionText = '';

    this.orderIndex =
      this.questions.length + 1;

    this.errorMessage = '';

    this.successMessage = '';


    this.options = [

      {
        optionText: '',
        correct: false
      },

      {
        optionText: '',
        correct: false
      },

      {
        optionText: '',
        correct: false
      },

      {
        optionText: '',
        correct: false
      }

    ];
  }


  // =========================================================
  // CANCEL
  // =========================================================

  cancelQuestionForm(): void {

    this.showQuestionForm = false;

    this.questionText = '';

    this.errorMessage = '';
  }


  // =========================================================
  // ADD OPTION
  // =========================================================

  addOption(): void {

    this.options.push({

      optionText: '',

      correct: false

    });
  }


  // =========================================================
  // REMOVE OPTION
  // =========================================================

  removeOption(
    index: number
  ): void {

    if (
      this.options.length <= 2
    ) {

      return;
    }


    this.options.splice(
      index,
      1
    );
  }


  // =========================================================
  // SELECT CORRECT OPTION
  // =========================================================

  selectCorrectOption(
    index: number
  ): void {

    this.options.forEach(
      (
        option,
        currentIndex
      ) => {

        option.correct =
          currentIndex === index;

      }
    );
  }


  // =========================================================
  // SAVE QUESTION
  // =========================================================

  saveQuestion(): void {

    this.errorMessage = '';

    this.successMessage = '';


    // -------------------------------------------------------
    // QUESTION VALIDATION
    // -------------------------------------------------------

    if (
      !this.questionText.trim()
    ) {

      this.errorMessage =
        'Question text is required.';

      return;
    }


    if (
      !this.orderIndex ||
      this.orderIndex < 1
    ) {

      this.errorMessage =
        'Question order must be at least 1.';

      return;
    }


    // -------------------------------------------------------
    // VALID OPTIONS
    // -------------------------------------------------------

    const validOptions =
      this.options.filter(
        option =>
          option.optionText.trim()
            .length > 0
      );


    if (
      validOptions.length < 2
    ) {

      this.errorMessage =
        'At least 2 options are required.';

      return;
    }


    // -------------------------------------------------------
    // CORRECT ANSWER
    // -------------------------------------------------------

    const correctOptions =
      validOptions.filter(
        option =>
          option.correct
      );


    if (
      correctOptions.length !== 1
    ) {

      this.errorMessage =
        'Select exactly one correct answer.';

      return;
    }


    // -------------------------------------------------------
    // SAVE
    // -------------------------------------------------------

    this.saving = true;


    const request = {

      questionText:
        this.questionText.trim(),

      orderIndex:
        this.orderIndex

    };


    console.log(
      'CREATE QUESTION REQUEST:',
      request
    );


    this.courseService
      .createQuestion(
        this.quizId,
        request
      )
      .subscribe({

        next: (
          createdQuestion: Question
        ) => {

          console.log(
            'QUESTION CREATED:',
            createdQuestion
          );


          this.createOptions(
            createdQuestion.id,
            validOptions
          );

        },

        error: (
          error:
            HttpErrorResponse
        ) => {

          console.error(
            'CREATE QUESTION ERROR:',
            error
          );

          this.saving = false;

          this.errorMessage =
            error?.error?.message ??
            'Unable to create question.';
        }

      });
  }


  // =========================================================
  // CREATE OPTIONS
  // =========================================================

  createOptions(
    questionId: number,

    options: NewOption[]
  ): void {

    let completed = 0;

    let failed = false;


    options.forEach(
      option => {

        const request = {

          optionText:
            option.optionText.trim(),

          correct:
            option.correct

        };


        this.courseService
          .createQuestionOption(
            questionId,
            request
          )
          .subscribe({

            next: (
              createdOption:
                QuestionOption
            ) => {

              console.log(
                'OPTION CREATED:',
                createdOption
              );


              completed++;


              if (
                completed ===
                options.length &&
                !failed
              ) {

                this.saving = false;

                this.showQuestionForm =
                  false;

                this.successMessage =
                  'Question and options created successfully.';

                this.loadQuestions();
              }

            },

            error: (
              error:
                HttpErrorResponse
            ) => {

              console.error(
                'CREATE OPTION ERROR:',
                error
              );

              failed = true;

              this.saving = false;

              this.errorMessage =
                error?.error?.message ??
                'Question created, but an option could not be created.';
            }

          });

      }
    );
  }


  // =========================================================
  // BACK
  // =========================================================

  back(): void {

    this.router.navigate([
      '/instructor/courses',
      this.courseId,
      'quizzes'
    ]);
  }

}