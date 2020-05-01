import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IInventory } from 'app/shared/model/inventory/inventory.model';
import { InventoryService } from './inventory.service';

@Component({
  selector: 'jhi-inventory-delete-dialog',
  templateUrl: './inventory-delete-dialog.component.html'
})
export class InventoryDeleteDialogComponent {
  inventory: IInventory;

  constructor(protected inventoryService: InventoryService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.inventoryService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'inventoryListModification',
        content: 'Deleted an inventory'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-inventory-delete-popup',
  template: ''
})
export class InventoryDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ inventory }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(InventoryDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.inventory = inventory;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/inventory', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/inventory', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
