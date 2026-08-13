
import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';

import { RecaptchaPlaceholderComponent } from '../../../shared/components/recaptcha-placeholder/recaptcha-placeholder.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    RecaptchaPlaceholderComponent,
  ],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  // Inject dependencies
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  // Component state
  readonly loading = signal(false);
  readonly showPassword = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly captchaVerified = signal(false);

  // Login form
  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    rememberMe: [false],
  });

  constructor() { }

  get f() {
    return this.form.controls;
  }

  togglePassword(): void {
    this.showPassword.update((v) => !v);
  }

  onCaptchaChecked(v: boolean): void {
    this.captchaVerified.set(v);
  }

  submit(): void {
    // Clear previous error
    this.errorMessage.set(null);

    // Validate form
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    // Validate CAPTCHA
    if (!this.captchaVerified()) {
      this.errorMessage.set(
        'Please verify that you are not a robot.'
      );
      return;
    }

    // Start loading
    this.loading.set(true);

    const {
      email,
      password,
      rememberMe,
    } = this.form.getRawValue();

    // Login
    this.auth
      .login({
        email,
        password,
        rememberMe,
      })
      .subscribe({
        next: (response) => {
          this.loading.set(false);

          const role = response.role;

          switch (role) {
            case 'admin':
              this.router.navigate(['/admin']);
              break;

            case 'instructor':
              this.router.navigate(['/instructor']);
              break;

            case 'student':
            default:
              // Check whether the student has completed
              // the skill assessment.
              this.auth.getMySkills().subscribe({
                next: (skillsResponse) => {
                  const skills = skillsResponse.skills ?? [];

                  if (skills.length === 0) {
                    // New student → show skill assessment
                    this.router.navigate(['/skill-assessment']);
                  } else {
                    // Existing student → dashboard
                    this.router.navigate(['/dashboard']);
                  }
                },

                error: (err) => {
                  console.error('Failed to check user skills:', err);

                  this.errorMessage.set(
                    'Unable to check your skill profile. Please try again.'
                  );
                },
              });

              break;
          }
        },
        error: (err: Error) => {
          this.loading.set(false);

          this.errorMessage.set(
            err.message ||
            'Something went wrong. Please try again.'
          );
        },
      });
  }

  loginWithGoogle(): void {
    // UI-only placeholder — wire up real OAuth once
    // backend authentication is connected.
    this.errorMessage.set(
      'Google login will be available once backend authentication is connected.'
    );
  }
}
