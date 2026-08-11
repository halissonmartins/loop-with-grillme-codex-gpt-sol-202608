import { TestBed } from '@angular/core/testing';

import { App } from './app';

describe('App', () => {
  it('identifica a aplicação na página inicial', async () => {
    await TestBed.configureTestingModule({ imports: [App] }).compileComponents();

    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('h1')?.textContent).toContain(
      'Relatórios agendados',
    );
  });
});
