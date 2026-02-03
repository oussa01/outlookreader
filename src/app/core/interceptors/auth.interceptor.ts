import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const sessionToken = localStorage.getItem('sessionToken');

  console.log('Interceptor Token:', sessionToken);
  if (sessionToken) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${sessionToken}`
      }
    });
    return next(authReq);
  }
  return next(req);
};
