import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {TimelineItem} from "../../../core/model";
import {Chart, registerables} from 'chart.js';
import 'chartjs-adapter-date-fns';

Chart.register(...registerables);

@Component({
  selector: 'app-timeline-graph',
  templateUrl: './timeline-graph.component.html',
  styleUrl: './timeline-graph.component.scss'
})
export class TimelineGraphComponent implements OnInit{
  @Input() timeline!:TimelineItem[];
  @Input() token!: string;

  @ViewChild('chartCanvas') chartCanvas!: ElementRef;
  chart: any;

  txDate!: Date[];
  balanceAmount!: number[];
  color!: string;


  ngOnInit(): void {
    this.txDate = this.timeline.map(data => new Date(data.dateTime));
    this.balanceAmount = this.timeline.map(item => item.amount);

    this.color = this.stringToColor(this.token);

  }
  ngAfterViewInit() {
    this.createChart();
  }

  createChart() {
  this.chart = new Chart(this.chartCanvas.nativeElement, {
    type: 'line',
    data: {
      labels: this.txDate,
      datasets: [
        {
          label: this.token,
          data: this.balanceAmount,
          borderColor: this.color,
          tension: 0.1
        }
      ]
    },
    options: {
      scales: {
        x: {
          type: 'time',
        },
      }
    }
  });
}

  stringToColor(str: string): string {
    // Hash the string to create a unique value
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }

    // Convert the hash to a hex color
    let color = '#';
    for (let i = 0; i < 3; i++) {
      const value = (hash >> (i * 8)) & 0xFF;
      color += ('00' + value.toString(16)).slice(-2);
    }

    return color;
  }
}




