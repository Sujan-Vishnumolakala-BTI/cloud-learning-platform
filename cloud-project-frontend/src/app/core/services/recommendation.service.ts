import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RecommendationCourse {
  course_id: string;
  course_title: string;
  difficulty: string;
  score: number;
  skills: string[];
}

export interface RecommendationResponse {
  recommendations: RecommendationCourse[];
}

export interface RecommendationCourseInput {
  course_id: string;
  course_title: string;
  difficulty: string;
  skills: Record<string, number>;
}

export interface RecommendationRequest {
  student_skills: Record<string, number>;
  courses: RecommendationCourseInput[];
  completed_courses: string[];
  enrolled_courses: string[];
  top_n: number;
}

@Injectable({
  providedIn: 'root'
})
export class RecommendationService {

  private readonly http = inject(HttpClient);

  private readonly API_URL =
    'http://127.0.0.1:8000/api/recommendations';

  getRecommendations(
    request: RecommendationRequest
  ): Observable<RecommendationResponse> {

    return this.http.post<RecommendationResponse>(
      this.API_URL,
      request
    );
  }
}