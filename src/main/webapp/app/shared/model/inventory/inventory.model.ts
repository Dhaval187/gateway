export interface IInventory {
  id?: number;
  name?: string;
  price?: number;
  quantity?: number;
}

export class Inventory implements IInventory {
  constructor(public id?: number, public name?: string, public price?: number, public quantity?: number) {}
}
