import { Component, ChangeDetectionStrategy, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-filter-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './filter-panel.component.html',
  styleUrl: './filter-panel.component.scss',
})
export class FilterPanelComponent {
  @Input() sectors: string[] = [];
  @Output() searchChange = new EventEmitter<string>();
  @Output() sectorChange = new EventEmitter<string>();

  searchQuery = '';
  selectedSector = '';

  onSearch(): void {
    this.searchChange.emit(this.searchQuery);
  }

  onSectorChange(): void {
    this.sectorChange.emit(this.selectedSector);
  }
}
