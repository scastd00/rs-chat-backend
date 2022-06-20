import { combineReducers } from '@reduxjs/toolkit';
import { themeReducer } from './themeReducer';
import { userReducer } from './userReducer';

const rootReducer = combineReducers({
  user: userReducer,
  theme: themeReducer,
});

export default rootReducer;
