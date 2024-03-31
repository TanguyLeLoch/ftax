import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionListHeaderComponent } from './transaction-list-header.component';

describe('TransactionListHeaderComponent', () => {
  let component: TransactionListHeaderComponent;
  let fixture: ComponentFixture<TransactionListHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionListHeaderComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TransactionListHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
