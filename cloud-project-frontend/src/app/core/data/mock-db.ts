
import { User } from '../models/user.model';

export interface MockUserRecord extends User {
  password: string;
}

/**
 * Temporary frontend database.
 *
 * IMPORTANT:
 * This is only for frontend development.
 *
 * Do NOT store real passwords like this
 * in a production application.
 */
export const MOCK_USERS: MockUserRecord[] = [
  // =====================================================
  // ADMIN
  // =====================================================
  {
    id: 'admin-001',
    fullName: 'CloudPath Administrator',
    email: 'admin@cloudpath.com',
    password: 'Admin@123',
    role: 'admin',

    enrolledCourseIds: [],
    completedCourseIds: [],

    avatarUrl:
      'https://api.dicebear.com/7.x/notionists/svg?seed=CloudPathAdmin',

    createdAt: '2026-01-10T10:00:00.000Z',
  },

  // =====================================================
  // STUDENTS
  // =====================================================
  {
    id: 'student-001',
    fullName: 'Arun Kumar',
    email: 'student@cloudpath.com',
    password: 'Student@123',
    role: 'student',

    studentId: 'CLD-100001',

    enrolledCourseIds: [
      'c1',
      'c3',
      'c5',
    ],

    completedCourseIds: [
      'c1',
    ],

    avatarUrl:
      'https://api.dicebear.com/7.x/notionists/svg?seed=Arun',

    createdAt: '2026-02-15T10:00:00.000Z',
  },

  {
    id: 'student-002',
    fullName: 'Priya Sharma',
    email: 'priya@cloudpath.com',
    password: 'Priya@123',
    role: 'student',

    studentId: 'CLD-100002',

    enrolledCourseIds: [
      'c1',
      'c2',
      'c4',
    ],

    completedCourseIds: [
      'c1',
      'c2',
    ],

    avatarUrl:
      'https://api.dicebear.com/7.x/notionists/svg?seed=Priya',

    createdAt: '2026-03-20T10:00:00.000Z',
  },

  {
    id: 'student-003',
    fullName: 'Rahul Raj',
    email: 'rahul@cloudpath.com',
    password: 'Rahul@123',
    role: 'student',

    studentId: 'CLD-100003',

    enrolledCourseIds: [
      'c2',
      'c3',
    ],

    completedCourseIds: [
      'c2',
    ],

    avatarUrl:
      'https://api.dicebear.com/7.x/notionists/svg?seed=Rahul',

    createdAt: '2026-04-05T10:00:00.000Z',
  },

  // =====================================================
  // INSTRUCTOR
  // =====================================================
  {
    id: 'instructor-001',
    fullName: 'Dr. Rajesh Kumar',
    email: 'instructor@cloudpath.com',
    password: 'Instructor@123',
    role: 'instructor',

    enrolledCourseIds: [],
    completedCourseIds: [],

    avatarUrl:
      'https://api.dicebear.com/7.x/notionists/svg?seed=Rajesh',

    createdAt: '2026-01-20T10:00:00.000Z',
  },

  {
    id: 'instructor-002',
    fullName: 'Anitha Krishnan',
    email: 'anitha@cloudpath.com',
    password: 'Anitha@123',
    role: 'instructor',

    enrolledCourseIds: [],
    completedCourseIds: [],

    avatarUrl:
      'https://api.dicebear.com/7.x/notionists/svg?seed=Anitha',

    createdAt: '2026-02-01T10:00:00.000Z',
  },
];
