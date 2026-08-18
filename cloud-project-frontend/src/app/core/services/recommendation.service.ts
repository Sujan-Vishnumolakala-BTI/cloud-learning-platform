import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RecommendationCourse {
  course_id: string;
  course_title: string;
  difficulty: string;
  score: number;
  similarity_score: number;
  skill_gap_score: number;
  skills: string[];
  generated_at?: string;
}

export interface RecommendationResponse {
  userId: number;
  recommendations: RecommendationCourse[];
}

@Injectable({
  providedIn: 'root'
})
export class RecommendationService {

  private readonly http = inject(HttpClient);

  private readonly API_URL =
    'http://localhost:8080/api/recommendations';

  getRecommendations(
    userId: number
  ): Observable<RecommendationResponse> {

    const token = localStorage.getItem('token');

    let headers = new HttpHeaders();

    if (token) {
      headers = headers.set(
        'Authorization',
        `Bearer ${token}`
      );
    }

    return this.http.get<RecommendationResponse>(
      `${this.API_URL}/${userId}`,
      {
        headers
      }
    );
  }
}