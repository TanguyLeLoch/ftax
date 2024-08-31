import {Component, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";
import {map, Observable, startWith, tap} from "rxjs";
import {Token, TokenControllerService} from "../../model";

@Component({
  selector: 'app-token-selector',
  standalone: true,
  imports: [],
  templateUrl: './token-selector.component.html',
  styleUrl: './token-selector.component.scss'
})
export class TokenSelectorComponent  implements OnInit {
  myControl = new FormControl<string | Token>('');
  options!: Token[]
  filteredOptions!: Observable<Token[]>;

  constructor(private service: TokenControllerService) {}


  ngOnInit() {
    this.service.getAllTokens().pipe(
      tap((tokens: Token[]) => {
        this.options = tokens
      })
    ).subscribe();
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : value?.name;
        return name ? this._filter(name as string) : this.options.slice();
      }),
    );
  }

  displayFn(token: Token): string {
    return token && token.name ? token.name : '';
  }

  private _filter(name: string): Token[] {
    const filterValue = name.toLowerCase();
    return this.options.filter(option => option.name.toLowerCase().includes(filterValue));
  }
}
