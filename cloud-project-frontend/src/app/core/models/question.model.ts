// export interface Question {

//   id: number;

//   quizId: number;

//   questionText: string;

//   orderIndex: number;

//   createdAt: string;

//   updatedAt: string;
// }

// export interface QuestionOption {
//   id: number;
//   questionId: number;
//   optionText: string;
//   correct: boolean;
// }

export interface Question {

  id: number;

  quizId: number;

  questionText: string;

  orderIndex: number;

  createdAt: string;

  updatedAt: string;
}


export interface QuestionOption {

  id: number;

  questionId: number;

  optionText: string;

  correct: boolean;
}


export interface CreateQuestionRequest {

  questionText: string;

  orderIndex: number;
}


export interface CreateQuestionOptionRequest {

  optionText: string;

  correct: boolean;
}