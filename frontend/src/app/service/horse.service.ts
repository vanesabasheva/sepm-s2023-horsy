import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse} from '../dto/horse';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) {
  }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri);
  }


  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }

  /**
   * Get a horse specified with the given id
   *
   * @param id the id of the horse to get
   * @return an Observable for the horse to get
   */
  getByID(id: number): Observable<Horse> {
    return this.http.get<Horse>(baseUri + '/' + id);
  }

  /**
   * Edit an existing horse in the system
   *
   * @param id the id of the horse to be edited
   * @param horse the horse with the newly added changes
   * @return an Observable for the edited horse
   */
  edit(id: number, horse: Horse): Observable<Horse>{
    return this.http.put<Horse>(
      baseUri + '/' + id,
      horse
    );
  }

  /**
   * Delete a horse from the system
   *
   * @param id the id of the horse to be deleted
   * @return an Observable of the deleted horse
   */
  deleteHorse(id: number): Observable<Horse> {
    return this.http.delete<Horse>(baseUri + '/' + id);

  }

  /**
   * Get a list of specific female horses stored in the system
   *
   * @param name the name/part of the name of the mother horse to get
   * @param limitTo the number of horse suggestions to be showed
   * @return an Observable list of found horses
   */
  searchByMotherName(name: string, limitTo: number): Observable<Horse[]> {
    const params = new HttpParams()
      .set('name', name)
      .set('sex', 'FEMALE')
      .set('limit', limitTo);
    return this.http.get<Horse[]>(baseUri, {params});
  }

  /**
   * Get a list of specific male horses stored in the system
   *
   * @param name the name/part of the name of the horse to get
   * @param limitTo the number of horse suggestions to be showed
   * @return an Observable list of found horses
   */
  searchByFatherName(name: string, limitTo: number): Observable<Horse[]> {
    const params = new HttpParams()
      .set('name', name)
      .set('sex', 'MALE')
      .set('limit', limitTo);
    return this.http.get<Horse[]>(baseUri, {params});
  }

  /**
   * Get all horses stored in the system matching the parameters
   *
   * @return observable list of the found horses.
   */
  getHorsesByParameters(params: HttpParams): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri, {params});
  }
}
