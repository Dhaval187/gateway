import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IInventory } from 'app/shared/model/inventory/inventory.model';

@Component({
  selector: 'jhi-inventory-detail',
  templateUrl: './inventory-detail.component.html'
})
export class InventoryDetailComponent implements OnInit {
  inventory: IInventory;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ inventory }) => {
      this.inventory = inventory;
    });
  }

  previousState() {
    window.history.back();
  }
}
