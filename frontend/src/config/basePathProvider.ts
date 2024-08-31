import {environment} from '../environments/environment';
import {BASE_PATH} from "../app/core/model";


// Provide the base path value based on the environment
export const basePathProvider = {
  provide: BASE_PATH,
  useValue: environment.basePath
};
