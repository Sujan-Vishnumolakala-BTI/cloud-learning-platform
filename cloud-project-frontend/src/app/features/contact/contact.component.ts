import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { delay, of } from 'rxjs';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './contact.component.html',
})
export class ContactComponent {
  // 1. Inject dependency directly as a class field
  private fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly submitted = signal(false);

  // 2. This can now safely reference 'this.fb' during initialization
  readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    subject: ['', [Validators.required]],
    message: ['', [Validators.required, Validators.minLength(10)]],
  });

  readonly contactCards = [
    { label: 'Email support', value: 'support@cloudpath.dev', d: 'M4 4h16v16H4zM4 6l8 6 8-6' },
    { label: 'Phone number', value: '+1 (555) 010-2024', d: 'M22 16.9v3a2 2 0 0 1-2.2 2 19.8 19.8 0 0 1-8.6-3 19.5 19.5 0 0 1-6-6A19.8 19.8 0 0 1 2.2 4.2 2 2 0 0 1 4.2 2h3a2 2 0 0 1 2 1.7c.1.9.3 1.8.6 2.7a2 2 0 0 1-.5 2.1L8 9.8a16 16 0 0 0 6 6l1.3-1.3a2 2 0 0 1 2.1-.5c.9.3 1.8.5 2.7.6a2 2 0 0 1 1.7 2Z' },
    { label: 'Office address', value: '221 Cloud Ave, Chennai, IN', d: 'M12 21s7-6.2 7-11a7 7 0 1 0-14 0c0 4.8 7 11 7 11ZM12 13a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5Z' },
  ];

  readonly socials = ['Twitter', 'LinkedIn', 'GitHub', 'YouTube'];

  // 3. Keep an empty constructor
  constructor() {}

  get f() {
    return this.form.controls;
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    // Frontend-only simulation — replace with POST /api/contact once backend is ready.
    of(true)
      .pipe(delay(1000))
      .subscribe(() => {
        this.loading.set(false);
        this.submitted.set(true);
        this.form.reset();
      });
  }
}
