import { Routes } from '@angular/router';
import {SampleEntitiesComponent} from './component/sample-entities/sample-entities.component';
import { HomeComponent } from './component/home/home.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'sample-entities', component: SampleEntitiesComponent },
  { path: '**', redirectTo: '' }
];

