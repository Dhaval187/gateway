import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IInventory, Inventory } from 'app/shared/model/inventory/inventory.model';
import { InventoryService } from './inventory.service';

@Component({
  selector: 'jhi-inventory-update',
  templateUrl: './inventory-update.component.html'
})
export class InventoryUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    name: [],
    price: [],
    quantity: []
  });

  constructor(protected inventoryService: InventoryService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ inventory }) => {
      this.updateForm(inventory);
    });
  }

  updateForm(inventory: IInventory) {
    this.editForm.patchValue({
      id: inventory.id,
      name: inventory.name,
      price: inventory.price,
      quantity: inventory.quantity
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const inventory = this.createFromForm();
    if (inventory.id !== undefined) {
      this.subscribeToSaveResponse(this.inventoryService.update(inventory));
    } else {
      this.subscribeToSaveResponse(this.inventoryService.create(inventory));
    }
  }

  private createFromForm(): IInventory {
    return {
      ...new Inventory(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      price: this.editForm.get(['price']).value,
      quantity: this.editForm.get(['quantity']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInventory>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
