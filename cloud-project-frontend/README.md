<!-- # CloudPath — Cloud Course Completion Platform (Frontend)

A complete, frontend-only Angular 20 application for a Udemy/Coursera-style cloud & DevOps
learning platform. Built with standalone components, Tailwind CSS, Reactive Forms, and Angular
Signals. All data currently comes from mock services so the UI can be reviewed and used
end-to-end before the backend team's REST API is ready.

---

## 1. Create the project / get it running

The code in this folder is a full Angular CLI project (not scaffolded live in this environment,
since it has no network access — but every file `ng new` would generate is here, plus all the
feature code). To run it locally:

```bash
# 1. Install dependencies
npm install

# 2. Start the dev server
ng serve

# 3. Open the app
# http://127.0.0.1:4200
```

If you'd rather scaffold a fresh Angular 20 project yourself and copy this `src/` folder in:

```bash
npm install -g @angular/cli@20
ng new cloud-course-platform --standalone --style=css --routing=false --ssr=false
cd cloud-course-platform
# then copy this project's src/, tailwind.config.js and postcss.config.js over yours
```

---

## 2. Tailwind CSS installation steps

Already wired up in this project (`tailwind.config.js`, `postcss.config.js`, `src/styles.css`
with `@tailwind` directives). If you ever need to redo it from scratch on a fresh Angular
project:

```bash
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init
```

Then:
1. Point `content` in `tailwind.config.js` at `./src/**/*.{html,ts}` (already done here).
2. Add the three `@tailwind` directives to `src/styles.css` (already done here).
3. Make sure `src/styles.css` is listed under `styles` in `angular.json` (already done here).

Custom design tokens (colors, fonts, animations, spacing) are defined in `tailwind.config.js`,
and reusable component classes (`.btn-primary`, `.card`, `.field-input`, etc.) live under
`@layer components` in `src/styles.css`.

---

## 3. Folder-by-folder explanation

```text
src/app/
 ├── core/                     # App-wide, singleton concerns
 │    ├── models/               # TypeScript interfaces (User, Course, etc.) — the API "contract"
 │    ├── services/             # AuthService & CourseService — mock now, swap internals later
 │    ├── guards/                # authGuard (protects /dashboard) & guestGuard (blocks /login when logged in)
 │    └── interceptors/          # authInterceptor — attaches the bearer token to future API calls
 ├── shared/
 │    └── components/           # Reusable, presentation-only building blocks
 │         ├── navbar/            # Sticky responsive navbar + profile dropdown
 │         ├── footer/            # Site footer
 │         ├── course-card/       # Used on landing page AND dashboard (progress mode)
 │         ├── otp-input/         # 6-box OTP input with auto-focus & paste support
 │         ├── recaptcha-placeholder/  # UI-only "I'm not a robot" widget
 │         └── not-found/         # 404 page
 ├── features/                  # Route-level pages (each lazy-loaded)
 │    ├── landing/                # Hero, feature cards, stats, courses, testimonials, CTA
 │    ├── auth/
 │    │    ├── login/              # Split-screen login + Google button + reCAPTCHA placeholder
 │    │    ├── register/           # Registration + live password-strength meter
 │    │    └── verify-otp/         # OTP verification flow (simulated)
 │    ├── dashboard/              # Student dashboard: stats, My Learning, Recommended, Activity
 │    ├── about/                  # Mission, values, team, cloud-learning benefits
 │    └── contact/                # Contact form + info cards + map placeholder
 ├── app.routes.ts               # All route definitions, each feature lazy-loaded
 ├── app.config.ts               # Providers: router, HttpClient + interceptor, animations
 └── app.component.ts/html       # Shell: navbar + <router-outlet> + footer
```

Every feature component is **standalone** (no NgModules) and lazy-loaded via
`loadComponent()` in `app.routes.ts`, so the initial bundle stays small.

---

## 4. Mock authentication & data

`core/services/auth.service.ts` simulates a real auth API using RxJS `of(...).pipe(delay(...))`
and persists the "logged in" user to `localStorage`. It exposes:

- `login(payload)`- `register(payload)` → stores a pending registration in `sessionStorage`, "sends" an OTP
- `verifyOtp(code)` → any 6-digit code succeeds **except `000000`**, which is wired up to
  demonstrate the error state
- `resendOtp()`
- `logout()`
- `currentUser` (a signal) / `isLoggedIn` (a computed signal)

`core/services/course.service.ts` returns mock course, testimonial, and activity data the same
way, with artificial `delay()` so the skeleton loaders in the UI have something to show.

---

## 5. Connecting real backend APIs later

The whole point of the mock services is that **no component code should need to change** when
the real API is ready — only the internals of `AuthService` and `CourseService`.

### Login / Register / OTP
In `core/services/auth.service.ts`, replace each method body with an `HttpClient` call that
returns the same shape:

```ts
login(payload: LoginPayload): Observable<AuthResponse> {
  return this.http.post<AuthResponse>('/api/auth/login', payload).pipe(
    tap((res) => this.persistSession(res))
  );
}

register(payload: RegisterPayload): Observable<{ email: string }> {
  return this.http.post<{ email: string }>('/api/auth/register', payload);
}

verifyOtp(otp: string): Observable<AuthResponse> {
  return this.http.post<AuthResponse>('/api/auth/verify-otp', { otp }).pipe(
    tap((res) => this.persistSession(res))
  );
}
```

Inject `HttpClient` in the constructor (it's already provided app-wide in `app.config.ts`, and
`authInterceptor` already attaches whatever token `getToken()` returns — no changes needed there).

### Dashboard / course data
In `core/services/course.service.ts`, swap each `of(mockData).pipe(delay(...))` for the matching
endpoint, e.g.:

```ts
getCourses(): Observable<Course[]> {
  return this.http.get<Course[]>('/api/courses');
}

getEnrolledCourses(enrolledIds: string[], completedIds: string[]): Observable<EnrolledCourse[]> {
  return this.http.get<EnrolledCourse[]>('/api/me/courses');
}
```

Because every component consumes these services through their public method signatures (not
the mock data directly), the UI, forms, guards, and routing all keep working unchanged.

### Environment-based API URL
Add an `environment.ts` / `environment.prod.ts` pair with an `apiUrl`, then prefix all HTTP calls
with `environment.apiUrl` so dev/staging/prod can point at different backends.

### Google login & reCAPTCHA
Both are UI-only placeholders today (`loginWithGoogle()` in `login.component.ts` and
`RecaptchaPlaceholderComponent`). Once the backend exposes a Google OAuth redirect URL and a
real reCAPTCHA site key, replace the click handler and swap the placeholder component for the
official `grecaptcha` widget — the rest of the login form (validation, error handling, loading
state) needs no changes.

---

## 6. Design notes

- Color system: primary `#2563EB`, secondary `#0EA5E9`, accent `#14B8A6`, background `#F8FAFC`
  (see `tailwind.config.js`).
- Typography: Space Grotesk (display/headings), Inter (body), JetBrains Mono (code/terminal
  accents in the hero and stats).
- Signature element: the hero's animated "deploy log" terminal card, echoing the DevOps subject
  matter instead of a generic illustration.
- Fully responsive (mobile hamburger nav, stacked grids, split-screen login only on `lg+`).
- Skeleton loaders on course grids, testimonials, and dashboard sections while mock data
  "loads."

docker run -p 4200:4200 -v ${PWD}:/app -v /app/node_modules cloudpath-frontend
docker run -p 4200:4200 -v "${PWD}:/app" -v /app/node_modules cloudpath-frontend -->


docker run -p 4200:4200 -v "${PWD}:/app" -v /app/node_modules cloudpath-frontend