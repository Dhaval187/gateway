import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'order',
        loadChildren: () => import('./order/order/order.module').then(m => m.OrderOrderModule)
      },
      {
        path: 'inventory',
        loadChildren: () => import('./inventory/inventory/inventory.module').then(m => m.InventoryInventoryModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class GatewayEntityModule {}
