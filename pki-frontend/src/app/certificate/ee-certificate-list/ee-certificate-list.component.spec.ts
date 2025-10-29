import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EeCertificateListComponent } from './ee-certificate-list.component';

describe('EeCertificateListComponent', () => {
  let component: EeCertificateListComponent;
  let fixture: ComponentFixture<EeCertificateListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EeCertificateListComponent]
    });
    fixture = TestBed.createComponent(EeCertificateListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
