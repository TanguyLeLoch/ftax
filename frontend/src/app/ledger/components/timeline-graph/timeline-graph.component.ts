import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {TimelineItem} from "../../../core/model";
import {Chart, registerables} from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-timeline-graph',
  templateUrl: './timeline-graph.component.html',
  styleUrl: './timeline-graph.component.scss'
})
export class TimelineGraphComponent implements OnInit{
  @Input() timeline!:TimelineItem[];

  @ViewChild('chartCanvas') chartCanvas!: ElementRef;
  chart: any;

  chartLabels!: string[];
  chartData!: number[];


  ngOnInit(): void {
    this.chartLabels = this.timeline.map(data => new Date(data.dateTime).toLocaleDateString());
    this.chartData = this.timeline.map(item => item.amount);


  }
  ngAfterViewInit() {
    this.createChart(this.chartLabels, this.chartData);
  }
  createChart(labels: string[], data: number[]) {
  this.chart = new Chart(this.chartCanvas.nativeElement, {
    type: 'line',
    data: {
      labels: this.chartLabels,
      datasets: [
        {
          label: 'Amount',
          data: this.chartData,
          borderColor: 'rgb(75, 192, 192)',
          tension: 0.1
        }
      ]
    }
  });
}
}




