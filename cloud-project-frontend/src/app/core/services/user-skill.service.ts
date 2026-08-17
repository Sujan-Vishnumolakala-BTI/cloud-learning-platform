import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SkillRequest {
    skill: string;
    proficiency: number;
}

export interface UserSkillResponse {
    userId: number;
    skills: {
        skill: string;
        proficiency: number;
    }[];
}

export interface UserSkillsResponse {
    skill: string;
    proficiency: number;
}

@Injectable({
    providedIn: 'root'
})
export class UserSkillService {

    private readonly http = inject(HttpClient);

    private readonly API_URL =
        'http://localhost:8080/api/users';

    getMySkills(): Observable<UserSkillResponse> {

        return this.http.get<UserSkillResponse>(
            `${this.API_URL}/me/skills`
        );
    }

    saveMySkills(
        skills: {
            skill: string;
            proficiency: number;
        }[]
    ) {
        return this.http.put<UserSkillResponse>(
            `${this.API_URL}/api/users/me/skills`,
            {
                skills
            }
        );
    }
}