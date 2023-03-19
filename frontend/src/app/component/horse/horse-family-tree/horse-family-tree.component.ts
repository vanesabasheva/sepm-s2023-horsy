import {Component, OnInit} from '@angular/core';
import {HorseFamilyTree} from '../../../dto/horse';
import {Sex} from '../../../dto/sex';
import {HorseService} from '../../../service/horse.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-horse-family-tree',
  templateUrl: './horse-family-tree.component.html',
  styleUrls: ['./horse-family-tree.component.scss']
})
export class HorseFamilyTreeComponent implements OnInit {
  id: number | undefined;
  limit = 5;

  horseCurrent: HorseFamilyTree = {
    name: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
  };
  horses: HorseFamilyTree[] = [
    {
      id: 1,
      name: 'A',
      dateOfBirth: new Date('1999-03-03'),
      sex: Sex.female,
      motherId: 2,
      fatherId: 3,
    },
  ];

  constructor(
    private service: HorseService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    const limit = this.route.snapshot.queryParamMap.get('generations');
    if (limit != null) {
      this.limit = Number(limit);
    }
    if (id != null) {
      this.id = Number(id);
      this.getFamilyTree(this.id, this.limit);
    }
  }

  setGenerations(): void {
    this.router.navigate(['horses/' + this.id + '/familytree'], {
        queryParams: {
          generations: this.limit
        }
      }
    ).then(r => {
      if (!r) {
        this.showError('Router failed');
      } else {
        this.getFamilyTree(Number(this.id), this.limit);
      }
    });
  }

  getById(id: number): HorseFamilyTree | undefined {
    for (const horse of this.horses) {
      if (horse.id === id) {
        return horse;
      }
    }
    return undefined;
  }

  goToHorseDetails(id: number) {
    this.router.navigate(['/horses/' + id]).then(r => {
      if (!r) {
        this.showError('Router failed');
      }
    });
  }

  goToHorseEdit(id: number) {
    this.router.navigate(['/horses/' + id + '/edit']).then(r => {
      if (!r) {
        this.showError('Router failed');
      }
    });
  }

  deleteHorse(id: number) {
    this.service.deleteHorse(id).subscribe({
      next: data => {
        this.reloadHorses();
      },
      error: error => {
        console.error(error.message);
        this.showError('Failed to delete horse: ' + error.error.message);
      }
    });
  }

  private reloadHorses() {
    this.service.getFamilyTree(Number(this.id), this.limit)
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error getting family tree of horse', error);
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
        }
      });
  }

  private getFamilyTree(id: number, limit: number) {
    this.service.getFamilyTree(id, limit).subscribe({
      next: (data: HorseFamilyTree[]) => {
        this.horses = data;
      },
      error: (error: any) => {
        console.error('Error getting family tree', error);
      }
    });
  }

  private showError(message: string) {
    console.error(`Error: ${message}`);
  }
}
