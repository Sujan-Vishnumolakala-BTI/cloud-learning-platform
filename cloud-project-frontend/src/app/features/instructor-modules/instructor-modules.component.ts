import {
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';

import { CommonModule } from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  ActivatedRoute,
  Router,
  RouterLink
} from '@angular/router';

import { CourseService } from '../../core/services/course.service';

import {
  Module,
  CreateModuleRequest,
  UpdateModuleRequest
} from '../../core/models/module.model';

@Component({
  selector: 'app-instructor-modules',
  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
  ],

  templateUrl:
    './instructor-modules.component.html'
})
export class InstructorModulesComponent
  implements OnInit {

  private readonly route =
    inject(ActivatedRoute);

  private readonly router =
    inject(Router);

  private readonly courseService =
    inject(CourseService);

  readonly courseId =
    signal<number>(0);

  readonly modules =
    signal<Module[]>([]);

  readonly loading =
    signal(true);

  readonly saving =
    signal(false);

  readonly error =
    signal<string | null>(null);

  readonly success =
    signal<string | null>(null);

  readonly showForm =
    signal(false);

  readonly editingModuleId =
    signal<number | null>(null);

  title = '';

  description = '';

  orderIndex = 1;

  ngOnInit(): void {

    const id = Number(
      this.route.snapshot.paramMap
        .get('courseId')
    );

    if (!id) {

      this.error.set(
        'Invalid course ID.'
      );

      this.loading.set(false);

      return;
    }

    this.courseId.set(id);

    this.loadModules();
  }

  loadModules(): void {

    this.loading.set(true);

    this.error.set(null);

    this.courseService
      .getModulesByCourse(
        this.courseId()
      )
      .subscribe({

        next: modules => {

          this.modules.set(
            modules
          );

          this.loading.set(false);
        },

        error: error => {

          console.error(
            'MODULE LOAD ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to load modules.'
          );

          this.loading.set(false);
        }

      });
  }

  openCreateForm(): void {

    this.resetForm();

    this.showForm.set(true);
  }

  editModule(module: Module): void {

    this.editingModuleId.set(
      module.id
    );

    this.title =
      module.title;

    this.description =
      module.description ?? '';

    this.orderIndex =
      module.orderIndex;

    this.showForm.set(true);
  }

  cancelForm(): void {

    this.showForm.set(false);

    this.resetForm();
  }

  saveModule(): void {

    this.error.set(null);

    this.success.set(null);

    if (!this.title.trim()) {

      this.error.set(
        'Module title is required.'
      );

      return;
    }

    if (!this.orderIndex ||
        this.orderIndex < 1) {

      this.error.set(
        'Order must be at least 1.'
      );

      return;
    }

    this.saving.set(true);

    const request:
      CreateModuleRequest = {

      title:
        this.title.trim(),

      description:
        this.description.trim(),

      orderIndex:
        this.orderIndex

    };

    const editingId =
      this.editingModuleId();

    if (editingId !== null) {

      const updateRequest:
        UpdateModuleRequest = request;

      this.courseService
        .updateModule(
          editingId,
          updateRequest
        )
        .subscribe({

          next: () => {

            this.saving.set(false);

            this.success.set(
              'Module updated successfully.'
            );

            this.showForm.set(false);

            this.resetForm();

            this.loadModules();
          },

          error: error => {

            console.error(
              'UPDATE MODULE ERROR:',
              error
            );

            this.saving.set(false);

            this.error.set(
              error?.error?.message ??
              'Unable to update module.'
            );
          }

        });

      return;
    }

    this.courseService
      .createModule(
        this.courseId(),
        request
      )
      .subscribe({

        next: () => {

          this.saving.set(false);

          this.success.set(
            'Module created successfully.'
          );

          this.showForm.set(false);

          this.resetForm();

          this.loadModules();
        },

        error: error => {

          console.error(
            'CREATE MODULE ERROR:',
            error
          );

          this.saving.set(false);

          this.error.set(
            error?.error?.message ??
            'Unable to create module.'
          );
        }

      });
  }

  deleteModule(module: Module): void {

    const confirmed =
      confirm(
        `Delete "${module.title}"?`
      );

    if (!confirmed) {
      return;
    }

    this.courseService
      .deleteModule(module.id)
      .subscribe({

        next: () => {

          this.success.set(
            'Module deleted successfully.'
          );

          this.loadModules();
        },

        error: error => {

          console.error(
            'DELETE MODULE ERROR:',
            error
          );

          this.error.set(
            error?.error?.message ??
            'Unable to delete module.'
          );
        }

      });
  }

  private resetForm(): void {

    this.editingModuleId.set(null);

    this.title = '';

    this.description = '';

    this.orderIndex =
      this.modules().length + 1;
  }

  back(): void {

    this.router.navigate([
      '/instructor/courses',
      this.courseId()
    ]);
  }
}