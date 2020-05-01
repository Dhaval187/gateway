import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IInventory } from 'app/shared/model/inventory/inventory.model';

type EntityResponseType = HttpResponse<IInventory>;
type EntityArrayResponseType = HttpResponse<IInventory[]>;

@Injectable({ providedIn: 'root' })
export class InventoryService {
  public resourceUrl = SERVER_API_URL + 'services/inventory/api/inventories';

  constructor(protected http: HttpClient) {}

  create(inventory: IInventory): Observable<EntityResponseType> {
    return this.http.post<IInventory>(this.resourceUrl, inventory, { observe: 'response' });
  }

  update(inventory: IInventory): Observable<EntityResponseType> {
    return this.http.put<IInventory>(this.resourceUrl, inventory, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IInventory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IInventory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
