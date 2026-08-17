import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CreateEnrollmentRequest {
  courseId: number;
}

export interface EnrollmentResponse {
  id: number;
  userId: number;
  courseId: number;
  status: string;
  enrolledAt: string;
  completedAt: string | null;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class EnrollmentService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    'http://localhost:8080/api/enrollments';

  /**
   * Enroll the currently authenticated user
   * into a course.
   */
  enroll(courseId: number): Observable<EnrollmentResponse> {
    return this.http.post<EnrollmentResponse>(
      this.apiUrl,
      { courseId }
    );
  }

  /**
   * Get enrollments belonging to the
   * currently authenticated user.
   */
  getMyEnrollments(): Observable<EnrollmentResponse[]> {
    return this.http.get<EnrollmentResponse[]>(
      `${this.apiUrl}/my`
    );
  }

  /**
   * Get a specific enrollment.
   */
  getEnrollment(id: number): Observable<EnrollmentResponse> {
    return this.http.get<EnrollmentResponse>(
      `${this.apiUrl}/${id}`
    );
  }

  /**
   * Cancel an enrollment.
   */
  cancelEnrollment(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}