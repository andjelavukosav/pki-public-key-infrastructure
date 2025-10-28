import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingCsrListComponent } from './pending-csr-list.component';

describe('PendingCsrListComponent', () => {
  let component: PendingCsrListComponent;
  let fixture: ComponentFixture<PendingCsrListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PendingCsrListComponent]
    });
    fixture = TestBed.createComponent(PendingCsrListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
