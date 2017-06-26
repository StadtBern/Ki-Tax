// declare function require(string: string): any;

// Extra variables that live on Global that will be replaced by webpack DefinePlugin
declare let ENV: string;
declare let VERSION: string;
declare let BUILDTSTAMP: string;
declare let HMR: boolean;
interface GlobalEnvironment {
  ENV: any;
  HMR: any;
  VERSION: string;
  BUILDTSTAMP: string;
}

interface WebpackModule {
  hot: {
    data?: any,
    idle: any,
    accept(dependencies?: string | string[], callback?: (updatedDependencies?: any) => void): void;
    decline(dependencies?: string | string[]): void;
    dispose(callback?: (data?: any) => void): void;
    addDisposeHandler(callback?: (data?: any) => void): void;
    removeDisposeHandler(callback?: (data?: any) => void): void;
    check(autoApply?: any, callback?: (err?: Error, outdatedModules?: any[]) => void): void;
    apply(options?: any, callback?: (err?: Error, outdatedModules?: any[]) => void): void;
    status(callback?: (status?: string) => void): void | string;
    removeStatusHandler(callback?: (status?: string) => void): void;
  };
}

interface WebpackRequire {
  context(file: string, flag?: boolean, exp?: RegExp): any;
}

interface ErrorStackTraceLimit {
  stackTraceLimit: number;
}
