import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CsrRequestComponent } from './csr-request.component';

describe('CsrRequestComponent', () => {
  let component: CsrRequestComponent;
  let fixture: ComponentFixture<CsrRequestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CsrRequestComponent]
    });
    fixture = TestBed.createComponent(CsrRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
