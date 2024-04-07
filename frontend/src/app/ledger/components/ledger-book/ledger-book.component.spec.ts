import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LedgerBookComponent } from './ledger-book.component';

describe('LedgerBookComponent', () => {
  let component: LedgerBookComponent;
  let fixture: ComponentFixture<LedgerBookComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LedgerBookComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LedgerBookComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
