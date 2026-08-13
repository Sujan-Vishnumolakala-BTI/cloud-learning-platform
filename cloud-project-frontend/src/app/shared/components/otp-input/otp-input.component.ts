import { Component, ElementRef, EventEmitter, Output, QueryList, ViewChildren, Input } from '@angular/core';

@Component({
  selector: 'app-otp-input',
  standalone: true,
  templateUrl: './otp-input.component.html',
})
export class OtpInputComponent {
  @Input() length = 6;
  @Input() hasError = false;
  @Output() codeChange = new EventEmitter<string>();
  @Output() completed = new EventEmitter<string>();

  @ViewChildren('digit') digitRefs!: QueryList<ElementRef<HTMLInputElement>>;

  digits: string[] = Array(this.length).fill('');

  get boxes(): number[] {
    return Array.from({ length: this.length }, (_, i) => i);
  }

  onInput(index: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = input.value.replace(/[^0-9]/g, '').slice(-1);
    this.digits[index] = value;
    input.value = value;

    if (value && index < this.length - 1) {
      this.digitRefs.get(index + 1)?.nativeElement.focus();
    }
    this.emit();
  }

  onKeydown(index: number, event: KeyboardEvent): void {
    if (event.key === 'Backspace' && !this.digits[index] && index > 0) {
      this.digitRefs.get(index - 1)?.nativeElement.focus();
    }
  }

  onPaste(event: ClipboardEvent): void {
    event.preventDefault();
    const pasted = event.clipboardData?.getData('text').replace(/[^0-9]/g, '') ?? '';
    for (let i = 0; i < this.length; i++) {
      this.digits[i] = pasted[i] ?? '';
      const ref = this.digitRefs.get(i)?.nativeElement;
      if (ref) ref.value = this.digits[i];
    }
    const nextEmpty = this.digits.findIndex((d) => !d);
    this.digitRefs.get(nextEmpty === -1 ? this.length - 1 : nextEmpty)?.nativeElement.focus();
    this.emit();
  }

  private emit(): void {
    const code = this.digits.join('');
    this.codeChange.emit(code);
    if (code.length === this.length && this.digits.every((d) => d !== '')) {
      this.completed.emit(code);
    }
  }

  reset(): void {
    this.digits = Array(this.length).fill('');
    this.digitRefs.forEach((ref) => (ref.nativeElement.value = ''));
    this.digitRefs.first?.nativeElement.focus();
  }
}
