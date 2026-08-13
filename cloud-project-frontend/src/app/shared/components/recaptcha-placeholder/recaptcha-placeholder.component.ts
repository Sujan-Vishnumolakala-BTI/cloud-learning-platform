import { Component, EventEmitter, Output, signal } from '@angular/core';

/**
 * UI-only reCAPTCHA placeholder.
 * Swap the click handler for the real grecaptcha widget once the backend
 * team provides a site key — no other component needs to change since
 * this emits the same `checked` boolean either way.
 */
@Component({
  selector: 'app-recaptcha-placeholder',
  standalone: true,
  templateUrl: './recaptcha-placeholder.component.html',
})
export class RecaptchaPlaceholderComponent {
  @Output() checkedChange = new EventEmitter<boolean>();
  readonly checked = signal(false);
  readonly verifying = signal(false);

  toggle(): void {
    if (this.checked()) return;
    this.verifying.set(true);
    setTimeout(() => {
      this.verifying.set(false);
      this.checked.set(true);
      this.checkedChange.emit(true);
    }, 700);
  }
}
