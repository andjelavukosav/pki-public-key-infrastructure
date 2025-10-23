import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CaCertificateListComponent } from './ca-certificate-list.component';

describe('CaCertificateListComponent', () => {
  let component: CaCertificateListComponent;
  let fixture: ComponentFixture<CaCertificateListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CaCertificateListComponent]
    });
    fixture = TestBed.createComponent(CaCertificateListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
