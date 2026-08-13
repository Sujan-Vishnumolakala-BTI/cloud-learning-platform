import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface FooterLink {
  label: string;
  path: string;
  fragment?: string; // Optional property to handle mixed configurations smoothly
}

interface FooterColumn {
  title: string;
  links: FooterLink[];
}

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './footer.component.html',
})
export class FooterComponent {
  readonly year = new Date().getFullYear();

  // Explicitly typing the structure ensures safe type-checking in the template HTML
  readonly columns: FooterColumn[] = [
    {
      title: 'Learn',
      links: [
        { label: 'Browse Courses', path: '/', fragment: 'courses' },
        { label: 'AWS', path: '/', fragment: 'courses' },
        { label: 'Azure', path: '/', fragment: 'courses' },
        { label: 'Kubernetes', path: '/', fragment: 'courses' },
      ],
    },
    {
      title: 'Company',
      links: [
        { label: 'About Us', path: '/about' },
        { label: 'Contact', path: '/contact' },
        { label: 'Careers', path: '/about' },
      ],
    },
    {
      title: 'Account',
      links: [
        { label: 'Login', path: '/login' },
        { label: 'Register', path: '/register' },
        { label: 'Dashboard', path: '/dashboard' },
      ],
    },
  ];

  readonly socials = [
    { label: 'Twitter', d: 'M22 5.9c-.7.3-1.5.6-2.3.7.8-.5 1.5-1.3 1.8-2.3-.8.5-1.7.8-2.6 1a4.1 4.1 0 0 0-7 3.7A11.6 11.6 0 0 1 3.4 4.6a4 4 0 0 0 1.3 5.5c-.6 0-1.3-.2-1.8-.5v.1c0 2 1.4 3.6 3.3 4a4.2 4.2 0 0 1-1.9.1 4.1 4.1 0 0 0 3.8 2.9A8.3 8.3 0 0 1 2 18.4a11.6 11.6 0 0 0 6.3 1.8c7.5 0 11.7-6.3 11.7-11.7v-.5c.8-.6 1.5-1.3 2-2.1Z' },
    { label: 'LinkedIn', d: 'M6.9 8.6H3.4V21H6.9V8.6ZM5.1 3a2 2 0 1 0 0 4.1 2 2 0 0 0 0-4.1ZM21 13.9c0-3.6-1.9-5.3-4.5-5.3a3.9 3.9 0 0 0-3.5 1.9V8.6H9.5c0 .9 0 12.4 0 12.4H13v-6.9c0-.4 0-.7.1-1a2.2 2.2 0 0 1 2-1.5c1.4 0 2 1.1 2 2.7V21H21v-7.1Z' },
    { label: 'GitHub', d: 'M12 2a10 10 0 0 0-3.2 19.5c.5.1.7-.2.7-.5v-1.7c-2.8.6-3.4-1.3-3.4-1.3-.4-1.2-1.1-1.5-1.1-1.5-.9-.6.1-.6.1-.6 1 .1 1.5 1 1.5 1 .9 1.6 2.4 1.1 3 .9.1-.7.4-1.1.6-1.4-2.2-.2-4.6-1.1-4.6-5A3.9 3.9 0 0 1 6.6 9c-.1-.3-.5-1.3.1-2.6 0 0 .8-.3 2.8 1a9.6 9.6 0 0 1 5 0c1.9-1.3 2.8-1 2.8-1 .6 1.3.2 2.3.1 2.6a3.9 3.9 0 0 1 1 2.6c0 3.9-2.4 4.7-4.6 5 .3.3.7 1 .7 2v3c0 .3.2.6.7.5A10 10 0 0 0 12 2Z' },
  ];
}
