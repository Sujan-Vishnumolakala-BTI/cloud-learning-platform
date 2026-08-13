import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router,
} from '@angular/router';

import { AuthService } from '../services/auth.service';
import { UserRole } from '../models/user.model';

/**
 * Allows access only to logged-in users.
 */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    return true;
  }

  return router.createUrlTree(['/login']);
};


/**
 * Allows access only to a specific role.
 */
export const roleGuard = (
  requiredRole: UserRole
): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    const user = auth.currentUser();

    if (!user) {
      return router.createUrlTree(['/login']);
    }

    if (user.role === requiredRole) {
      return true;
    }

    switch (user.role) {
      case 'admin':
        return router.createUrlTree(['/admin']);

      case 'instructor':
        return router.createUrlTree(['/instructor']);

      case 'student':
      default:
        return router.createUrlTree(['/dashboard']);
    }
  };
};


/**
 * Prevent logged-in users from opening login/register.
 */
export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const user = auth.currentUser();

  if (!user) {
    return true;
  }

  switch (user.role) {
    case 'admin':
      return router.createUrlTree(['/admin']);

    case 'instructor':
      return router.createUrlTree(['/instructor']);

    case 'student':
    default:
      return router.createUrlTree(['/dashboard']);
  }
};