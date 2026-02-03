import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {
  backend_api = `http://localhost:5030/`;
  constructor() {
   }
  
}
