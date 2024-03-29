import {Owner} from './owner';
import {Sex} from './sex';

export interface Horse {
  id?: number;
  name: string;
  description?: string;
  dateOfBirth: Date;
  sex: Sex;
  owner?: Owner;
  mother?: Horse;
  father?: Horse;
}


export interface HorseSearch {
  name?: string;
  // TODO fill in missing fields
}

export interface HorseFamilyTree {
  id?: number;
  name: string;
  dateOfBirth: Date;
  sex: Sex;
  motherId?: number;
  fatherId?: number;
}
