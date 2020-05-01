import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IInventory } from 'app/shared/model/inventory/inventory.model';
import { AccountService } from 'app/core/auth/account.service';
import { InventoryService } from './inventory.service';

@Component({
  selector: 'jhi-inventory',
  templateUrl: './inventory.component.html'
})
export class InventoryComponent implements OnInit, OnDestroy {
  inventories: IInventory[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected inventoryService: InventoryService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.inventoryService
      .query()
      .pipe(
        filter((res: HttpResponse<IInventory[]>) => res.ok),
        map((res: HttpResponse<IInventory[]>) => res.body)
      )
      .subscribe(
        (res: IInventory[]) => {
          this.inventories = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInInventories();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IInventory) {
    return item.id;
  }

  registerChangeInInventories() {
    this.eventSubscriber = this.eventManager.subscribe('inventoryListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
