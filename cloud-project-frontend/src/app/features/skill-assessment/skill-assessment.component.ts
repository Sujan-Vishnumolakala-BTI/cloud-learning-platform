import {
  Component,
  OnInit,
  inject,
  signal
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  Router
} from '@angular/router';

import {
  AuthService
} from '../../core/services/auth.service';

@Component({
  selector: 'app-skill-assessment',

  standalone: true,

  imports: [
    CommonModule
  ],

  templateUrl:
    './skill-assessment.component.html'
})
export class SkillAssessmentComponent
  implements OnInit {

  private readonly auth =
    inject(AuthService);

  private readonly router =
    inject(Router);


  // =====================================================
  // AVAILABLE SKILLS
  // =====================================================

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


  // =====================================================
  // SELECTED SKILLS
  // =====================================================

  readonly selectedSkills =
    signal<string[]>([]);


  // =====================================================
  // PROFICIENCY
  // =====================================================

  /*
   * Stores proficiency for each selected skill.
   *
   * Example:
   *
   * {
   *   Python: 8,
   *   SQL: 6,
   *   Docker: 3
   * }
   */

  readonly proficiency =
    signal<Record<string, number>>({});


  // =====================================================
  // UI STATE
  // =====================================================

  readonly loading =
    signal(false);

  readonly loadingExisting =
    signal(true);

  readonly error =
    signal<string | null>(null);

  readonly success =
    signal<string | null>(null);


  // =====================================================
  // INIT
  // =====================================================

  ngOnInit(): void {

    this.loadExistingSkills();

  }


  // =====================================================
  // LOAD EXISTING SKILLS
  // =====================================================

  private loadExistingSkills(): void {

    this.loadingExisting.set(true);

    this.error.set(null);

    this.auth
      .getMySkills()
      .subscribe({

        next: response => {

          console.log(
            'EXISTING USER SKILLS:',
            response
          );

          /*
           * Backend response:
           *
           * {
           *   userId: 21,
           *   skills: [
           *     {
           *       skill: "Python",
           *       proficiency: 8
           *     }
           *   ]
           * }
           */

          const existingSkills =
            response.skills ?? [];


          /*
           * Extract skill names.
           */

          this.selectedSkills.set(
            existingSkills.map(
              item => item.skill
            )
          );


          /*
           * Extract proficiency values.
           */

          const proficiencyMap:
            Record<string, number> = {};


          for (
            const item of existingSkills
          ) {

            proficiencyMap[item.skill] =
              item.proficiency;

          }


          this.proficiency.set(
            proficiencyMap
          );


          this.loadingExisting.set(false);

        },


        error: error => {

          console.error(
            'LOAD USER SKILLS ERROR:',
            error
          );

          this.loadingExisting.set(false);

          this.error.set(
            error?.error?.message ??
            'Unable to load your skills.'
          );

        }

      });

  }


  // =====================================================
  // CHECK WHETHER SKILL IS SELECTED
  // =====================================================

  isSelected(
    skill: string
  ): boolean {

    return this
      .selectedSkills()
      .includes(skill);

  }


  // =====================================================
  // TOGGLE SKILL
  // =====================================================

  toggleSkill(
    skill: string
  ): void {

    const current =
      this.selectedSkills();


    /*
     * REMOVE SKILL
     */

    if (
      current.includes(skill)
    ) {

      this.selectedSkills.set(
        current.filter(
          item => item !== skill
        )
      );


      /*
       * Also remove its proficiency.
       */

      this.proficiency.update(
        values => {

          const updated = {
            ...values
          };

          delete updated[skill];

          return updated;

        }
      );

      return;

    }


    /*
     * ADD SKILL
     */

    this.selectedSkills.set([
      ...current,
      skill
    ]);


    /*
     * Default proficiency = 1.
     */

    this.proficiency.update(
      values => ({
        ...values,
        [skill]: values[skill] ?? 1
      })
    );

  }


  // =====================================================
  // GET PROFICIENCY
  // =====================================================

  getProficiency(
    skill: string
  ): number {

    return this.proficiency()[skill] ?? 1;

  }


  // =====================================================
  // SET PROFICIENCY
  // =====================================================

  setProficiency(
    skill: string,
    value: number
  ): void {

    this.proficiency.update(
      values => ({
        ...values,
        [skill]: value
      })
    );

  }


  // =====================================================
  // SAVE SKILLS
  // =====================================================

  saveSkills(): void {

    this.error.set(null);

    this.success.set(null);


    const selected =
      this.selectedSkills();


    /*
     * At least one skill required.
     */

    if (
      selected.length === 0
    ) {

      this.error.set(
        'Please select at least one skill.'
      );

      return;

    }


    /*
     * Convert frontend data into
     * backend request format.
     */

    const skills =
      selected.map(skill => ({

        skill: skill,

        proficiency:
          this.getProficiency(skill)

      }));


    console.log(
      'SAVING USER SKILLS:',
      skills
    );


    this.loading.set(true);


    this.auth
      .saveMySkills(skills)
      .subscribe({

        next: response => {

          console.log(
            'USER SKILLS SAVED:',
            response
          );


          this.loading.set(false);


          this.success.set(
            'Your skills have been saved successfully.'
          );


          /*
           * Go to dashboard after
           * successful save.
           */

          setTimeout(() => {

            this.router.navigate([
              '/dashboard'
            ]);

          }, 700);

        },


        error: error => {

          console.error(
            'SAVE USER SKILLS ERROR:',
            error
          );


          this.loading.set(false);


          this.error.set(
            error?.error?.message ??
            'Unable to save your skills.'
          );

        }

      });

  }


  // =====================================================
  // SKIP
  // =====================================================

  skip(): void {

    this.router.navigate([
      '/dashboard'
    ]);

  }

}