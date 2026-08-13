import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './about.component.html',
})
export class AboutComponent {
  readonly values = [
    {
      title: 'Learn by deploying',
      desc: 'Every module ends with something running in a real sandbox environment, not just a quiz.',
      d: 'M12 2l8 5v10l-8 5-8-5V7z',
    },
    {
      title: 'Mentors who ship',
      desc: 'Instructors are practicing cloud and DevOps engineers, not full-time content creators.',
      d: 'M12 12a5 5 0 1 0 0-10 5 5 0 0 0 0 10ZM4 22a8 8 0 0 1 16 0',
    },
    {
      title: 'Certification-first curriculum',
      desc: 'Every track maps directly to an industry-recognized certification exam.',
      d: 'M12 3a4 4 0 1 1 0 8 4 4 0 0 1 0-8ZM6.5 14l-1.8 6.5L9 19l2 2 1.9-6M17.5 14l1.8 6.5L15 19l-2 2-1.9-6',
    },
    {
      title: 'Community-supported',
      desc: 'Get unstuck fast with a peer community and weekly office hours.',
      d: 'M17 21v-2a4 4 0 0 0-4-4H7a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8ZM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75',
    },
  ];

  readonly team = [
    { name: 'Priya Raman', role: 'Founder & Lead Instructor, AWS', img: 'https://i.pravatar.cc/200?img=47' },
    { name: 'Daniel Cho', role: 'Head of Curriculum, Azure', img: 'https://i.pravatar.cc/200?img=13' },
    { name: 'Sara Lindqvist', role: 'DevOps Instructor', img: 'https://i.pravatar.cc/200?img=32' },
    { name: 'Marcus Webb', role: 'Kubernetes Instructor', img: 'https://i.pravatar.cc/200?img=15' },
  ];
}
