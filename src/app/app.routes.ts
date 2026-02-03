import { Routes } from '@angular/router';
import { CaseTableComponent } from './features/cases/components/case-table/case-table.component';
import { LoginComponent } from './features/cases/pages/login/login.component';
import { HeaderComponent } from './layout/header/header.component';

export const routes: Routes = [
   {component:CaseTableComponent,path:"cases"},
   {component: LoginComponent ,path:"login"}
];
