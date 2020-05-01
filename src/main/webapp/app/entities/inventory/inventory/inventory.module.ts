import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GatewaySharedModule } from 'app/shared/shared.module';
import { InventoryComponent } from './inventory.component';
import { InventoryDetailComponent } from './inventory-detail.component';
import { InventoryUpdateComponent } from './inventory-update.component';
import { InventoryDeletePopupComponent, InventoryDeleteDialogComponent } from './inventory-delete-dialog.component';
import { inventoryRoute, inventoryPopupRoute } from './inventory.route';

const ENTITY_STATES = [...inventoryRoute, ...inventoryPopupRoute];

@NgModule({
  imports: [GatewaySharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    InventoryComponent,
    InventoryDetailComponent,
    InventoryUpdateComponent,
    InventoryDeleteDialogComponent,
    InventoryDeletePopupComponent
  ],
  entryComponents: [InventoryDeleteDialogComponent]
})
export class InventoryInventoryModule {}
