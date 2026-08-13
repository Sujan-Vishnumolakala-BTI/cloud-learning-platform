export interface Module {
  id: number;
  courseId: number;
  title: string;
  description: string;
  orderIndex: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateModuleRequest {
  title: string;
  description: string;
  orderIndex: number;
}

export interface UpdateModuleRequest {
  title: string;
  description: string;
  orderIndex: number;
}