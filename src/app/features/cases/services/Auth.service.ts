import { Inject, Injectable } from '@angular/core';
import { GlobalService } from '../../../core/services/global.service';
import { HttpClient } from '@angular/common/http';
import { AppSession, LoginCredentials } from '../models/logincredentials';
import { BehaviorSubject, Subject, tap } from 'rxjs';
import { Router } from '@angular/router';
import { WINDOW } from '../../../core/window.token';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
   private ws!: WebSocket;
   public loginPath$ = new Subject<LoginCredentials>();
     public sessionData$ = new BehaviorSubject<AppSession | null>(null);
private globalservice: GlobalService;
  constructor(private gs: GlobalService,private http: HttpClient,@Inject(WINDOW) private window:Window) {
   this.globalservice=gs;
  }
   connect() {
    this.ws = new WebSocket('ws://localhost:5030/ws/login'); // change port if needed
this.ws.onmessage = (event) => {
    const data = JSON.parse(event.data);
    if (data.type === 'logreq') {

      this.loginPath$.next(data);
      console.log('WebSocket message received:', data);
    }

    if (data.type === 'ok') {
      console.log('WebSocket login bool:', data.authenticated);
      this.sessionData$.next(data);
      this.setSession(data);
      this.window.open ('http://localhost:4200/cases','_blank');
     
    }
  };

    this.ws.onclose = () => {
      console.log('WebSocket disconnected');
    };
    this.ws.onerror = (err) => {
      console.error('WebSocket error', err);
    };
  }
  disconnect() {
    if (this.ws) {
      this.ws.close();
    }
  }
   public getLoginDetails(){
    
    return this.http.get<LoginCredentials>(this.globalservice.backend_api+`connect`);
   }
    validateSession() {
    return this.http.get<AppSession>(this.globalservice.backend_api+`session/validate`).pipe(
      tap(session => {
        if (session?.isAuthenticated) {
          this.setSession(session);
        } else {
          this.clearSession();
        }
      })
    );
  }

  setSession(session: AppSession) {
    localStorage.setItem('sessionToken', session.sessionToken);
    this.sessionData$.next(session);
  }

  clearSession() {
    localStorage.removeItem('sessionToken');
    this.sessionData$.next({ type: '', sessionToken: '', isAuthenticated: false });
  }

  isAuthenticated(): boolean {
    return !!this.sessionData$.value?.isAuthenticated;
  }

  getSessionObservable() {
    return this.sessionData$.asObservable();
  }
}
