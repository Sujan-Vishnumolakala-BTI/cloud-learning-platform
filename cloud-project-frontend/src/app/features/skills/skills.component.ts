import {
  Component,
  inject,
  signal
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  Router
} from '@angular/router';

import {
  UserSkillService,
  SkillRequest
} from '../../core/services/user-skill.service';


@Component({
  selector: 'app-skills',
  standalone: true,

  imports: [
    CommonModule,
    ReactiveFormsModule
  ],

  templateUrl: './skills.component.html'
})
export class SkillsComponent {

  private readonly fb =
    inject(FormBuilder);

  private readonly skillService =
    inject(UserSkillService);

  private readonly router =
    inject(Router);


  readonly loading =
    signal(false);

  readonly error =
    signal<string | null>(null);


  readonly availableSkills = [

    'Java',
    'Python',
    'JavaScript',
    'TypeScript',

    'HTML',
    'CSS',

    'Angular',
    'React',

    'Spring Boot',

    'SQL',
    'MySQL',
    'PostgreSQL',

    'REST API',
    'API Testing',

    'Git',

    'Docker',
    'Kubernetes',

    'Linux',

    'AWS',
    'Azure',

    'DevOps',

    'Machine Learning',
    'Data Science'

  ];


  readonly selectedSkills =
    signal<string[]>([]);


  readonly proficiency: Record<string, number> = {};


  constructor() {

    for (
      const skill of this.availableSkills
    ) {

      this.proficiency[skill] = 1;

    }

  }


  toggleSkill(skill: string): void {

    this.selectedSkills.update(
      skills => {

        if (skills.includes(skill)) {

          return skills.filter(
            existing => existing !== skill
          );

        }

        return [
          ...skills,
          skill
        ];

      }
    );

  }


  setProficiency(
    skill: string,
    value: number
  ): void {

    this.proficiency[skill] = value;

  }


  saveSkills(): void {

    this.error.set(null);

    const selected =
      this.selectedSkills();


    if (selected.length === 0) {

      this.error.set(
        'Please select at least one skill.'
      );

      return;
    }


    const skills: SkillRequest[] =
      selected.map(skill => ({

        skill,

        proficiency:
          this.proficiency[skill] ?? 1

      }));


    console.log(
      'USER SKILLS REQUEST:',
      skills
    );


    this.loading.set(true);


    this.skillService
      .saveMySkills(skills)
      .subscribe({

        next: response => {

          console.log(
            'SKILLS SAVED:',
            response
          );

          this.loading.set(false);

          /*
           * Skills are now stored in
           * user-service.
           *
           * Next step will be recommendations.
           */

          this.router.navigate([
            '/dashboard'
          ]);

        },


        error: err => {

          console.error(
            'SAVE SKILLS ERROR:',
            err
          );

          this.loading.set(false);

          this.error.set(
            err?.error?.message ??
            'Unable to save your skills.'
          );

        }

      });

  }

}