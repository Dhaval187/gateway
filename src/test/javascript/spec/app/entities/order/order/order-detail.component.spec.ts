import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GatewayTestModule } from '../../../../test.module';
import { OrderDetailComponent } from 'app/entities/order/order/order-detail.component';
import { Order } from 'app/shared/model/order/order.model';

describe('Component Tests', () => {
  describe('Order Management Detail Component', () => {
    let comp: OrderDetailComponent;
    let fixture: ComponentFixture<OrderDetailComponent>;
    const route = ({ data: of({ order: new Order(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [GatewayTestModule],
        declarations: [OrderDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(OrderDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(OrderDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.order).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
