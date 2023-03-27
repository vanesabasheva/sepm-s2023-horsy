import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse} from '../../dto/horse';
import {Owner} from '../../dto/owner';
import {debounceTime} from 'rxjs';
import {HttpParams} from '@angular/common/http';

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  search = false;
  horses: Horse[] = [];
  bannerError: string | null = null;
  searchName = '';
  searchHorseDescription = '';
  searchHorseDateOfBirth = '';
  searchSex = '';
  searchOwner = '';
  error = '';

  constructor(
    private service: HorseService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
  }

  reloadHorses() {
    this.service.getAll()
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }

  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }
  searchHorses() {
    const parameters = new HttpParams()
      .set('name', this.searchName)
      .set('description', this.searchHorseDescription)
      .set('bornBefore', this.searchHorseDateOfBirth)
      .set('sex', this.searchSex)
      .set('ownerName', this.searchOwner);

    this.service.getHorsesByParameters(parameters).pipe(
      debounceTime(600)).subscribe({
      next: data => {
        this.horses = data;
      },
      error: error => {
        console.error('Error fetching horses', error);
        this.notification.error(error.message, 'Could Not Fetch Horses');
      }
    });
  }

  deleteHorse(id: number) {
    this.service.deleteHorse(id).subscribe({
      next: data => {
        this.notification.success(`Horse successfully deleted`);
        this.reloadHorses();
      },
      error: error => {
        console.error(error.message);
        this.showError('Failed to delete horse: ' + error.error.message);
      }
    });
  }

  clearFilter() {
    this.searchSex = '';
    this.searchHorses();
  }

  private showError(message: string) {
    console.error(`Error: ${message}`);
    this.error = message;
  }
}
