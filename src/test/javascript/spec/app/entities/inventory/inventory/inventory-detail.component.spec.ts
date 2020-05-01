import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GatewayTestModule } from '../../../../test.module';
import { InventoryDetailComponent } from 'app/entities/inventory/inventory/inventory-detail.component';
import { Inventory } from 'app/shared/model/inventory/inventory.model';

describe('Component Tests', () => {
  describe('Inventory Management Detail Component', () => {
    let comp: InventoryDetailComponent;
    let fixture: ComponentFixture<InventoryDetailComponent>;
    const route = ({ data: of({ inventory: new Inventory(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [GatewayTestModule],
        declarations: [InventoryDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(InventoryDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(InventoryDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.inventory).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
