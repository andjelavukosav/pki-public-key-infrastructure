import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateIntermediateCertificateComponent } from './create-intermediate-certificate.component';

describe('CreateIntermediateCertificateComponent', () => {
  let component: CreateIntermediateCertificateComponent;
  let fixture: ComponentFixture<CreateIntermediateCertificateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateIntermediateCertificateComponent]
    });
    fixture = TestBed.createComponent(CreateIntermediateCertificateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
