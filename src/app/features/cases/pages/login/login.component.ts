import { Component, Inject, OnInit } from '@angular/core';
import { AuthService } from '../../services/Auth.service';
import { DatePipe } from '@angular/common';
import { WINDOW } from '../../../../core/window.token';
import { AppSession } from '../../models/logincredentials';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
private loginService: AuthService;
loginModal = false;
generatedCode = '';
auth =false;

expirationDate :number=0;
loginUrlPath = '';
constructor(loginService : AuthService,@Inject(WINDOW) private window:Window,private router:Router){
  this.loginService=loginService;
}
ngOnInit(): void {
  this.auth = this.loginService.isAuthenticated();
console.log('Auth status:', this.auth);
const token = localStorage.getItem('sessionToken')
   if (token) {
    this.loginService.validateSession().subscribe({
      next:(session)=>{
        if (session && session.isAuthenticated) {
          this.router.navigate(['/cases']);
      }else {
            this.router.navigate(['/login']);
          }}
    });
  }
   this.loginService.loginPath$.subscribe((data)=>{
       
    this.generatedCode=data.generatedCode;
    this.loginUrlPath=data.loginUrlPath;
    data.expiration = new Date(data.expiration).getTime();
    this.expirationDate=data.expiration;
  });
  if (!this.loginService.isAuthenticated()){
      this.loginService.connect();
    this.loginService.getLoginDetails().subscribe();
    this.loginModal=true;
  }
     
}
public ButtonClick(){

this.window.open(this.loginUrlPath,'_blank');
  // Subscribe to the real-time emitted LoginCredentials
}


}
