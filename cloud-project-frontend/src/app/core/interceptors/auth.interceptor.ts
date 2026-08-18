import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const auth = inject(AuthService);
  const token = auth.getToken();

  console.log('========== AUTH INTERCEPTOR ==========');
  console.log('REQUEST URL:', req.url);
  console.log('TOKEN PRESENT:', !!token);

  // ---------------------------------------------------------
  // MINIO PRESIGNED URL
  // Do NOT attach application JWT
  // ---------------------------------------------------------

  if (req.url.startsWith('http://localhost:9000/')) {

    console.log('MINIO REQUEST -> JWT NOT ATTACHED');

    return next(req);
  }

  // ---------------------------------------------------------
  // BACKEND API
  // Attach JWT
  // ---------------------------------------------------------

  if (token) {

    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    console.log('BACKEND REQUEST -> JWT ATTACHED');
    console.log(
      'AUTHORIZATION HEADER:',
      cloned.headers.get('Authorization')
        ? 'Bearer <present>'
        : 'MISSING'
    );

    return next(cloned);
  }

  console.warn(
    'BACKEND REQUEST -> JWT NOT AVAILABLE'
  );

  return next(req);
};