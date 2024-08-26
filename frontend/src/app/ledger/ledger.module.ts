import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TimelineGraphComponent} from "./components/timeline-graph/timeline-graph.component";
import {LedgerBookComponent} from "./components/ledger-book/ledger-book.component";
import {FormsModule} from "@angular/forms";
import {BaseChartDirective, provideCharts, withDefaultRegisterables} from "ng2-charts";


@NgModule({
  declarations: [TimelineGraphComponent,
    LedgerBookComponent],
  imports: [
    CommonModule,
    FormsModule,
    BaseChartDirective,
  ],
  providers: [
    provideCharts(withDefaultRegisterables()),
  ],

})
export class LedgerModule { }
