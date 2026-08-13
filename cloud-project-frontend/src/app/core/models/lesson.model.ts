export interface Lesson {

  id: number;

  moduleId: number;

  title: string;

  description: string | null;

  contentType: string;

  contentUrl: string | null;

  durationMinutes: number | null;

  orderIndex: number;

  createdAt: string;

  updatedAt: string;
}