import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription, Observable } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IOrder } from 'app/shared/model/order/order.model';
import { AccountService } from 'app/core/auth/account.service';
import { OrderService } from './order.service';
import * as fileSaver from 'file-saver';

@Component({
  selector: 'jhi-order',
  templateUrl: './order.component.html'
})
export class OrderComponent implements OnInit, OnDestroy {
  orders: IOrder[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected orderService: OrderService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) { }

  loadAll() {
    this.orderService
      .query()
      .pipe(
        filter((res: HttpResponse<IOrder[]>) => res.ok),
        map((res: HttpResponse<IOrder[]>) => res.body)
      )
      .subscribe(
        (res: IOrder[]) => {
          this.orders = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInOrders();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IOrder) {
    return item.id;
  }

  registerChangeInOrders() {
    this.eventSubscriber = this.eventManager.subscribe('orderListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  exportFile(type: string) {
    this.orderService.export(type).subscribe(
      res => this.downloadFile(res.body, res.headers.get('content-type'),
        res.headers.get('content-disposition').split("filename=")[1], type));
  }

  downloadFile(data: any, contentType, filename, type) {
    const blob = new Blob([data], { type: contentType + '; charset=utf-8' });
    if(type === 'PRINT'){
      const blobUrl = URL.createObjectURL(blob);
      const iframe = document.createElement('iframe');
      iframe.style.display = 'none';
      iframe.src = blobUrl;
      document.body.appendChild(iframe);
      iframe.contentWindow.print();
    }else{
      fileSaver.saveAs(blob, filename);
      // const url = window.URL.createObjectURL(blob);
      // window.open(url, filename);
    }
  }
}
