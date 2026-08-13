// import { HttpInterceptorFn } from '@angular/common/http';
// import { inject } from '@angular/core';
// import { AuthService } from '../services/auth.service';

// /**
//  * Attaches the mock bearer token to outgoing requests.
//  * ---------------------------------------------------------------------
//  * BACKEND INTEGRATION POINT: once real endpoints exist, this interceptor
//  * needs no changes — it already reads whatever token AuthService stores.
//  * ---------------------------------------------------------------------
//  */
// export const authInterceptor: HttpInterceptorFn = (req, next) => {
//   const auth = inject(AuthService);
//   const token = auth.getToken();

//   if (!token) return next(req);

//   const cloned = req.clone({
//     setHeaders: { Authorization: `Bearer ${token}` },
//   });
//   return next(cloned);
// };


import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const auth = inject(AuthService);

  const token = auth.getToken();

  console.log('========== AUTH INTERCEPTOR ==========');
  console.log('REQUEST URL:', req.url);
  console.log('TOKEN PRESENT:', !!token);

  if (token) {
    console.log(
      'TOKEN PREVIEW:',
      token.substring(0, 20) + '...'
    );

    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    console.log(
      'AUTHORIZATION HEADER ADDED:',
      cloned.headers.has('Authorization')
    );

    return next(cloned);
  }

  console.warn(
    'NO TOKEN - REQUEST SENT WITHOUT AUTHORIZATION:',
    req.url
  );

  return next(req);
};