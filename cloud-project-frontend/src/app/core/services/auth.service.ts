import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map, tap, switchMap } from 'rxjs/operators';

import {
  LoginPayload,
  LoginResponse,
  RegisterPayload,
  StoredAuth,
  User,
  UserRole,
  UserSkillsResponse,
  SkillsResponse
} from '../models/user.model';

const STORAGE_KEY = 'cloudpath_auth';
const PENDING_KEY = 'cloudpath_pending_registration';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private readonly http = inject(HttpClient);

  private readonly userApiUrl =
    'http://127.0.0.1:8081/api/users';

  private readonly authApiUrl =
    'http://127.0.0.1:8081/api/auth';

  private readonly AUTH_API =
    'http://127.0.0.1:8081/api/auth';

  private readonly _currentUser =
    signal<User | null>(this.readStoredUser());

  readonly currentUser =
    this._currentUser.asReadonly();

  readonly isLoggedIn =
    computed(() => this._currentUser() !== null);

  /**
   * Read the logged-in user from localStorage.
   */
  private readStoredUser(): User | null {

    try {

      const raw =
        localStorage.getItem(STORAGE_KEY);

      if (!raw) {
        return null;
      }

      const parsed =
        JSON.parse(raw) as StoredAuth;

      return parsed.user ?? null;

    } catch {

      return null;
    }
  }

  /**
   * Store the real backend authentication session.
   */
  private persistSession(
    accessToken: string,
    refreshToken: string,
    user: User
  ): void {

    const session: StoredAuth = {
      accessToken,
      refreshToken,
      user,
    };

    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify(session)
    );

    this._currentUser.set(user);
  }

  /**
   * Convert backend UserResponse into
   * the frontend User model.
   */
  private mapUser(
    backendUser: any
  ): User {

    const role =
      String(backendUser.role)
        .toLowerCase() as UserRole;

    return {
      id: String(backendUser.id),

      fullName: [
        backendUser.firstName,
        backendUser.lastName,
      ]
        .filter(Boolean)
        .join(' '),

      email: backendUser.email,

      role,

      avatarUrl:
        backendUser.avatarUrl ??
        `https://api.dicebear.com/7.x/notionists/svg?seed=${encodeURIComponent(
          backendUser.email
        )}`,

      enrolledCourseIds: [],

      completedCourseIds: [],

      createdAt:
        backendUser.createdAt,
    };
  }

  /**
   * REAL BACKEND LOGIN
   *
   * POST /api/auth/login
   *
   * Then:
   *
   * GET /api/users/me
   */
  login(
    payload: LoginPayload
  ): Observable<User> {

    if (
      !payload.email ||
      !payload.password
    ) {

      return throwError(
        () =>
          new Error(
            'Email and password are required.'
          )
      );
    }

    const request = {

      email:
        payload.email
          .trim()
          .toLowerCase(),

      password:
        payload.password,
    };

    return this.http
      .post<LoginResponse>(
        `${this.authApiUrl}/login`,
        request
      )
      .pipe(

        /**
         * Save tokens BEFORE calling /me.
         *
         * This allows the HTTP interceptor to
         * attach the access token to /api/users/me.
         */
        tap((response) => {

          localStorage.setItem(
            STORAGE_KEY,
            JSON.stringify({
              accessToken:
                response.accessToken,

              refreshToken:
                response.refreshToken,

              user: null,
            })
          );
        }),

        /**
         * Now request the actual authenticated
         * user from User Service.
         */
        switchMap((response) =>

          this.http
            .get<any>(
              `${this.userApiUrl}/me`
            )
            .pipe(

              switchMap((backendUser) => {

                const user =
                  this.mapUser(
                    backendUser
                  );

                this.persistSession(
                  response.accessToken,
                  response.refreshToken,
                  user
                );

                return new Observable<User>((subscriber) => {
                  subscriber.next(user);
                  subscriber.complete();
                });
                // return [
                //   user
                // ];
              })
            )
        ),

        catchError((error) => {

          let message =
            'Login failed. Please try again.';

          if (
            error?.status === 401
          ) {

            message =
              'Invalid email or password.';
          }

          if (
            error?.status === 403
          ) {

            message =
              'Your account does not have access.';
          }

          if (
            error?.status === 0
          ) {

            message =
              'Unable to connect to User Service on port 8081.';
          }

          return throwError(
            () =>
              new Error(message)
          );
        })
      );
  }

  /**
   * Get stored access token.
   */
  getToken(): string | null {

    try {

      const raw =
        localStorage.getItem(
          STORAGE_KEY
        );

      if (!raw) {
        return null;
      }

      const parsed =
        JSON.parse(raw) as StoredAuth;

      return parsed.accessToken ?? null;

    } catch {

      return null;
    }
  }

  /**
   * Get stored refresh token.
   */
  getRefreshToken(): string | null {

    try {

      const raw =
        localStorage.getItem(
          STORAGE_KEY
        );

      if (!raw) {
        return null;
      }

      const parsed =
        JSON.parse(raw) as StoredAuth;

      return parsed.refreshToken ?? null;

    } catch {

      return null;
    }
  }

  /**
   * Logout.
   *
   * Backend logout integration can be added
   * after login is confirmed.
   */
  logout(): void {

    localStorage.removeItem(
      STORAGE_KEY
    );

    this._currentUser.set(null);
  }

  /**
   * ------------------------------------------------
   * REGISTRATION / OTP
   * ------------------------------------------------
   *
   * Leave these methods temporarily so your
   * existing registration components continue
   * compiling.
   */
  /*
 * =========================================================
 * REGISTRATION / OTP
 * =========================================================
 */

  register(
    payload: RegisterPayload
  ): Observable<{ email: string }> {

    const request = {
      firstName: payload.firstName,
      lastName: payload.lastName,
      email: payload.email.trim().toLowerCase(),
      password: payload.password,
      role: payload.role.toUpperCase()
    };

    return this.http
      .post<{ email: string; message?: string }>(
        `${this.AUTH_API}/register`,
        request
      )
      .pipe(

        tap(() => {

          /*
           * Save registration information so
           * the Verify OTP page knows which
           * email is being verified.
           */
          sessionStorage.setItem(
            PENDING_KEY,
            JSON.stringify(payload)
          );

        }),

        map(response => ({
          email:
            response.email ??
            payload.email
        }))
      );
  }


  getPendingEmail(): string | null {

    const raw =
      sessionStorage.getItem(PENDING_KEY);

    if (!raw) {
      return null;
    }

    try {

      return (
        JSON.parse(raw) as RegisterPayload
      ).email;

    } catch {

      return null;
    }
  }


  verifyOtp(
    otp: string
  ): Observable<User> {

    const email =
      this.getPendingEmail();

    if (!email) {

      return throwError(
        () =>
          new Error(
            'No pending registration found. Please register again.'
          )
      );
    }

    if (
      !otp ||
      otp.length !== 6
    ) {

      return throwError(
        () =>
          new Error(
            'Enter the complete 6-digit code.'
          )
      );
    }

    return this.http
      .post<any>(
        `${this.AUTH_API}/verify-otp`,
        {
          email,
          otp
        }
      )
      .pipe(

        map(response => {

          /*
           * If verification endpoint returns
           * the user directly.
           */
          if (response?.user) {
            return this.mapUser(response.user);
          }

          /*
           * If backend returns only a success
           * response, we return a minimal user.
           *
           * Login will fetch the complete session.
           */
          return {
            id: '',
            fullName: '',
            email,
            role: 'student',
            enrolledCourseIds: [],
            completedCourseIds: [],
            createdAt: new Date().toISOString()
          } as User;

        }),

        tap(() => {

          sessionStorage.removeItem(
            PENDING_KEY
          );

        })
      );
  }


  resendOtp(): Observable<{
    sent: boolean;
  }> {

    const email =
      this.getPendingEmail();

    if (!email) {

      return throwError(
        () =>
          new Error(
            'No pending registration found.'
          )
      );
    }

    return this.http
      .post<any>(
        `${this.AUTH_API}/resend-otp`,
        {
          email
        }
      )
      .pipe(

        map(response => ({
          sent:
            response?.sent ??
            true
        }))
      );
  }


  getMySkills(): Observable<UserSkillsResponse> {

    return this.http.get<UserSkillsResponse>(
      `${this.userApiUrl}/me/skills`
    );
  }
  saveMySkills(
    skills: SkillsResponse[]
  ): Observable<UserSkillsResponse> {

    return this.http.put<UserSkillsResponse>(
      `${this.userApiUrl}/me/skills`,
      { skills }
    );
  }

}
