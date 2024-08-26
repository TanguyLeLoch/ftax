import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimelineGraphComponent } from './timeline-graph.component';

describe('TimelineGraphComponent', () => {
  let component: TimelineGraphComponent;
  let fixture: ComponentFixture<TimelineGraphComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TimelineGraphComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TimelineGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
