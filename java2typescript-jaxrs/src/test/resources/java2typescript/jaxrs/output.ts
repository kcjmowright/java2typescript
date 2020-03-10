import { IAggregationResultDTO } from './i-aggregation-result-dto';
import { IPagingSortDTO } from './i-paging-sort-dto';
import { IPreTestResource } from './i-pre-test-resource';
import { ISearchResultDTO } from './i-search-result-dto';
import { ITestDTO } from './i-test-dto';
import { PreTestResource } from './pre-test-resource';
import { ctx } from './ctx';

export {
  IAggregationResultDTO,
  IPagingSortDTO,
  IPreTestResource,
  ISearchResultDTO,
  ITestDTO,
  PreTestResource,
  ctx
};

import { InjectionToken } from '@angular/core';

export const ctx = new InjectionToken<String>('URL Context token', {
  providedIn: 'root',
  factory: () => 'ctx'
});

export interface IAggregationResultDTO {
  _in?: number;
  timestamp?: string;
  value?: string;
}
export interface IPagingSortDTO {
  page?: number;
  pageSize?: number;
  sort?: string;
}
import { IAggregationResultDTO } from './i-aggregation-result-dto';
import { IPagingSortDTO } from './i-paging-sort-dto';
import { ISearchResultDTO } from './i-search-result-dto';
import { ITestDTO } from './i-test-dto';
import { Observable } from 'rxjs';

export interface IPreTestResource {
  create(_in?: number, aggregationResultDTO?: IAggregationResultDTO): Observable<IAggregationResultDTO>;
  delete(_in?: number, id?: string): Observable<any>;
  findAll(_in?: number, testDTO?: ITestDTO, includeParts?: boolean, includeCounts?: boolean): Observable<ISearchResultDTO<any>>;
  findEvents(_in?: number, testDTO?: ITestDTO, pagingSortDTO?: IPagingSortDTO): Observable<IAggregationResultDTO>;
  in(_in?: number): Observable<any>;
  update(_in?: number, id?: string, testDTO?: ITestDTO): Observable<any>;
}
export interface ISearchResultDTO<T> {
  count?: number;
  page?: number;
  pageSize?: number;
  results?: T[];
  total?: number;
}
export interface ITestDTO {
  num?: number;
  nums?: number[];
  test?: string;
}
import { Inject } from '@angular/core';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ctx } from './ctx';
import { IPreTestResource } from './i-pre-test-resource';
import { IAggregationResultDTO } from './i-aggregation-result-dto';
import { IPagingSortDTO } from './i-paging-sort-dto';
import { ISearchResultDTO } from './i-search-result-dto';
import { ITestDTO } from './i-test-dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PreTestResource implements IPreTestResource {
  constructor(private http: HttpClient, @Inject(ctx) private context: string) {}

  public create(_in?: number, aggregationResultDTO?: IAggregationResultDTO): Observable<IAggregationResultDTO> {
    const pathParams = {
      _in: encodeURIComponent('' + _in)
    };
    let params: any = {};
    const urlTmpl = `${this.context}/domain/${encodeURIComponent(pathParams._in)}/test`;

    return this.http.post<IAggregationResultDTO>(urlTmpl, aggregationResultDTO, {
      params: params,
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json'
      }
    });
  }

  public delete(_in?: number, id?: string): any {
    const pathParams = {
      _in: encodeURIComponent('' + _in),
      id: encodeURIComponent('' + id)
    };
    let params: any = {};
    const urlTmpl = `${this.context}/domain/${encodeURIComponent(pathParams._in)}/test/${pathParams.id}`;

    return this.http.delete(urlTmpl, {
      params: params,
      responseType: 'json'
    });
  }

  public findAll(_in?: number, testDTO?: ITestDTO, includeParts?: boolean, includeCounts?: boolean): Observable<ISearchResultDTO<any>> {
    const pathParams = {
      _in: encodeURIComponent('' + _in)
    };
    let params: any = {};
    if (includeParts !== undefined) {
      params.includeParts = includeParts;
    }
    if (includeCounts !== undefined) {
      params.includeCounts = includeCounts;
    }
    for ( const key in testDTO ) {
      if (key !== undefined && key !== null && testDTO[key] !== undefined) {
        params[key] = testDTO[key];
      }
    }
    const urlTmpl = `${this.context}/domain/${encodeURIComponent(pathParams._in)}/test/all`;

    return this.http.get<ISearchResultDTO<any>>(urlTmpl, {
      params: params,
      headers: {
        Accept: 'application/json'
      },
      responseType: 'json'
    });
  }

  public findEvents(_in?: number, testDTO?: ITestDTO, pagingSortDTO?: IPagingSortDTO): Observable<IAggregationResultDTO> {
    const pathParams = {
      _in: encodeURIComponent('' + _in)
    };
    let params: any = {};
    for ( const key in testDTO ) {
      if (key !== undefined && key !== null && testDTO[key] !== undefined) {
        params[key] = testDTO[key];
      }
    }
    for ( const key in pagingSortDTO ) {
      if (key !== undefined && key !== null && pagingSortDTO[key] !== undefined) {
        params[key] = pagingSortDTO[key];
      }
    }
    const urlTmpl = `${this.context}/domain/${encodeURIComponent(pathParams._in)}/test`;

    return this.http.get<IAggregationResultDTO>(urlTmpl, {
      params: params,
      headers: {
        Accept: 'application/json'
      },
      responseType: 'json'
    });
  }

  public in(_in?: number): Observable<any> {
    const pathParams = {
      _in: encodeURIComponent('' + _in)
    };
    let params: any = {};
    const urlTmpl = `${this.context}/domain/${encodeURIComponent(pathParams._in)}/test/in`;

    return this.http.get<any>(urlTmpl, {
      params: params,
      headers: {
        Accept: 'application/json'
      },
      responseType: 'json'
    });
  }

  public update(_in?: number, id?: string, testDTO?: ITestDTO): Observable<any> {
    const pathParams = {
      _in: encodeURIComponent('' + _in),
      id: encodeURIComponent('' + id)
    };
    let params: any = {};
    const urlTmpl = `${this.context}/domain/${encodeURIComponent(pathParams._in)}/test/${pathParams.id}`;

    return this.http.put<any>(urlTmpl, testDTO, {
      params: params,
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json'
      }
    });
  }

}

import * as _function from './_function';

export {
  _function
};
import * as java2typescript from './java2typescript';

export {
  java2typescript
};
