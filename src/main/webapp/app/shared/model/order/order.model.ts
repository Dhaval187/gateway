export interface IOrder {
  id?: number;
  name?: string;
  amount?: number;
  inventory?: number;
  inProgress?: boolean;
}

export class Order implements IOrder {
  constructor(
    public id?: number,
    public name?: string,
    public amount?: number,
    public inventory?: number,
    public inProgress?: boolean
  ) { }
}
