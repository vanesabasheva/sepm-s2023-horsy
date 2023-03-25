import {Component, OnInit} from '@angular/core';
import {Horse} from '../../../dto/horse';
import {Sex} from '../../../dto/sex';
import {HorseService} from '../../../service/horse.service';
import {OwnerService} from '../../../service/owner.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Owner} from '../../../dto/owner';

@Component({
  selector: 'app-horse-detail',
  templateUrl: './horse-detail.component.html',
  styleUrls: ['./horse-detail.component.scss']
})
export class HorseDetailComponent implements OnInit {
  horse: Horse = {
    name: '',
    description: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
  };

  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
    this.route.paramMap.subscribe(() => {
      this.ngOnInit();
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id != null && !isNaN(Number(id))) {
      this.getHorse(Number(id));
    } else {
      this.router.navigate(['**']);
    }
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  deleteHorse(id: number) {
    this.service.deleteHorse(id).subscribe({
      next: data => {
        this.notification.success(`Horse ${this.horse.name} successfully deleted`);
        this.router.navigate(['/horses'], {state: {del: 'true'}}).then(r => {
          if (!r) {
            this.showError('Router failed');
          }
        });
      },
      error: error => {
        console.error(error.message);
        this.showError('Failed to delete horse: ' + error.error.message);
      }
    });
  }

  goToHorse(parent: Horse) {
    this.router.navigate(['/horses/' + parent.id]).then(r => {
      if (!r) {
        this.showError('Router failed');
      } else {
        this.getHorse(parent.id ?? 0);
      }
    });
  }

  private getHorse(id: number) {
    this.service.getByID(id).subscribe({
      next: (data: Horse) => {
        this.horse = data;
      },
      error: (error: any) => {
        console.log('Error getting details of horse', error);
      }
    });
  }

  private showError(message: string) {
    console.error(`Error: ${message}`);
  }

}
