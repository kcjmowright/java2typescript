import * as java from './java';
import * as java2typescript from './java2typescript';

export {
  java,
  java2typescript
};
import * as jackson from './jackson';

export {
  jackson
};
import * as module from './module';

export {
  module
};
import { IStringClass } from './i-string-class';
import { ITestClass } from './i-test-class';

export {
  IStringClass,
  ITestClass
};


java2typescript.jackson.module.IStringClass
export interface IStringClass {
  someField?: string;
}

java2typescript.jackson.module.ITestClass
import { IOptional } from '../../../java/util/i-optional';

export interface ITestClass {
  _Boolean?: boolean;
  _String?: string;
  _boolean?: boolean;
  _enum?: any;
  _float?: number;
  _int?: number;
  _optionalInteger?: IOptional<any>;
  booleanCollection?: boolean[];
  map?: { [key: string ]: boolean;};
  recursive?: ITestClass;
  recursiveArray?: ITestClass[];
  stringArray?: string[];
  stringArrayList?: string[];
  _test?: string;
  aMethod(param0?: boolean, param1?: string): string;
  test(): string;
}
import * as lang from './lang';
import * as util from './util';

export {
  lang,
  util
};
import * as _function from './_function';
import * as stream from './stream';

export {
  _function,
  stream
};
import { IComparator } from './i-comparator';
import { IDoubleSummaryStatistics } from './i-double-summary-statistics';
import { IIntSummaryStatistics } from './i-int-summary-statistics';
import { ILongSummaryStatistics } from './i-long-summary-statistics';
import { IOfDouble } from './i-of-double';
import { IOfInt } from './i-of-int';
import { IOfLong } from './i-of-long';
import { IOptional } from './i-optional';
import { IOptionalDouble } from './i-optional-double';
import { IOptionalInt } from './i-optional-int';
import { IOptionalLong } from './i-optional-long';

export {
  IComparator,
  IDoubleSummaryStatistics,
  IIntSummaryStatistics,
  ILongSummaryStatistics,
  IOfDouble,
  IOfInt,
  IOfLong,
  IOptional,
  IOptionalDouble,
  IOptionalInt,
  IOptionalLong
};


java.util.IComparator
import { IFunction } from './_function/i-function';
import { IToDoubleFunction } from './_function/i-to-double-function';
import { IToIntFunction } from './_function/i-to-int-function';
import { IToLongFunction } from './_function/i-to-long-function';

export interface IComparator<T> {
  compare(param0?: any, param1?: any): number;
  reversed(): IComparator<any>;
  thenComparing(param0?: IFunction<any, any>, param1?: IComparator<any>): IComparator<any>;
  thenComparing(param0?: IComparator<any>): IComparator<any>;
  thenComparing(param0?: IFunction<any, any>): IComparator<any>;
  thenComparingDouble(param0?: IToDoubleFunction<any>): IComparator<any>;
  thenComparingInt(param0?: IToIntFunction<any>): IComparator<any>;
  thenComparingLong(param0?: IToLongFunction<any>): IComparator<any>;
}

java.util.IDoubleSummaryStatistics

export interface IDoubleSummaryStatistics {
  average?: number;
  count?: number;
  max?: number;
  min?: number;
  sum?: number;
  accept(param0?: number): void;
  combine(param0?: IDoubleSummaryStatistics): void;
}

java.util.IIntSummaryStatistics

export interface IIntSummaryStatistics {
  average?: number;
  count?: number;
  max?: number;
  min?: number;
  sum?: number;
  accept(param0?: number): void;
  combine(param0?: IIntSummaryStatistics): void;
}

java.util.ILongSummaryStatistics

export interface ILongSummaryStatistics {
  average?: number;
  count?: number;
  max?: number;
  min?: number;
  sum?: number;
  accept(param0?: number): void;
  accept(param0?: number): void;
  combine(param0?: ILongSummaryStatistics): void;
}

java.util.IOfDouble
import { IComparator } from './i-comparator';
import { IConsumer } from './_function/i-consumer';
import { IDoubleConsumer } from './_function/i-double-consumer';

export interface IOfDouble {
  comparator?: IComparator<any>;
  exactSizeIfKnown?: number;
  forEachRemaining(param0?: IConsumer<any>): void;
  forEachRemaining(param0?: IDoubleConsumer): void;
  forEachRemaining(param0?: any): void;
  tryAdvance(param0?: IConsumer<any>): boolean;
  tryAdvance(param0?: IDoubleConsumer): boolean;
  tryAdvance(param0?: any): boolean;
  trySplit(): IOfDouble;
}

java.util.IOfInt
import { IComparator } from './i-comparator';
import { IConsumer } from './_function/i-consumer';
import { IIntConsumer } from './_function/i-int-consumer';

export interface IOfInt {
  comparator?: IComparator<any>;
  exactSizeIfKnown?: number;
  forEachRemaining(param0?: IConsumer<any>): void;
  forEachRemaining(param0?: IIntConsumer): void;
  forEachRemaining(param0?: any): void;
  tryAdvance(param0?: IConsumer<any>): boolean;
  tryAdvance(param0?: IIntConsumer): boolean;
  tryAdvance(param0?: any): boolean;
  trySplit(): IOfInt;
}

java.util.IOfLong
import { IComparator } from './i-comparator';
import { IConsumer } from './_function/i-consumer';
import { ILongConsumer } from './_function/i-long-consumer';

export interface IOfLong {
  comparator?: IComparator<any>;
  exactSizeIfKnown?: number;
  forEachRemaining(param0?: IConsumer<any>): void;
  forEachRemaining(param0?: ILongConsumer): void;
  forEachRemaining(param0?: any): void;
  tryAdvance(param0?: IConsumer<any>): boolean;
  tryAdvance(param0?: ILongConsumer): boolean;
  tryAdvance(param0?: any): boolean;
  trySplit(): IOfLong;
}

java.util.IOptional
import { IConsumer } from './_function/i-consumer';
import { IFunction } from './_function/i-function';
import { IPredicate } from './_function/i-predicate';
import { IRunnable } from '../lang/i-runnable';
import { IStream } from './stream/i-stream';
import { ISupplier } from './_function/i-supplier';

export interface IOptional<T> {
  empty?: boolean;
  present?: boolean;
  filter(param0?: IPredicate<any>): IOptional<any>;
  flatMap(param0?: IFunction<any, any>): IOptional<any>;
  get(): any;
  ifPresent(param0?: IConsumer<any>): void;
  ifPresentOrElse(param0?: IConsumer<any>, param1?: IRunnable): void;
  map(param0?: IFunction<any, any>): IOptional<any>;
  or(param0?: ISupplier<any>): IOptional<any>;
  orElse(param0?: any): any;
  orElseGet(param0?: ISupplier<any>): any;
  orElseThrow(param0?: ISupplier<any>): any;
  orElseThrow(): any;
  stream(): IStream<any>;
}

java.util.IOptionalDouble
import { IDoubleConsumer } from './_function/i-double-consumer';
import { IDoubleStream } from './stream/i-double-stream';
import { IDoubleSupplier } from './_function/i-double-supplier';
import { IRunnable } from '../lang/i-runnable';
import { ISupplier } from './_function/i-supplier';

export interface IOptionalDouble {
  asDouble?: number;
  empty?: boolean;
  present?: boolean;
  ifPresent(param0?: IDoubleConsumer): void;
  ifPresentOrElse(param0?: IDoubleConsumer, param1?: IRunnable): void;
  orElse(param0?: number): number;
  orElseGet(param0?: IDoubleSupplier): number;
  orElseThrow(param0?: ISupplier<any>): number;
  orElseThrow(): number;
  stream(): IDoubleStream;
}

java.util.IOptionalInt
import { IIntConsumer } from './_function/i-int-consumer';
import { IIntStream } from './stream/i-int-stream';
import { IIntSupplier } from './_function/i-int-supplier';
import { IRunnable } from '../lang/i-runnable';
import { ISupplier } from './_function/i-supplier';

export interface IOptionalInt {
  asInt?: number;
  empty?: boolean;
  present?: boolean;
  ifPresent(param0?: IIntConsumer): void;
  ifPresentOrElse(param0?: IIntConsumer, param1?: IRunnable): void;
  orElse(param0?: number): number;
  orElseGet(param0?: IIntSupplier): number;
  orElseThrow(param0?: ISupplier<any>): number;
  orElseThrow(): number;
  stream(): IIntStream;
}

java.util.IOptionalLong
import { ILongConsumer } from './_function/i-long-consumer';
import { ILongStream } from './stream/i-long-stream';
import { ILongSupplier } from './_function/i-long-supplier';
import { IRunnable } from '../lang/i-runnable';
import { ISupplier } from './_function/i-supplier';

export interface IOptionalLong {
  asLong?: number;
  empty?: boolean;
  present?: boolean;
  ifPresent(param0?: ILongConsumer): void;
  ifPresentOrElse(param0?: ILongConsumer, param1?: IRunnable): void;
  orElse(param0?: number): number;
  orElseGet(param0?: ILongSupplier): number;
  orElseThrow(param0?: ISupplier<any>): number;
  orElseThrow(): number;
  stream(): ILongStream;
}
import { IBiConsumer } from './i-bi-consumer';
import { IBiFunction } from './i-bi-function';
import { IBinaryOperator } from './i-binary-operator';
import { IConsumer } from './i-consumer';
import { IDoubleBinaryOperator } from './i-double-binary-operator';
import { IDoubleConsumer } from './i-double-consumer';
import { IDoubleFunction } from './i-double-function';
import { IDoublePredicate } from './i-double-predicate';
import { IDoubleSupplier } from './i-double-supplier';
import { IDoubleToIntFunction } from './i-double-to-int-function';
import { IDoubleToLongFunction } from './i-double-to-long-function';
import { IDoubleUnaryOperator } from './i-double-unary-operator';
import { IFunction } from './i-function';
import { IIntBinaryOperator } from './i-int-binary-operator';
import { IIntConsumer } from './i-int-consumer';
import { IIntFunction } from './i-int-function';
import { IIntPredicate } from './i-int-predicate';
import { IIntSupplier } from './i-int-supplier';
import { IIntToDoubleFunction } from './i-int-to-double-function';
import { IIntToLongFunction } from './i-int-to-long-function';
import { IIntUnaryOperator } from './i-int-unary-operator';
import { ILongBinaryOperator } from './i-long-binary-operator';
import { ILongConsumer } from './i-long-consumer';
import { ILongFunction } from './i-long-function';
import { ILongPredicate } from './i-long-predicate';
import { ILongSupplier } from './i-long-supplier';
import { ILongToDoubleFunction } from './i-long-to-double-function';
import { ILongToIntFunction } from './i-long-to-int-function';
import { ILongUnaryOperator } from './i-long-unary-operator';
import { IObjDoubleConsumer } from './i-obj-double-consumer';
import { IObjIntConsumer } from './i-obj-int-consumer';
import { IObjLongConsumer } from './i-obj-long-consumer';
import { IPredicate } from './i-predicate';
import { ISupplier } from './i-supplier';
import { IToDoubleFunction } from './i-to-double-function';
import { IToIntFunction } from './i-to-int-function';
import { IToLongFunction } from './i-to-long-function';

export {
  IBiConsumer,
  IBiFunction,
  IBinaryOperator,
  IConsumer,
  IDoubleBinaryOperator,
  IDoubleConsumer,
  IDoubleFunction,
  IDoublePredicate,
  IDoubleSupplier,
  IDoubleToIntFunction,
  IDoubleToLongFunction,
  IDoubleUnaryOperator,
  IFunction,
  IIntBinaryOperator,
  IIntConsumer,
  IIntFunction,
  IIntPredicate,
  IIntSupplier,
  IIntToDoubleFunction,
  IIntToLongFunction,
  IIntUnaryOperator,
  ILongBinaryOperator,
  ILongConsumer,
  ILongFunction,
  ILongPredicate,
  ILongSupplier,
  ILongToDoubleFunction,
  ILongToIntFunction,
  ILongUnaryOperator,
  IObjDoubleConsumer,
  IObjIntConsumer,
  IObjLongConsumer,
  IPredicate,
  ISupplier,
  IToDoubleFunction,
  IToIntFunction,
  IToLongFunction
};


java.util._function.IBiConsumer

export interface IBiConsumer<T, U> {
  accept(param0?: any, param1?: any): void;
  andThen(param0?: IBiConsumer<any, any>): IBiConsumer<any, any>;
}

java.util._function.IBiFunction
import { IFunction } from './i-function';

export interface IBiFunction<T, U, R> {
  andThen(param0?: IFunction<any, any>): IBiFunction<any, any, any>;
  apply(param0?: any, param1?: any): any;
}

java.util._function.IBinaryOperator
export interface IBinaryOperator<T> {
}

java.util._function.IConsumer

export interface IConsumer<T> {
  accept(param0?: any): void;
  andThen(param0?: IConsumer<any>): IConsumer<any>;
}

java.util._function.IDoubleBinaryOperator
export interface IDoubleBinaryOperator {
  applyAsDouble(param0?: number, param1?: number): number;
}

java.util._function.IDoubleConsumer

export interface IDoubleConsumer {
  accept(param0?: number): void;
  andThen(param0?: IDoubleConsumer): IDoubleConsumer;
}

java.util._function.IDoubleFunction
export interface IDoubleFunction<R> {
  apply(param0?: number): any;
}

java.util._function.IDoublePredicate

export interface IDoublePredicate {
  and(param0?: IDoublePredicate): IDoublePredicate;
  negate(): IDoublePredicate;
  or(param0?: IDoublePredicate): IDoublePredicate;
  test(param0?: number): boolean;
}

java.util._function.IDoubleSupplier
export interface IDoubleSupplier {
  asDouble?: number;
}

java.util._function.IDoubleToIntFunction
export interface IDoubleToIntFunction {
  applyAsInt(param0?: number): number;
}

java.util._function.IDoubleToLongFunction
export interface IDoubleToLongFunction {
  applyAsLong(param0?: number): number;
}

java.util._function.IDoubleUnaryOperator

export interface IDoubleUnaryOperator {
  andThen(param0?: IDoubleUnaryOperator): IDoubleUnaryOperator;
  applyAsDouble(param0?: number): number;
  compose(param0?: IDoubleUnaryOperator): IDoubleUnaryOperator;
}

java.util._function.IFunction

export interface IFunction<T, R> {
  andThen(param0?: IFunction<any, any>): IFunction<any, any>;
  apply(param0?: any): any;
  compose(param0?: IFunction<any, any>): IFunction<any, any>;
}

java.util._function.IIntBinaryOperator
export interface IIntBinaryOperator {
  applyAsInt(param0?: number, param1?: number): number;
}

java.util._function.IIntConsumer

export interface IIntConsumer {
  accept(param0?: number): void;
  andThen(param0?: IIntConsumer): IIntConsumer;
}

java.util._function.IIntFunction
export interface IIntFunction<R> {
  apply(param0?: number): any;
}

java.util._function.IIntPredicate

export interface IIntPredicate {
  and(param0?: IIntPredicate): IIntPredicate;
  negate(): IIntPredicate;
  or(param0?: IIntPredicate): IIntPredicate;
  test(param0?: number): boolean;
}

java.util._function.IIntSupplier
export interface IIntSupplier {
  asInt?: number;
}

java.util._function.IIntToDoubleFunction
export interface IIntToDoubleFunction {
  applyAsDouble(param0?: number): number;
}

java.util._function.IIntToLongFunction
export interface IIntToLongFunction {
  applyAsLong(param0?: number): number;
}

java.util._function.IIntUnaryOperator

export interface IIntUnaryOperator {
  andThen(param0?: IIntUnaryOperator): IIntUnaryOperator;
  applyAsInt(param0?: number): number;
  compose(param0?: IIntUnaryOperator): IIntUnaryOperator;
}

java.util._function.ILongBinaryOperator
export interface ILongBinaryOperator {
  applyAsLong(param0?: number, param1?: number): number;
}

java.util._function.ILongConsumer

export interface ILongConsumer {
  accept(param0?: number): void;
  andThen(param0?: ILongConsumer): ILongConsumer;
}

java.util._function.ILongFunction
export interface ILongFunction<R> {
  apply(param0?: number): any;
}

java.util._function.ILongPredicate

export interface ILongPredicate {
  and(param0?: ILongPredicate): ILongPredicate;
  negate(): ILongPredicate;
  or(param0?: ILongPredicate): ILongPredicate;
  test(param0?: number): boolean;
}

java.util._function.ILongSupplier
export interface ILongSupplier {
  asLong?: number;
}

java.util._function.ILongToDoubleFunction
export interface ILongToDoubleFunction {
  applyAsDouble(param0?: number): number;
}

java.util._function.ILongToIntFunction
export interface ILongToIntFunction {
  applyAsInt(param0?: number): number;
}

java.util._function.ILongUnaryOperator

export interface ILongUnaryOperator {
  andThen(param0?: ILongUnaryOperator): ILongUnaryOperator;
  applyAsLong(param0?: number): number;
  compose(param0?: ILongUnaryOperator): ILongUnaryOperator;
}

java.util._function.IObjDoubleConsumer
export interface IObjDoubleConsumer<T> {
  accept(param0?: any, param1?: number): void;
}

java.util._function.IObjIntConsumer
export interface IObjIntConsumer<T> {
  accept(param0?: any, param1?: number): void;
}

java.util._function.IObjLongConsumer
export interface IObjLongConsumer<T> {
  accept(param0?: any, param1?: number): void;
}

java.util._function.IPredicate

export interface IPredicate<T> {
  and(param0?: IPredicate<any>): IPredicate<any>;
  negate(): IPredicate<any>;
  or(param0?: IPredicate<any>): IPredicate<any>;
  test(param0?: any): boolean;
}

java.util._function.ISupplier
export interface ISupplier<T> {
  get(): any;
}

java.util._function.IToDoubleFunction
export interface IToDoubleFunction<T> {
  applyAsDouble(param0?: any): number;
}

java.util._function.IToIntFunction
export interface IToIntFunction<T> {
  applyAsInt(param0?: any): number;
}

java.util._function.IToLongFunction
export interface IToLongFunction<T> {
  applyAsLong(param0?: any): number;
}
import { IDoubleStream } from './i-double-stream';
import { IIntStream } from './i-int-stream';
import { ILongStream } from './i-long-stream';
import { IStream } from './i-stream';

export {
  IDoubleStream,
  IIntStream,
  ILongStream,
  IStream
};


java.util.stream.IDoubleStream
import { IBiConsumer } from '../_function/i-bi-consumer';
import { IDoubleBinaryOperator } from '../_function/i-double-binary-operator';
import { IDoubleConsumer } from '../_function/i-double-consumer';
import { IDoubleFunction } from '../_function/i-double-function';
import { IDoublePredicate } from '../_function/i-double-predicate';
import { IDoubleSummaryStatistics } from '../i-double-summary-statistics';
import { IDoubleToIntFunction } from '../_function/i-double-to-int-function';
import { IDoubleToLongFunction } from '../_function/i-double-to-long-function';
import { IDoubleUnaryOperator } from '../_function/i-double-unary-operator';
import { IIntStream } from './i-int-stream';
import { ILongStream } from './i-long-stream';
import { IObjDoubleConsumer } from '../_function/i-obj-double-consumer';
import { IOfDouble } from '../i-of-double';
import { IOptionalDouble } from '../i-optional-double';
import { IStream } from './i-stream';
import { ISupplier } from '../_function/i-supplier';

export interface IDoubleStream {
  _parallel?: boolean;
  allMatch(param0?: IDoublePredicate): boolean;
  anyMatch(param0?: IDoublePredicate): boolean;
  average(): IOptionalDouble;
  boxed(): IStream<any>;
  collect(param0?: ISupplier<any>, param1?: IObjDoubleConsumer<any>, param2?: IBiConsumer<any, any>): any;
  count(): number;
  distinct(): IDoubleStream;
  dropWhile(param0?: IDoublePredicate): IDoubleStream;
  filter(param0?: IDoublePredicate): IDoubleStream;
  findAny(): IOptionalDouble;
  findFirst(): IOptionalDouble;
  flatMap(param0?: IDoubleFunction<any>): IDoubleStream;
  forEach(param0?: IDoubleConsumer): void;
  forEachOrdered(param0?: IDoubleConsumer): void;
  iterator(): number[];
  limit(param0?: number): IDoubleStream;
  map(param0?: IDoubleUnaryOperator): IDoubleStream;
  mapToInt(param0?: IDoubleToIntFunction): IIntStream;
  mapToLong(param0?: IDoubleToLongFunction): ILongStream;
  mapToObj(param0?: IDoubleFunction<any>): IStream<any>;
  max(): IOptionalDouble;
  min(): IOptionalDouble;
  noneMatch(param0?: IDoublePredicate): boolean;
  parallel(): IDoubleStream;
  peek(param0?: IDoubleConsumer): IDoubleStream;
  reduce(param0?: number, param1?: IDoubleBinaryOperator): number;
  reduce(param0?: IDoubleBinaryOperator): IOptionalDouble;
  sequential(): IDoubleStream;
  skip(param0?: number): IDoubleStream;
  sorted(): IDoubleStream;
  spliterator(): IOfDouble;
  sum(): number;
  summaryStatistics(): IDoubleSummaryStatistics;
  takeWhile(param0?: IDoublePredicate): IDoubleStream;
  toArray(): number[];
}

java.util.stream.IIntStream
import { IBiConsumer } from '../_function/i-bi-consumer';
import { IDoubleStream } from './i-double-stream';
import { IIntBinaryOperator } from '../_function/i-int-binary-operator';
import { IIntConsumer } from '../_function/i-int-consumer';
import { IIntFunction } from '../_function/i-int-function';
import { IIntPredicate } from '../_function/i-int-predicate';
import { IIntSummaryStatistics } from '../i-int-summary-statistics';
import { IIntToDoubleFunction } from '../_function/i-int-to-double-function';
import { IIntToLongFunction } from '../_function/i-int-to-long-function';
import { IIntUnaryOperator } from '../_function/i-int-unary-operator';
import { ILongStream } from './i-long-stream';
import { IObjIntConsumer } from '../_function/i-obj-int-consumer';
import { IOfInt } from '../i-of-int';
import { IOptionalDouble } from '../i-optional-double';
import { IOptionalInt } from '../i-optional-int';
import { IStream } from './i-stream';
import { ISupplier } from '../_function/i-supplier';

export interface IIntStream {
  _parallel?: boolean;
  allMatch(param0?: IIntPredicate): boolean;
  anyMatch(param0?: IIntPredicate): boolean;
  asDoubleStream(): IDoubleStream;
  asLongStream(): ILongStream;
  average(): IOptionalDouble;
  boxed(): IStream<any>;
  collect(param0?: ISupplier<any>, param1?: IObjIntConsumer<any>, param2?: IBiConsumer<any, any>): any;
  count(): number;
  distinct(): IIntStream;
  dropWhile(param0?: IIntPredicate): IIntStream;
  filter(param0?: IIntPredicate): IIntStream;
  findAny(): IOptionalInt;
  findFirst(): IOptionalInt;
  flatMap(param0?: IIntFunction<any>): IIntStream;
  forEach(param0?: IIntConsumer): void;
  forEachOrdered(param0?: IIntConsumer): void;
  iterator(): number[];
  limit(param0?: number): IIntStream;
  map(param0?: IIntUnaryOperator): IIntStream;
  mapToDouble(param0?: IIntToDoubleFunction): IDoubleStream;
  mapToLong(param0?: IIntToLongFunction): ILongStream;
  mapToObj(param0?: IIntFunction<any>): IStream<any>;
  max(): IOptionalInt;
  min(): IOptionalInt;
  noneMatch(param0?: IIntPredicate): boolean;
  parallel(): IIntStream;
  peek(param0?: IIntConsumer): IIntStream;
  reduce(param0?: number, param1?: IIntBinaryOperator): number;
  reduce(param0?: IIntBinaryOperator): IOptionalInt;
  sequential(): IIntStream;
  skip(param0?: number): IIntStream;
  sorted(): IIntStream;
  spliterator(): IOfInt;
  sum(): number;
  summaryStatistics(): IIntSummaryStatistics;
  takeWhile(param0?: IIntPredicate): IIntStream;
  toArray(): number[];
}

java.util.stream.ILongStream
import { IBiConsumer } from '../_function/i-bi-consumer';
import { IDoubleStream } from './i-double-stream';
import { IIntStream } from './i-int-stream';
import { ILongBinaryOperator } from '../_function/i-long-binary-operator';
import { ILongConsumer } from '../_function/i-long-consumer';
import { ILongFunction } from '../_function/i-long-function';
import { ILongPredicate } from '../_function/i-long-predicate';
import { ILongSummaryStatistics } from '../i-long-summary-statistics';
import { ILongToDoubleFunction } from '../_function/i-long-to-double-function';
import { ILongToIntFunction } from '../_function/i-long-to-int-function';
import { ILongUnaryOperator } from '../_function/i-long-unary-operator';
import { IObjLongConsumer } from '../_function/i-obj-long-consumer';
import { IOfLong } from '../i-of-long';
import { IOptionalDouble } from '../i-optional-double';
import { IOptionalLong } from '../i-optional-long';
import { IStream } from './i-stream';
import { ISupplier } from '../_function/i-supplier';

export interface ILongStream {
  _parallel?: boolean;
  allMatch(param0?: ILongPredicate): boolean;
  anyMatch(param0?: ILongPredicate): boolean;
  asDoubleStream(): IDoubleStream;
  average(): IOptionalDouble;
  boxed(): IStream<any>;
  collect(param0?: ISupplier<any>, param1?: IObjLongConsumer<any>, param2?: IBiConsumer<any, any>): any;
  count(): number;
  distinct(): ILongStream;
  dropWhile(param0?: ILongPredicate): ILongStream;
  filter(param0?: ILongPredicate): ILongStream;
  findAny(): IOptionalLong;
  findFirst(): IOptionalLong;
  flatMap(param0?: ILongFunction<any>): ILongStream;
  forEach(param0?: ILongConsumer): void;
  forEachOrdered(param0?: ILongConsumer): void;
  iterator(): number[];
  limit(param0?: number): ILongStream;
  map(param0?: ILongUnaryOperator): ILongStream;
  mapToDouble(param0?: ILongToDoubleFunction): IDoubleStream;
  mapToInt(param0?: ILongToIntFunction): IIntStream;
  mapToObj(param0?: ILongFunction<any>): IStream<any>;
  max(): IOptionalLong;
  min(): IOptionalLong;
  noneMatch(param0?: ILongPredicate): boolean;
  parallel(): ILongStream;
  peek(param0?: ILongConsumer): ILongStream;
  reduce(param0?: number, param1?: ILongBinaryOperator): number;
  reduce(param0?: ILongBinaryOperator): IOptionalLong;
  sequential(): ILongStream;
  skip(param0?: number): ILongStream;
  sorted(): ILongStream;
  spliterator(): IOfLong;
  sum(): number;
  summaryStatistics(): ILongSummaryStatistics;
  takeWhile(param0?: ILongPredicate): ILongStream;
  toArray(): number[];
}

java.util.stream.IStream
import { IBiConsumer } from '../_function/i-bi-consumer';
import { IBiFunction } from '../_function/i-bi-function';
import { IBinaryOperator } from '../_function/i-binary-operator';
import { IComparator } from '../i-comparator';
import { IConsumer } from '../_function/i-consumer';
import { IDoubleStream } from './i-double-stream';
import { IFunction } from '../_function/i-function';
import { IIntFunction } from '../_function/i-int-function';
import { IIntStream } from './i-int-stream';
import { ILongStream } from './i-long-stream';
import { IOptional } from '../i-optional';
import { IPredicate } from '../_function/i-predicate';
import { ISupplier } from '../_function/i-supplier';
import { IToDoubleFunction } from '../_function/i-to-double-function';
import { IToIntFunction } from '../_function/i-to-int-function';
import { IToLongFunction } from '../_function/i-to-long-function';

export interface IStream<T> {
  parallel?: boolean;
  allMatch(param0?: IPredicate<any>): boolean;
  anyMatch(param0?: IPredicate<any>): boolean;
  collect(param0?: ISupplier<any>, param1?: IBiConsumer<any, any>, param2?: IBiConsumer<any, any>): any;
  collect(param0?: any): any;
  count(): number;
  distinct(): IStream<any>;
  dropWhile(param0?: IPredicate<any>): IStream<any>;
  filter(param0?: IPredicate<any>): IStream<any>;
  findAny(): IOptional<any>;
  findFirst(): IOptional<any>;
  flatMap(param0?: IFunction<any, any>): IStream<any>;
  flatMapToDouble(param0?: IFunction<any, any>): IDoubleStream;
  flatMapToInt(param0?: IFunction<any, any>): IIntStream;
  flatMapToLong(param0?: IFunction<any, any>): ILongStream;
  forEach(param0?: IConsumer<any>): void;
  forEachOrdered(param0?: IConsumer<any>): void;
  limit(param0?: number): IStream<any>;
  map(param0?: IFunction<any, any>): IStream<any>;
  mapToDouble(param0?: IToDoubleFunction<any>): IDoubleStream;
  mapToInt(param0?: IToIntFunction<any>): IIntStream;
  mapToLong(param0?: IToLongFunction<any>): ILongStream;
  max(param0?: IComparator<any>): IOptional<any>;
  min(param0?: IComparator<any>): IOptional<any>;
  noneMatch(param0?: IPredicate<any>): boolean;
  peek(param0?: IConsumer<any>): IStream<any>;
  reduce(param0?: any, param1?: IBiFunction<any, any, any>, param2?: IBinaryOperator<any>): any;
  reduce(param0?: any, param1?: IBinaryOperator<any>): any;
  reduce(param0?: IBinaryOperator<any>): IOptional<any>;
  skip(param0?: number): IStream<any>;
  sorted(param0?: IComparator<any>): IStream<any>;
  sorted(): IStream<any>;
  takeWhile(param0?: IPredicate<any>): IStream<any>;
  toArray(param0?: IIntFunction<any>): any[];
  toArray(): any[];
}
import { IRunnable } from './i-runnable';

export {
  IRunnable
};


java.lang.IRunnable
export interface IRunnable {
  run(): void;
}
