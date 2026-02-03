// window.token.ts
import { InjectionToken } from '@angular/core';

// Create a token for the window object
export const WINDOW = new InjectionToken<Window | null>(
  'WindowToken',
  {
    providedIn: 'root',
    factory: () => (typeof window !== 'undefined' ? window : null) // SSR-safe
  }
);
