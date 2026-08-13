// import { UserRole } from './user.model';

// export interface AdminUser {
//   id: string;
//   name: string;
//   email: string;
//   role: UserRole;
//   enrolledCourses: number;
//   completedCourses: number;
//   progress: number;
//   lastActive: string;
//   status: 'Active' | 'Inactive';
//   joinedDate: string;
// }

// export interface AdminCourse {
//   id: string;
//   title: string;
//   instructor: string;
//   enrolledStudents: number;
//   completionRate: number;
//   status: 'Published' | 'Draft';
// }

// export interface AdminStats {
//   totalUsers: number;
//   totalStudents: number;
//   totalInstructors: number;
//   totalCourses: number;
//   totalEnrollments: number;
//   activeStudents: number;
//   averageCompletion: number;
// }

import { UserRole } from './user.model';

export interface AdminUser {
  id: string;
  name: string;
  email: string;
  role: UserRole;

  enrolledCourses: number;
  completedCourses: number;
  progress: number;

  lastActive: string;

  status: 'Active' | 'Inactive';

  joinedDate: string;
}

export interface AdminCourse {
  id: string;
  title: string;
  instructor: string;

  enrolledStudents: number;
  completionRate: number;

  status: 'Published' | 'Draft';
}

export interface AdminStats {
  totalUsers: number;
  totalStudents: number;
  totalInstructors: number;

  totalCourses: number;

  totalEnrollments: number;

  activeStudents: number;

  averageCompletion: number;
}