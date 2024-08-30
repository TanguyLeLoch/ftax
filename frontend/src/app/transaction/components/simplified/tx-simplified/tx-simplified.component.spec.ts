import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TxSimplifiedComponent } from './tx-simplified.component';

describe('TxSimplifiedComponent', () => {
  let component: TxSimplifiedComponent;
  let fixture: ComponentFixture<TxSimplifiedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TxSimplifiedComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TxSimplifiedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
