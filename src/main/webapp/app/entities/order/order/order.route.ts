import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Order } from 'app/shared/model/order/order.model';
import { OrderService } from './order.service';
import { OrderComponent } from './order.component';
import { OrderDetailComponent } from './order-detail.component';
import { OrderUpdateComponent } from './order-update.component';
import { OrderDeletePopupComponent } from './order-delete-dialog.component';
import { IOrder } from 'app/shared/model/order/order.model';

@Injectable({ providedIn: 'root' })
export class OrderResolve implements Resolve<IOrder> {
  constructor(private service: OrderService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IOrder> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Order>) => response.ok),
        map((order: HttpResponse<Order>) => order.body)
      );
    }
    return of(new Order());
  }
}

export const orderRoute: Routes = [
  {
    path: '',
    component: OrderComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Orders'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: OrderDetailComponent,
    resolve: {
      order: OrderResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Orders'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: OrderUpdateComponent,
    resolve: {
      order: OrderResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Orders'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: OrderUpdateComponent,
    resolve: {
      order: OrderResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Orders'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const orderPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: OrderDeletePopupComponent,
    resolve: {
      order: OrderResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Orders'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
