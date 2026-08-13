import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CourseService } from '../../core/services/course.service';
import { Course, Testimonial } from '../../core/models/course.model';
import { CourseCardComponent } from '../../shared/components/course-card/course-card.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterLink, CourseCardComponent, DecimalPipe],
  templateUrl: './landing.component.html',
})
export class LandingComponent implements OnInit {
  readonly courses = signal<Course[]>([]);
  readonly loadingCourses = signal(true);
  readonly testimonials = signal<Testimonial[]>([]);
  readonly loadingTestimonials = signal(true);
  readonly skeletonArray = Array.from({ length: 6 });

  readonly platforms = [
    { name: 'AWS', accent: 'from-orange-400 to-orange-500', d: 'M4 15.5c3.5 2.4 12.5 2.4 16 0M8 8.2a4 4 0 1 1 5.4 3.7A3.6 3.6 0 0 1 16.5 15a3.6 3.6 0 0 1-3.5 2.6' },
    { name: 'Azure', accent: 'from-sky-400 to-sky-500', d: 'M8 4l6 14H4l4-9M14 4l6 14H10' },
    { name: 'Google Cloud', accent: 'from-blue-400 to-blue-500', d: 'M7 16a4.5 4.5 0 0 1 1-8.9A5.5 5.5 0 0 1 18.5 8.4 4 4 0 0 1 18 16H7Z' },
    { name: 'Docker', accent: 'from-cyan-400 to-cyan-500', d: 'M3 12h18M6 8h3v3H6zM10 8h3v3h-3zM14 8h3v3h-3zM6 12v3a3 3 0 0 0 3 3h6a3 3 0 0 0 3-3v-3' },
    { name: 'Kubernetes', accent: 'from-indigo-400 to-indigo-500', d: 'M12 2l8 5v10l-8 5-8-5V7z M12 8v8M8 10l8 4M16 10l-8 4' },
    { name: 'DevOps', accent: 'from-teal-400 to-teal-500', d: 'M4 4v16h16M8 15l3-4 3 3 4-6' },
  ];

  readonly stats = [
    { value: 10000, suffix: '+', label: 'Students Learning' },
    { value: 500, suffix: '+', label: 'Courses & Labs' },
    { value: 95, suffix: '%', label: 'Completion Rate' },
  ];

  readonly deployLines = [
    { text: '$ cloudpath deploy --track kubernetes', tone: 'cmd' },
    { text: '✓ Provisioning learning environment', tone: 'ok' },
    { text: '✓ 12 hands-on labs ready', tone: 'ok' },
    { text: '✓ Mentor review enabled', tone: 'ok' },
    { text: '➜ status: certification-ready', tone: 'accent' },
  ];

  constructor(private courseService: CourseService) { }

  // ngOnInit(): void {
  //   this.courseService.getCourses().subscribe((courses) => {
  //     this.courses.set(courses);
  //     this.loadingCourses.set(false);
  //   });
  //   this.courseService.getTestimonials().subscribe((testimonials) => {
  //     this.testimonials.set(testimonials);
  //     this.loadingTestimonials.set(false);
  //   });
  // }
  ngOnInit(): void {
    console.log('LANDING INIT');

    this.courseService.getCourses().subscribe({
      next: (courses) => {
        console.log('COURSES FROM BACKEND:', courses);

        this.courses.set(courses);
        this.loadingCourses.set(false);
      },

      error: (error) => {
        console.error('COURSE API ERROR:', error);

        this.loadingCourses.set(false);
      },
    });
  }

  starsArray(rating: number): number[] {
    return Array.from({ length: 5 }, (_, i) => (i < Math.round(rating) ? 1 : 0));
  }
}
