import { Component, HostListener, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  readonly scrolled = signal(false);
  readonly mobileOpen = signal(false);
  readonly profileOpen = signal(false);

  readonly links = [
    { label: 'Home', path: '/' },
    { label: 'Courses', path: '/', fragment: 'courses' },
    { label: 'About', path: '/about' },
    { label: 'Contact', path: '/contact' },
  ];

  constructor(public auth: AuthService, private router: Router) {}

  @HostListener('window:scroll')
  onScroll(): void {
    this.scrolled.set(window.scrollY > 12);
  }

  toggleMobile(): void {
    this.mobileOpen.update((v) => !v);
  }

  closeMobile(): void {
    this.mobileOpen.set(false);
  }

  toggleProfile(): void {
    this.profileOpen.update((v) => !v);
  }

  logout(): void {
    this.auth.logout();
    this.profileOpen.set(false);
    this.mobileOpen.set(false);
    this.router.navigate(['/']);
  }

  firstName(): string {
    const name = this.auth.currentUser()?.fullName ?? '';
    return name.split(' ')[0];
  }
}
